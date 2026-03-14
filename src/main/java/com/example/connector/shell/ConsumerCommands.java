package com.example.connector.shell;

import java.util.concurrent.CountDownLatch;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

import com.example.connector.config.RabbitConfig;

@Component
public class ConsumerCommands {

    private final ConnectionFactory connectionFactory;
    private final Queue connectorQueue;

    private SimpleMessageListenerContainer container;

    public ConsumerCommands(ConnectionFactory connectionFactory, Queue connectorQueue) {
        this.connectionFactory = connectionFactory;
        this.connectorQueue = connectorQueue;
    }

    @Command(name = "rabbit-consumer-start", description = "Start RabbitMQ consumer (runs in background, use rabbit-consumer-stop to stop)")
    public String startConsumer() {
        if (container != null && container.isRunning()) {
            return "Consumer is already running.";
        }
        container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(connectorQueue);
        container.setMessageListener((Message message) -> {
            var body = new String(message.getBody());
            System.out.println("Received: " + body);
        });
        container.start();
        return "Consumer started. Listening on queue '" + RabbitConfig.QUEUE_NAME + "'. Run 'rabbit-consumer-stop' to stop.";
    }

    @Command(name = "rabbit-consumer-stop", description = "Stop the RabbitMQ consumer")
    public String stopConsumer() {
        if (container == null || !container.isRunning()) {
            return "Consumer is not running.";
        }
        container.stop();
        return "Consumer stopped.";
    }

    @Command(name = "rabbit-consumer-consume", description = "Consume messages until interrupt (Ctrl+C)")
    public String consume() {
        var consumeContainer = new SimpleMessageListenerContainer(connectionFactory);
        consumeContainer.setQueues(connectorQueue);
        consumeContainer.setMessageListener((Message message) -> {
            var body = new String(message.getBody());
            System.out.println("Received: " + body);
        });
        consumeContainer.start();
        System.out.println("Consuming from queue '" + RabbitConfig.QUEUE_NAME + "'. Press Ctrl+C to stop.");
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            consumeContainer.stop();
        }
        return "Stopped.";
    }
}
