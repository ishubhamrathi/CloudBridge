package io.cloudbridge.oci.messaging;

import com.oracle.bmc.queue.QueueClient;
import com.oracle.bmc.queue.requests.DeleteMessageRequest;
import io.cloudbridge.core.messaging.Acknowledgement;
import java.util.concurrent.atomic.AtomicBoolean;

public class OciAcknowledgement implements Acknowledgement {

    private final QueueClient queueClient;
    private final String queueId;
    private final String receipt;
    private final AtomicBoolean completed = new AtomicBoolean();

    public OciAcknowledgement(QueueClient queueClient, String queueId, String receipt) {
        this.queueClient = queueClient;
        this.queueId = queueId;
        this.receipt = receipt;
    }

    @Override
    public void ack() {
        if (completed.compareAndSet(false, true)) {
            queueClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueId(queueId)
                    .messageReceipt(receipt)
                    .build());
        }
    }

    @Override
    public void nack(Throwable cause) {
        completed.compareAndSet(false, true);
    }
}
