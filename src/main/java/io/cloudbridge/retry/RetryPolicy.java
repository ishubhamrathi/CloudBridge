package io.cloudbridge.retry;

import java.time.Duration;
import java.util.Objects;

public record RetryPolicy(int maxAttempts, Duration backoff) {

    public RetryPolicy {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be at least 1");
        }
        backoff = Objects.requireNonNull(backoff, "backoff must not be null");
        if (backoff.isNegative()) {
            throw new IllegalArgumentException("backoff must not be negative");
        }
    }
}
