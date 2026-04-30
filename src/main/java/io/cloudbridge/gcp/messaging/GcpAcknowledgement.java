package io.cloudbridge.gcp.messaging;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import io.cloudbridge.core.messaging.Acknowledgement;
import java.util.concurrent.atomic.AtomicBoolean;

public class GcpAcknowledgement implements Acknowledgement {

    private final AckReplyConsumer consumer;
    private final AtomicBoolean completed = new AtomicBoolean();

    public GcpAcknowledgement(AckReplyConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void ack() {
        if (completed.compareAndSet(false, true)) {
            consumer.ack();
        }
    }

    @Override
    public void nack(Throwable cause) {
        if (completed.compareAndSet(false, true)) {
            consumer.nack();
        }
    }
}
