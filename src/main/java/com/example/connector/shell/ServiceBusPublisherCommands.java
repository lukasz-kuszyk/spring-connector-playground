package com.example.connector.shell;

import com.example.connector.transport.ServiceBusPublisher;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class ServiceBusPublisherCommands {

    private final ServiceBusPublisher serviceBusPublisher;

    public ServiceBusPublisherCommands(ServiceBusPublisher serviceBusPublisher) {
        this.serviceBusPublisher = serviceBusPublisher;
    }

    @Command(name = "servicebus-publish", description = "Publish a message to the configured Service Bus topic")
    public String publish() {
        return serviceBusPublisher.publish("Hello from Service Bus");
    }
}
