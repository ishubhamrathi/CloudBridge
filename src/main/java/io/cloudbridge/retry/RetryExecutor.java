package io.cloudbridge.retry;

import io.cloudbridge.core.messaging.Acknowledgement;
import io.cloudbridge.core.messaging.CloudMessage;

public class RetryExecutor {

    private final RetryPolicy retryPolicy;
    private final DeadLetterPublisher deadLetterPublisher;
    private final BackoffSleeper sleeper;

    public RetryExecutor(RetryPolicy retryPolicy, DeadLetterPublisher deadLetterPublisher, BackoffSleeper sleeper) {
        this.retryPolicy = retryPolicy;
        this.deadLetterPublisher = deadLetterPublisher;
        this.sleeper = sleeper;
    }

    public void execute(String queue, CloudMessage message, Acknowledgement acknowledgement, ThrowingRunnable runnable) {
        for (int attempt = 1; attempt <= retryPolicy.maxAttempts(); attempt++) {
            try {
                runnable.run();
                acknowledgement.ack();
                return;
            } catch (Exception ex) {
                if (attempt >= retryPolicy.maxAttempts()) {
                    handleExhausted(queue, message, acknowledgement, ex);
                    return;
                }
                sleep();
            }
        }
    }

    private void handleExhausted(String queue, CloudMessage message, Acknowledgement acknowledgement, Exception failure) {
        try {
            deadLetterPublisher.publish(queue, message, failure);
            acknowledgement.ack();
        } catch (RuntimeException dlqFailure) {
            acknowledgement.nack(dlqFailure);
            throw dlqFailure;
        }
    }

    private void sleep() {
        try {
            sleeper.sleep(retryPolicy.backoff());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry interrupted", ex);
        }
    }
}
