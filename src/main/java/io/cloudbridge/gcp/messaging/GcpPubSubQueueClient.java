package io.cloudbridge.gcp.messaging;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import io.cloudbridge.config.CloudBridgeProperties;
import io.cloudbridge.core.messaging.CloudMessage;
import io.cloudbridge.core.messaging.QueueClient;
import io.cloudbridge.core.messaging.SendOptions;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GcpPubSubQueueClient implements QueueClient {

    private final String projectId;
    private final Map<String, Publisher> publishers = new ConcurrentHashMap<>();

    public GcpPubSubQueueClient(CloudBridgeProperties properties) {
        this.projectId = properties.getGcp().getProjectId();
    }

    @Override
    public void send(String queue, CloudMessage message, SendOptions options) {
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8(message.payload()))
                .putAllAttributes(message.headers())
                .build();
        try {
            publisher(queue).publish(pubsubMessage);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to publish to GCP Pub/Sub topic " + queue, ex);
        }
    }

    private Publisher publisher(String queue) {
        return publishers.computeIfAbsent(queue, this::newPublisher);
    }

    private Publisher newPublisher(String queue) {
        try {
            return Publisher.newBuilder(TopicName.of(projectId, queue)).build();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to create GCP publisher for " + queue, ex);
        }
    }
}
