package io.cloudbridge.core.messaging;

import java.time.Duration;
import java.util.Objects;

/**
 * Common send-time hints that adapters may honor when the provider supports them.
 */
public record SendOptions(Duration delay) {

    public SendOptions {
        Duration value = Objects.requireNonNull(delay, "delay must not be null");
        if (value.isNegative()) {
            throw new IllegalArgumentException("delay must not be negative");
        }
        delay = value;
    }
}
