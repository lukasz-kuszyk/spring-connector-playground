package com.example.connector.transport;

import java.util.concurrent.CountDownLatch;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer implements MessageConsumer {

    private final RabbitListenerFactory listenerFactory;

    private SimpleMessageListenerContainer container;

    public RabbitConsumer(RabbitListenerFactory listenerFactory) {
        this.listenerFactory = listenerFactory;
    }

    public String start(MessageHandler handler) {
        if (container != null && container.isRunning()) {
            return "already_running";
        }
        container = listenerFactory.create(handler);
        container.start();
        return "started";
    }

    public String stop() {
        if (container == null || !container.isRunning()) {
            return "not_running";
        }
        container.stop();
        return "stopped";
    }

    public String consume(MessageHandler handler) {
        var consumeContainer = listenerFactory.create(handler);
        consumeContainer.start();
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            consumeContainer.stop();
        }
        return "stopped";
    }
}
