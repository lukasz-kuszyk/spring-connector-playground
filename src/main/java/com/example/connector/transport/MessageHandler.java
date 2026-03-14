package com.example.connector.transport;

/**
 * Handles received messages. Implementations decide what to do (e.g. print, forward).
 * Provided by Commands or Shovels; transport layer invokes it.
 */
@FunctionalInterface
public interface MessageHandler {

    void onMessage(String body);

    default void onError(Throwable t) {
        // no-op by default
    }
}
