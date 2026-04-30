package io.cloudbridge.oci.messaging;

import com.oracle.bmc.queue.QueueClient;
import com.oracle.bmc.queue.model.PutMessagesDetails;
import com.oracle.bmc.queue.model.PutMessagesDetailsEntry;
import com.oracle.bmc.queue.requests.PutMessagesRequest;
import io.cloudbridge.core.messaging.CloudMessage;
import io.cloudbridge.core.messaging.SendOptions;

public class OciQueueClient implements io.cloudbridge.core.messaging.QueueClient {

    private final QueueClient queueClient;

    public OciQueueClient(QueueClient queueClient) {
        this.queueClient = queueClient;
    }

    @Override
    public void send(String queue, CloudMessage message, SendOptions options) {
        PutMessagesDetailsEntry.Builder entryBuilder = PutMessagesDetailsEntry.builder()
                .content(message.payload());

        if (options != null && options.delay() != null && !options.delay().isZero()) {
            entryBuilder.delayInSeconds((int) Math.max(0, options.delay().toSeconds()));
        }

        queueClient.putMessages(PutMessagesRequest.builder()
                .queueId(queue)
                .putMessagesDetails(PutMessagesDetails.builder()
                        .messages(java.util.List.of(entryBuilder.build()))
                        .build())
                .build());
    }
}
