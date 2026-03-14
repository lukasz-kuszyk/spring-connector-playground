package com.example.connector.shell;

import com.example.connector.transport.ServiceBusPublisher;
import com.example.connector.config.ServiceBusConfig;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class ServiceBusPublisherCommands {

    private static final String MESSAGE = "Hello from Service Bus";

    private final ServiceBusPublisher serviceBusPublisher;
    private final ServiceBusConfig serviceBusConfig;

    public ServiceBusPublisherCommands(ServiceBusPublisher serviceBusPublisher, ServiceBusConfig serviceBusConfig) {
        this.serviceBusPublisher = serviceBusPublisher;
        this.serviceBusConfig = serviceBusConfig;
    }

    @Command(name = "servicebus-publish", description = "Publish a message to the configured Service Bus topic")
    public String publish() {
        serviceBusPublisher.publish(MESSAGE);
        return "Published to topic '" + serviceBusConfig.getTopicName() + "': " + MESSAGE;
    }
}
