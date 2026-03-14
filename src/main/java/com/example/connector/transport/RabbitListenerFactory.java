package com.example.connector.transport;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RabbitListenerFactory {

    private final ConnectionFactory connectionFactory;
    private final Queue connectorQueue;

    public RabbitListenerFactory(ConnectionFactory connectionFactory, Queue connectorQueue) {
        this.connectionFactory = connectionFactory;
        this.connectorQueue = connectorQueue;
    }

    public SimpleMessageListenerContainer create(MessageHandler handler) {
        var c = new SimpleMessageListenerContainer(connectionFactory);
        c.setQueues(connectorQueue);
        c.setMessageListener((Message message) -> {
            var body = new String(message.getBody());
            handler.onMessage(body);
        });
        return c;
    }
}
