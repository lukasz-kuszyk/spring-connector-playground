package com.example.connector.shell;

import com.example.connector.transport.RabbitConsumer;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumerCommands {

    private final RabbitConsumer rabbitConsumer;

    public RabbitConsumerCommands(RabbitConsumer rabbitConsumer) {
        this.rabbitConsumer = rabbitConsumer;
    }

    @Command(name = "rabbit-consumer-start", description = "Start RabbitMQ consumer (runs in background, use rabbit-consumer-stop to stop)")
    public String startConsumer() {
        return rabbitConsumer.start();
    }

    @Command(name = "rabbit-consumer-stop", description = "Stop the RabbitMQ consumer")
    public String stopConsumer() {
        return rabbitConsumer.stop();
    }

    @Command(name = "rabbit-consumer-consume", description = "Consume messages until interrupt (Ctrl+C)")
    public String consume() {
        return rabbitConsumer.consume();
    }
}
