package com.example.connector.shell;

import com.example.connector.transport.RabbitToAsbShovel;
import com.example.connector.transport.AsbToRabbitShovel;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class ShovelCommands {

    private final AsbToRabbitShovel asbToRabbitShovel;
    private final RabbitToAsbShovel rabbitToAsbShovel;

    public ShovelCommands(AsbToRabbitShovel asbToRabbitShovel, RabbitToAsbShovel rabbitToAsbShovel) {
        this.asbToRabbitShovel = asbToRabbitShovel;
        this.rabbitToAsbShovel = rabbitToAsbShovel;
    }

    @Command(name = "shovel-asb-to-rabbit-start", description = "Start shovel ASB->Rabbit (subscribe to ASB, forward to RabbitMQ)")
    public String start() {
        return asbToRabbitShovel.start();
    }

    @Command(name = "shovel-asb-to-rabbit-stop", description = "Stop shovel ASB->Rabbit")
    public String stop() {
        return asbToRabbitShovel.stop();
    }

    @Command(name = "shovel-rabbit-to-asb-start", description = "Start shovel Rabbit->ASB (subscribe to RabbitMQ, forward to ASB topic)")
    public String rabbitToAsbStart() {
        return rabbitToAsbShovel.start();
    }

    @Command(name = "shovel-rabbit-to-asb-stop", description = "Stop shovel Rabbit->ASB")
    public String rabbitToAsbStop() {
        return rabbitToAsbShovel.stop();
    }
}
