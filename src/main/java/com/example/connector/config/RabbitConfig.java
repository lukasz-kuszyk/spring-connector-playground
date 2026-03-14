package com.example.connector.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "connector.exchange";
    public static final String QUEUE_NAME = "connector.queue";
    public static final String ROUTING_KEY = "connector.routing.key";

    @Bean
    public Queue connectorQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange connectorExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding connectorBinding(Queue connectorQueue, DirectExchange connectorExchange) {
        return BindingBuilder.bind(connectorQueue).to(connectorExchange).with(ROUTING_KEY);
    }
}
