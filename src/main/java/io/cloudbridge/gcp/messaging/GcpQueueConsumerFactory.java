package io.cloudbridge.gcp.messaging;

import io.cloudbridge.config.CloudBridgeProperties;
import io.cloudbridge.core.messaging.MessageHandler;
import io.cloudbridge.core.messaging.QueueConsumer;
import io.cloudbridge.core.messaging.QueueConsumerFactory;
import io.cloudbridge.listener.QueueListenerEndpoint;

public class GcpQueueConsumerFactory implements QueueConsumerFactory {

    private final CloudBridgeProperties properties;

    public GcpQueueConsumerFactory(CloudBridgeProperties properties) {
        this.properties = properties;
    }

    @Override
    public QueueConsumer create(QueueListenerEndpoint endpoint, MessageHandler handler) {
        return new GcpQueueConsumer(properties, endpoint, handler);
    }
}
