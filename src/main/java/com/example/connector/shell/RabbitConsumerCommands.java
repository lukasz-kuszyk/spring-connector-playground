package com.example.connector.shell;

import com.example.connector.config.RabbitConfig;
import com.example.connector.transport.MessageHandler;
import com.example.connector.transport.RabbitConsumer;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumerCommands {

    private final RabbitConsumer rabbitConsumer;

    public RabbitConsumerCommands(RabbitConsumer rabbitConsumer) {
        this.rabbitConsumer = rabbitConsumer;
    }

    private static final MessageHandler PRINT_HANDLER = body -> System.out.println("Received: " + body);

    @Command(name = "rabbit-consumer-start", description = "Start RabbitMQ consumer (runs in background, use rabbit-consumer-stop to stop)")
    public String startConsumer() {
        var status = rabbitConsumer.start(PRINT_HANDLER);
        return status.equals("already_running") ? "Consumer is already running."
                : "Consumer started. Listening on queue '" + RabbitConfig.QUEUE_NAME + "'. Run 'rabbit-consumer-stop' to stop.";
    }

    @Command(name = "rabbit-consumer-stop", description = "Stop the RabbitMQ consumer")
    public String stopConsumer() {
        var status = rabbitConsumer.stop();
        return status.equals("not_running") ? "Consumer is not running." : "Consumer stopped.";
    }

    @Command(name = "rabbit-consumer-consume", description = "Consume messages until interrupt (Ctrl+C)")
    public String consume() {
        System.out.println("Consuming from queue '" + RabbitConfig.QUEUE_NAME + "'. Press Ctrl+C to stop.");
        rabbitConsumer.consume(PRINT_HANDLER);
        return "Stopped.";
    }
}
