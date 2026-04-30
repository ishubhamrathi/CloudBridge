package io.cloudbridge.oci.messaging;

import com.oracle.bmc.queue.QueueClient;
import io.cloudbridge.config.CloudBridgeProperties;
import io.cloudbridge.core.messaging.MessageHandler;
import io.cloudbridge.core.messaging.QueueConsumer;
import io.cloudbridge.core.messaging.QueueConsumerFactory;
import io.cloudbridge.listener.QueueListenerEndpoint;

public class OciQueueConsumerFactory implements QueueConsumerFactory {

    private final QueueClient queueClient;
    private final CloudBridgeProperties properties;

    public OciQueueConsumerFactory(QueueClient queueClient, CloudBridgeProperties properties) {
        this.queueClient = queueClient;
        this.properties = properties;
    }

    @Override
    public QueueConsumer create(QueueListenerEndpoint endpoint, MessageHandler handler) {
        return new OciQueueConsumer(queueClient, properties, endpoint, handler);
    }
}
