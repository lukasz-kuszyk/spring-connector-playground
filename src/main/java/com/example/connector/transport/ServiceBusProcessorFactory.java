package com.example.connector.transport;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import com.example.connector.config.ServiceBusConfig;
import org.springframework.stereotype.Component;

@Component
public class ServiceBusProcessorFactory {

    private final ServiceBusConfig serviceBusConfig;

    public ServiceBusProcessorFactory(ServiceBusConfig serviceBusConfig) {
        this.serviceBusConfig = serviceBusConfig;
    }

    public ServiceBusProcessorClient create(MessageHandler handler) {
        return new ServiceBusClientBuilder()
                .connectionString(serviceBusConfig.getConnectionString())
                .processor()
                .topicName(serviceBusConfig.getTopicName())
                .subscriptionName(serviceBusConfig.getSubscriptionName())
                .receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE)
                .disableAutoComplete()
                .processMessage(ctx -> processMessage(ctx, handler))
                .processError(ctx -> processError(ctx, handler))
                .buildProcessorClient();
    }

    private void processMessage(ServiceBusReceivedMessageContext context, MessageHandler handler) {
        var message = context.getMessage();
        var body = message.getBody() != null ? new String(message.getBody().toBytes()) : "";
        handler.onMessage(body);
    }

    private void processError(ServiceBusErrorContext context, MessageHandler handler) {
        handler.onError(context.getException());
    }
}
