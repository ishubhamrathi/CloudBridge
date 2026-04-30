package io.cloudbridge.core.messaging;

/**
 * Provider-neutral contract for sending messages to a named destination.
 */
public interface QueueClient {

    default void send(String queue, CloudMessage message) {
        send(queue, message, null);
    }

    void send(String queue, CloudMessage message, SendOptions options);
}
