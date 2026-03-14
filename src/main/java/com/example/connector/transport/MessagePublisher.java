package com.example.connector.transport;

/**
 * Publishes messages to a transport (e.g. RabbitMQ, Service Bus).
 * Pure I/O - no user-facing output. Commands are responsible for informing the user.
 */
public interface MessagePublisher {

    void publish(String message);
}
