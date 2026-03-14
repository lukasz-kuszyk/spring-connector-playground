package com.example.connector.shell;

import com.example.connector.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class ConsumerCommands {

    private final ConnectionFactory connectionFactory;
    private final Queue connectorQueue;

    public ConsumerCommands(ConnectionFactory connectionFactory, Queue connectorQueue) {
        this.connectionFactory = connectionFactory;
        this.connectorQueue = connectorQueue;
    }

    @Command(name = "start-consumer", description = "Start RabbitMQ consumer and wait for messages (blocks until Ctrl+C)")
    public String startConsumer() {
        var container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(connectorQueue);
        container.setMessageListener((Message message) -> {
            var body = new String(message.getBody());
            System.out.println("Received: " + body);
        });

        container.start();

        System.out.println("Consumer started. Listening on queue '" + RabbitConfig.QUEUE_NAME + "'. Press Ctrl+C to stop.");

        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            container.stop();
        }

        return "Consumer stopped.";
    }
}
