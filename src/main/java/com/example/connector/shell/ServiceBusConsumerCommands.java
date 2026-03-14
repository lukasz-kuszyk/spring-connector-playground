package com.example.connector.shell;

import com.example.connector.config.ServiceBusConfig;
import com.example.connector.transport.MessageHandler;
import com.example.connector.transport.ServiceBusConsumer;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class ServiceBusConsumerCommands {

    private final ServiceBusConsumer serviceBusConsumer;
    private final ServiceBusConfig serviceBusConfig;

    public ServiceBusConsumerCommands(ServiceBusConsumer serviceBusConsumer, ServiceBusConfig serviceBusConfig) {
        this.serviceBusConsumer = serviceBusConsumer;
        this.serviceBusConfig = serviceBusConfig;
    }

    private static final MessageHandler PRINT_HANDLER = new MessageHandler() {
        @Override
        public void onMessage(String body) {
            System.out.println("Received: " + body);
        }

        @Override
        public void onError(Throwable t) {
            System.err.println("Error: " + t.getMessage());
        }
    };

    @Command(name = "servicebus-consumer-start", description = "Start Service Bus consumer (runs in background, use servicebus-consumer-stop to stop)")
    public String startConsumer() {
        var status = serviceBusConsumer.start(PRINT_HANDLER);
        return status.equals("already_running") ? "Consumer is already running."
                : "Consumer started. Listening on topic '" + serviceBusConfig.getTopicName()
                        + "' subscription '" + serviceBusConfig.getSubscriptionName()
                        + "'. Run 'servicebus-consumer-stop' to stop.";
    }

    @Command(name = "servicebus-consumer-stop", description = "Stop the Service Bus consumer")
    public String stopConsumer() {
        var status = serviceBusConsumer.stop();
        return status.equals("not_running") ? "Consumer is not running." : "Consumer stopped.";
    }

    @Command(name = "servicebus-consumer-consume", description = "Consume messages until interrupt (Ctrl+C)")
    public String consume() {
        System.out.println("Consuming from topic '" + serviceBusConfig.getTopicName()
                + "' subscription '" + serviceBusConfig.getSubscriptionName()
                + "'. Press Ctrl+C to stop.");
        serviceBusConsumer.consume(PRINT_HANDLER);
        return "Stopped.";
    }
}
