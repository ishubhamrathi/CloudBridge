package io.cloudbridge.core.messaging;

public interface QueueConsumer extends AutoCloseable {

    void start();

    void stop();

    @Override
    default void close() {
        stop();
    }
}
