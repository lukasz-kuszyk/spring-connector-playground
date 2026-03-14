package com.example.connector.transport;

import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import org.springframework.stereotype.Component;

@Component
public class AsbToRabbitShovel {

    private final ServiceBusProcessorFactory processorFactory;
    private final RabbitPublisher rabbitPublisher;

    private ServiceBusProcessorClient processorClient;

    public AsbToRabbitShovel(ServiceBusProcessorFactory processorFactory, RabbitPublisher rabbitPublisher) {
        this.processorFactory = processorFactory;
        this.rabbitPublisher = rabbitPublisher;
    }

    public String start() {
        if (processorClient != null && processorClient.isRunning()) {
            return "already_running";
        }
        processorClient = processorFactory.create(rabbitPublisher::publish);
        processorClient.start();
        return "started";
    }

    public String stop() {
        if (processorClient == null || !processorClient.isRunning()) {
            return "not_running";
        }
        processorClient.stop();
        processorClient.close();
        processorClient = null;
        return "stopped";
    }
}
