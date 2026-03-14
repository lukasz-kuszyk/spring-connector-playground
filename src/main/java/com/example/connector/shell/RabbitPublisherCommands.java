package com.example.connector.shell;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

import com.example.connector.config.RabbitConfig;

@Component
public class RabbitPublisherCommands {

    private final RabbitTemplate rabbitTemplate;

    public RabbitPublisherCommands(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Command(name = "rabbit-publish", description = "Publish a message to connector.queue")
    public String publish() {
        String message = "Hello from RabbitMQ";

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, message);

        return "Published: " + message;
    }
}
