package com.example.connector.shell;

import com.example.connector.transport.ServiceBusConsumer;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class ServiceBusConsumerCommands {

    private final ServiceBusConsumer serviceBusConsumer;

    public ServiceBusConsumerCommands(ServiceBusConsumer serviceBusConsumer) {
        this.serviceBusConsumer = serviceBusConsumer;
    }

    @Command(name = "servicebus-consumer-start", description = "Start Service Bus consumer (runs in background, use servicebus-consumer-stop to stop)")
    public String startConsumer() {
        return serviceBusConsumer.start();
    }

    @Command(name = "servicebus-consumer-stop", description = "Stop the Service Bus consumer")
    public String stopConsumer() {
        return serviceBusConsumer.stop();
    }

    @Command(name = "servicebus-consumer-consume", description = "Consume messages until interrupt (Ctrl+C)")
    public String consume() {
        return serviceBusConsumer.consume();
    }
}
