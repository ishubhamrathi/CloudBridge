package io.cloudbridge.gcp.messaging;

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import io.cloudbridge.config.CloudBridgeProperties;
import io.cloudbridge.core.messaging.CloudMessage;
import io.cloudbridge.core.messaging.MessageHandler;
import io.cloudbridge.core.messaging.QueueConsumer;
import io.cloudbridge.listener.QueueListenerEndpoint;

public class GcpQueueConsumer implements QueueConsumer {

    private final Subscriber subscriber;

    public GcpQueueConsumer(CloudBridgeProperties properties, QueueListenerEndpoint endpoint, MessageHandler handler) {
        MessageReceiver receiver = (message, consumer) -> {
            GcpAcknowledgement acknowledgement = new GcpAcknowledgement(consumer);
            try {
                handler.handle(
                        new CloudMessage(message.getData().toStringUtf8(), message.getAttributesMap()),
                        acknowledgement
                );
            } catch (Exception ex) {
                acknowledgement.nack(ex);
            }
        };
        this.subscriber = Subscriber.newBuilder(
                        ProjectSubscriptionName.of(properties.getGcp().getProjectId(), endpoint.queue()),
                        receiver)
                .build();
    }

    @Override
    public void start() {
        subscriber.startAsync().awaitRunning();
    }

    @Override
    public void stop() {
        subscriber.stopAsync().awaitTerminated();
    }
}
