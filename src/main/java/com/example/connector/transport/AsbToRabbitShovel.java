package com.example.connector.transport;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import com.example.connector.config.ServiceBusConfig;
import org.springframework.stereotype.Component;

@Component
public class AsbToRabbitShovel {

    private final ServiceBusConfig serviceBusConfig;
    private final RabbitPublisher rabbitPublisher;

    private ServiceBusProcessorClient processorClient;

    public AsbToRabbitShovel(ServiceBusConfig serviceBusConfig, RabbitPublisher rabbitPublisher) {
        this.serviceBusConfig = serviceBusConfig;
        this.rabbitPublisher = rabbitPublisher;
    }

    public String start() {
        if (processorClient != null && processorClient.isRunning()) {
            return "Shovel is already running.";
        }
        processorClient = createProcessorClient();
        processorClient.start();
        return "Shovel started. Subscribing to topic '" + serviceBusConfig.getTopicName()
                + "' subscription '" + serviceBusConfig.getSubscriptionName()
                + "', forwarding to RabbitMQ. Run 'shovel-stop' to stop.";
    }

    public String stop() {
        if (processorClient == null || !processorClient.isRunning()) {
            return "Shovel is not running.";
        }
        processorClient.stop();
        processorClient.close();
        processorClient = null;
        return "Shovel stopped.";
    }

    private ServiceBusProcessorClient createProcessorClient() {
        return new ServiceBusClientBuilder()
                .connectionString(serviceBusConfig.getConnectionString())
                .processor()
                .topicName(serviceBusConfig.getTopicName())
                .subscriptionName(serviceBusConfig.getSubscriptionName())
                .receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE)
                .processMessage(this::processMessage)
                .processError(this::processError)
                .buildProcessorClient();
    }

    private void processMessage(ServiceBusReceivedMessageContext context) {
        var message = context.getMessage();
        var body = message.getBody() != null ? new String(message.getBody().toBytes()) : "";
        rabbitPublisher.publish(body);
        System.out.println("Shovel forwarded to RabbitMQ: " + body);
    }

    private void processError(ServiceBusErrorContext context) {
        System.err.println("Shovel error: " + context.getException().getMessage());
    }
}
