package io.cloudbridge.oci.messaging;

import com.oracle.bmc.queue.QueueClient;
import com.oracle.bmc.queue.model.GetMessage;
import com.oracle.bmc.queue.requests.GetMessagesRequest;
import io.cloudbridge.config.CloudBridgeProperties;
import io.cloudbridge.core.messaging.CloudMessage;
import io.cloudbridge.core.messaging.MessageHandler;
import io.cloudbridge.core.messaging.QueueConsumer;
import io.cloudbridge.listener.QueueListenerEndpoint;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class OciQueueConsumer implements QueueConsumer {

    private final QueueClient queueClient;
    private final CloudBridgeProperties properties;
    private final QueueListenerEndpoint endpoint;
    private final MessageHandler handler;
    private final AtomicBoolean running = new AtomicBoolean();
    private final List<Thread> workers = new CopyOnWriteArrayList<>();

    public OciQueueConsumer(
            QueueClient queueClient,
            CloudBridgeProperties properties,
            QueueListenerEndpoint endpoint,
            MessageHandler handler
    ) {
        this.queueClient = queueClient;
        this.properties = properties;
        this.endpoint = endpoint;
        this.handler = handler;
    }

    @Override
    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        for (int i = 0; i < endpoint.concurrency(); i++) {
            Thread worker = new Thread(this::pollLoop, "cloudbridge-oci-" + i);
            worker.setDaemon(true);
            worker.start();
            workers.add(worker);
        }
    }

    @Override
    public void stop() {
        running.set(false);
        List<Thread> snapshot = new ArrayList<>(workers);
        workers.clear();
        snapshot.forEach(Thread::interrupt);
    }

    private void pollLoop() {
        while (running.get()) {
            var response = queueClient.getMessages(GetMessagesRequest.builder()
                    .queueId(endpoint.queue())
                    .limit(properties.getOci().getChannelConsumptionLimit())
                    .visibilityInSeconds(properties.getOci().getVisibilityInSeconds())
                    .timeoutInSeconds(properties.getOci().getPollingTimeoutSeconds())
                    .build());

            List<GetMessage> messages = response.getItems();
            if (messages == null || messages.isEmpty()) {
                sleep(Duration.ofMillis(properties.getMessaging().getIdleBackoffMs()));
                continue;
            }

            for (GetMessage message : messages) {
                OciAcknowledgement acknowledgement = new OciAcknowledgement(queueClient, endpoint.queue(), message.getReceipt());
                try {
                    handler.handle(new CloudMessage(message.getContent()), acknowledgement);
                } catch (Exception ex) {
                    acknowledgement.nack(ex);
                }
            }
        }
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
