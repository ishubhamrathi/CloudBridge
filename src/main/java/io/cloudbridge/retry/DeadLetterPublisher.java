package io.cloudbridge.retry;

import io.cloudbridge.core.messaging.CloudMessage;

public interface DeadLetterPublisher {

    void publish(String sourceQueue, CloudMessage message, Throwable cause);
}
