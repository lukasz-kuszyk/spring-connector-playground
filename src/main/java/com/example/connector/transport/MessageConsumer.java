package com.example.connector.transport;

/**
 * Consumes messages from a transport (e.g. RabbitMQ, Service Bus).
 * Implementations start, stop, or consume until interrupt.
 */
public interface MessageConsumer {

    String start();

    String stop();

    String consume();
}
