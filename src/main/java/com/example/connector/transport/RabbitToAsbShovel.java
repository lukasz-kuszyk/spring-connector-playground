package com.example.connector.transport;

import com.example.connector.config.RabbitConfig;
import com.example.connector.config.ServiceBusConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RabbitToAsbShovel {

    private final ConnectionFactory connectionFactory;
    private final Queue connectorQueue;
    private final ServiceBusPublisher serviceBusPublisher;
    private final ServiceBusConfig serviceBusConfig;

    private SimpleMessageListenerContainer container;

    public RabbitToAsbShovel(ConnectionFactory connectionFactory, Queue connectorQueue,
            ServiceBusPublisher serviceBusPublisher, ServiceBusConfig serviceBusConfig) {
        this.connectionFactory = connectionFactory;
        this.connectorQueue = connectorQueue;
        this.serviceBusPublisher = serviceBusPublisher;
        this.serviceBusConfig = serviceBusConfig;
    }

    public String start() {
        if (container != null && container.isRunning()) {
            return "Shovel (Rabbit->ASB) is already running.";
        }
        container = createContainer();
        container.start();
        return "Shovel started. Subscribing to queue '" + RabbitConfig.QUEUE_NAME
                + "', forwarding to ASB topic '" + serviceBusConfig.getTopicName()
                + "'. Run 'shovel-rabbit-to-asb-stop' to stop.";
    }

    public String stop() {
        if (container == null || !container.isRunning()) {
            return "Shovel (Rabbit->ASB) is not running.";
        }
        container.stop();
        container = null;
        return "Shovel (Rabbit->ASB) stopped.";
    }

    private SimpleMessageListenerContainer createContainer() {
        var c = new SimpleMessageListenerContainer(connectionFactory);
        c.setQueues(connectorQueue);
        c.setMessageListener((Message message) -> {
            var body = new String(message.getBody());
            serviceBusPublisher.publish(body);
            System.out.println("Shovel forwarded to ASB: " + body);
        });
        return c;
    }
}
