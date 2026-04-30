package io.cloudbridge.retry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.cloudbridge.core.messaging.Acknowledgement;
import io.cloudbridge.core.messaging.CloudMessage;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class RetryExecutorTest {

    @Test
    void retriesBeforeAckingSuccess() {
        RecordingDeadLetterPublisher dlqPublisher = new RecordingDeadLetterPublisher();
        RecordingAcknowledgement acknowledgement = new RecordingAcknowledgement();
        AtomicInteger attempts = new AtomicInteger();
        AtomicInteger sleeps = new AtomicInteger();
        RetryExecutor executor = new RetryExecutor(
                new RetryPolicy(3, Duration.ofMillis(25)),
                dlqPublisher,
                duration -> sleeps.incrementAndGet());

        executor.execute("orders", new CloudMessage("payload"), acknowledgement, () -> {
            if (attempts.incrementAndGet() < 3) {
                throw new IllegalStateException("boom");
            }
        });

        assertEquals(3, attempts.get());
        assertEquals(2, sleeps.get());
        assertEquals(1, acknowledgement.ackCount);
        assertEquals(0, acknowledgement.nackCount);
        assertEquals(0, dlqPublisher.publishCount);
    }

    @Test
    void publishesToDlqAfterMaxAttempts() {
        RecordingDeadLetterPublisher dlqPublisher = new RecordingDeadLetterPublisher();
        RecordingAcknowledgement acknowledgement = new RecordingAcknowledgement();
        RetryExecutor executor = new RetryExecutor(
                new RetryPolicy(2, Duration.ZERO),
                dlqPublisher,
                duration -> {
                });

        executor.execute("orders", new CloudMessage("payload"), acknowledgement, () -> {
            throw new IllegalStateException("boom");
        });

        assertEquals(1, acknowledgement.ackCount);
        assertEquals(0, acknowledgement.nackCount);
        assertEquals(1, dlqPublisher.publishCount);
        assertEquals("orders", dlqPublisher.queue);
        assertEquals("payload", dlqPublisher.message.payload());
    }

    @Test
    void nacksWhenDlqPublishFails() {
        RecordingAcknowledgement acknowledgement = new RecordingAcknowledgement();
        RuntimeException failure = new RuntimeException("dlq failure");
        RetryExecutor executor = new RetryExecutor(
                new RetryPolicy(1, Duration.ZERO),
                (queue, message, cause) -> {
                    throw failure;
                },
                duration -> {
                });

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                executor.execute("orders", new CloudMessage("payload"), acknowledgement, () -> {
                    throw new IllegalStateException("boom");
                }));

        assertSame(failure, thrown);
        assertEquals(0, acknowledgement.ackCount);
        assertEquals(1, acknowledgement.nackCount);
    }

    static final class RecordingAcknowledgement implements Acknowledgement {
        private int ackCount;
        private int nackCount;

        @Override
        public void ack() {
            ackCount++;
        }

        @Override
        public void nack(Throwable cause) {
            nackCount++;
        }
    }

    static final class RecordingDeadLetterPublisher implements DeadLetterPublisher {
        private int publishCount;
        private String queue;
        private CloudMessage message;

        @Override
        public void publish(String sourceQueue, CloudMessage message, Throwable cause) {
            publishCount++;
            queue = sourceQueue;
            this.message = message;
        }
    }
}
