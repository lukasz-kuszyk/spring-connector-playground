package com.example.connector.shell;

import com.example.connector.config.RabbitConfig;
import com.example.connector.config.ServiceBusConfig;
import com.example.connector.transport.AsbToRabbitShovel;
import com.example.connector.transport.RabbitToAsbShovel;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class ShovelCommands {

    private final AsbToRabbitShovel asbToRabbitShovel;
    private final RabbitToAsbShovel rabbitToAsbShovel;
    private final ServiceBusConfig serviceBusConfig;

    public ShovelCommands(AsbToRabbitShovel asbToRabbitShovel, RabbitToAsbShovel rabbitToAsbShovel,
            ServiceBusConfig serviceBusConfig) {
        this.asbToRabbitShovel = asbToRabbitShovel;
        this.rabbitToAsbShovel = rabbitToAsbShovel;
        this.serviceBusConfig = serviceBusConfig;
    }

    @Command(name = "shovel-asb-to-rabbit-start", description = "Start shovel ASB->Rabbit (subscribe to ASB, forward to RabbitMQ)")
    public String asbToRabbitStart() {
        var status = asbToRabbitShovel.start();
        return status.equals("already_running") ? "Shovel is already running."
                : "Shovel started. Subscribing to topic '" + serviceBusConfig.getTopicName()
                        + "' subscription '" + serviceBusConfig.getSubscriptionName()
                        + "', forwarding to RabbitMQ. Run 'shovel-asb-to-rabbit-stop' to stop.";
    }

    @Command(name = "shovel-asb-to-rabbit-stop", description = "Stop shovel ASB->Rabbit")
    public String asbToRabbitStop() {
        var status = asbToRabbitShovel.stop();
        return status.equals("not_running") ? "Shovel is not running." : "Shovel stopped.";
    }

    @Command(name = "shovel-rabbit-to-asb-start", description = "Start shovel Rabbit->ASB (subscribe to RabbitMQ, forward to ASB topic)")
    public String rabbitToAsbStart() {
        var status = rabbitToAsbShovel.start();
        return status.equals("already_running") ? "Shovel (Rabbit->ASB) is already running."
                : "Shovel started. Subscribing to queue '" + RabbitConfig.QUEUE_NAME
                        + "', forwarding to ASB topic '" + serviceBusConfig.getTopicName()
                        + "'. Run 'shovel-rabbit-to-asb-stop' to stop.";
    }

    @Command(name = "shovel-rabbit-to-asb-stop", description = "Stop shovel Rabbit->ASB")
    public String rabbitToAsbStop() {
        var status = rabbitToAsbShovel.stop();
        return status.equals("not_running") ? "Shovel (Rabbit->ASB) is not running." : "Shovel (Rabbit->ASB) stopped.";
    }
}
