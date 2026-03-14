package com.example.connector.transport;

/**
 * Publishes messages to a transport (e.g. RabbitMQ, Service Bus).
 * Implementations receive a message and pass it to the appropriate transport.
 */
public interface MessagePublisher {

    String publish(String message);
}
