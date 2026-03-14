package com.example.connector.shell;

import com.example.connector.transport.RabbitPublisher;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class RabbitPublisherCommands {

    private static final String MESSAGE = "Hello from RabbitMQ";

    private final RabbitPublisher rabbitPublisher;

    public RabbitPublisherCommands(RabbitPublisher rabbitPublisher) {
        this.rabbitPublisher = rabbitPublisher;
    }

    @Command(name = "rabbit-publish", description = "Publish a message to connector.queue")
    public String rabbitPublish() {
        rabbitPublisher.publish(MESSAGE);
        return "Published: " + MESSAGE;
    }
}
