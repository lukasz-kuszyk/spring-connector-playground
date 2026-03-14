package com.example.connector.transport;

import java.util.concurrent.CountDownLatch;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import com.example.connector.config.ServiceBusConfig;
import org.springframework.stereotype.Component;

@Component
public class ServiceBusConsumer implements MessageConsumer {

    private final ServiceBusConfig serviceBusConfig;

    private ServiceBusProcessorClient processorClient;

    public ServiceBusConsumer(ServiceBusConfig serviceBusConfig) {
        this.serviceBusConfig = serviceBusConfig;
    }

    @Override
    public String start() {
        if (processorClient != null && processorClient.isRunning()) {
            return "Consumer is already running.";
        }
        processorClient = createProcessorClient();
        processorClient.start();
        return "Consumer started. Listening on topic '" + serviceBusConfig.getTopicName()
                + "' subscription '" + serviceBusConfig.getSubscriptionName()
                + "'. Run 'servicebus-consumer-stop' to stop.";
    }

    @Override
    public String stop() {
        if (processorClient == null || !processorClient.isRunning()) {
            return "Consumer is not running.";
        }
        processorClient.stop();
        processorClient.close();
        processorClient = null;
        return "Consumer stopped.";
    }

    @Override
    public String consume() {
        var consumeProcessor = createProcessorClient();
        consumeProcessor.start();
        System.out.println("Consuming from topic '" + serviceBusConfig.getTopicName()
                + "' subscription '" + serviceBusConfig.getSubscriptionName()
                + "'. Press Ctrl+C to stop.");
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            consumeProcessor.stop();
            consumeProcessor.close();
        }
        return "Stopped.";
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
        System.out.println("Received: " + body);
    }

    private void processError(ServiceBusErrorContext context) {
        System.err.println("Error: " + context.getException().getMessage());
    }
}
