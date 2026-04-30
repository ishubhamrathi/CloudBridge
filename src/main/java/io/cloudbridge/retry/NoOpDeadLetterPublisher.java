package io.cloudbridge.retry;

import io.cloudbridge.core.messaging.CloudMessage;

public class NoOpDeadLetterPublisher implements DeadLetterPublisher {

    @Override
    public void publish(String sourceQueue, CloudMessage message, Throwable cause) {
    }
}
