package io.cloudbridge.retry;

import java.time.Duration;

@FunctionalInterface
public interface BackoffSleeper {

    void sleep(Duration duration) throws InterruptedException;
}
