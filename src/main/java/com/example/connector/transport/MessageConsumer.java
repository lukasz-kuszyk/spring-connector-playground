package com.example.connector.transport;

/**
 * Consumes messages from a transport (e.g. RabbitMQ, Service Bus).
 * Pure I/O - no user-facing output. Commands provide MessageHandler and format status messages.
 */
public interface MessageConsumer {

    String start(MessageHandler handler);

    String stop();

    String consume(MessageHandler handler);
}
