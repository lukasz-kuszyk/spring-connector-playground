package com.example.connector.transport;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.example.connector.config.ServiceBusConfig;
import org.springframework.stereotype.Component;

@Component
public class ServiceBusPublisher implements MessagePublisher {

    private final ServiceBusConfig serviceBusConfig;

    public ServiceBusPublisher(ServiceBusConfig serviceBusConfig) {
        this.serviceBusConfig = serviceBusConfig;
    }

    @Override
    public void publish(String message) {
        try (ServiceBusSenderClient sender = new ServiceBusClientBuilder()
                .connectionString(serviceBusConfig.getConnectionString())
                .sender()
                .topicName(serviceBusConfig.getTopicName())
                .buildClient()) {
            sender.sendMessage(new ServiceBusMessage(message));
        }
    }
}
