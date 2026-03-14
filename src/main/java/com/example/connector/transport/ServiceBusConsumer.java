package com.example.connector.transport;

import java.util.concurrent.CountDownLatch;

import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import org.springframework.stereotype.Component;

@Component
public class ServiceBusConsumer implements MessageConsumer {

    private final ServiceBusProcessorFactory processorFactory;

    private ServiceBusProcessorClient processorClient;

    public ServiceBusConsumer(ServiceBusProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }

    @Override
    public String start(MessageHandler handler) {
        if (processorClient != null && processorClient.isRunning()) {
            return "already_running";
        }
        processorClient = processorFactory.create(handler);
        processorClient.start();
        return "started";
    }

    @Override
    public String stop() {
        if (processorClient == null || !processorClient.isRunning()) {
            return "not_running";
        }
        processorClient.stop();
        processorClient.close();
        processorClient = null;
        return "stopped";
    }

    @Override
    public String consume(MessageHandler handler) {
        var consumeProcessor = processorFactory.create(handler);
        consumeProcessor.start();
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            consumeProcessor.stop();
            consumeProcessor.close();
        }
        return "stopped";
    }
}
