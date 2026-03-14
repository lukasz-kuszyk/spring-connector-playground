package com.example.connector.transport;

import java.util.concurrent.CountDownLatch;

import com.example.connector.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer implements MessageConsumer {

    private final ConnectionFactory connectionFactory;
    private final Queue connectorQueue;

    private SimpleMessageListenerContainer container;

    public RabbitConsumer(ConnectionFactory connectionFactory, Queue connectorQueue) {
        this.connectionFactory = connectionFactory;
        this.connectorQueue = connectorQueue;
    }

    @Override
    public String start() {
        if (container != null && container.isRunning()) {
            return "Consumer is already running.";
        }
        container = createContainer();
        container.start();
        return "Consumer started. Listening on queue '" + RabbitConfig.QUEUE_NAME + "'. Run 'rabbit-consumer-stop' to stop.";
    }

    @Override
    public String stop() {
        if (container == null || !container.isRunning()) {
            return "Consumer is not running.";
        }
        container.stop();
        return "Consumer stopped.";
    }

    @Override
    public String consume() {
        var consumeContainer = createContainer();
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

    private SimpleMessageListenerContainer createContainer() {
        var c = new SimpleMessageListenerContainer(connectionFactory);
        c.setQueues(connectorQueue);
        c.setMessageListener((Message message) -> {
            var body = new String(message.getBody());
            System.out.println("Received: " + body);
        });
        return c;
    }
}
