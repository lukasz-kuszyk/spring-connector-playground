package com.example.connector.shell;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.example.connector.config.ServiceBusConfig;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class ServiceBusPublisherCommands {

    private final ServiceBusConfig serviceBusConfig;

    public ServiceBusPublisherCommands(ServiceBusConfig serviceBusConfig) {
        this.serviceBusConfig = serviceBusConfig;
    }

    @Command(name = "servicebus-publish", description = "Publish a message to the configured Service Bus topic")
    public String publish() {
        String message = "Hello from Service Bus";

        try (ServiceBusSenderClient sender = new ServiceBusClientBuilder()
                .connectionString(serviceBusConfig.getConnectionString())
                .sender()
                .topicName(serviceBusConfig.getTopicName())
                .buildClient()) {

            sender.sendMessage(new ServiceBusMessage(message));

            return "Published to topic '" + serviceBusConfig.getTopicName() + "': " + message;
        }
    }
}
