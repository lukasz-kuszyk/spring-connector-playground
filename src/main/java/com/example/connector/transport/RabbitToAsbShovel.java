package com.example.connector.transport;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RabbitToAsbShovel {

    private final RabbitListenerFactory listenerFactory;
    private final ServiceBusPublisher serviceBusPublisher;

    private SimpleMessageListenerContainer container;

    public RabbitToAsbShovel(RabbitListenerFactory listenerFactory, ServiceBusPublisher serviceBusPublisher) {
        this.listenerFactory = listenerFactory;
        this.serviceBusPublisher = serviceBusPublisher;
    }

    public String start() {
        if (container != null && container.isRunning()) {
            return "already_running";
        }
        container = listenerFactory.create(serviceBusPublisher::publish);
        container.start();
        return "started";
    }

    public String stop() {
        if (container == null || !container.isRunning()) {
            return "not_running";
        }
        container.stop();
        container = null;
        return "stopped";
    }
}
