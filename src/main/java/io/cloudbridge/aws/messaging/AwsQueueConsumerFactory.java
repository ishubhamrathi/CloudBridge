package io.cloudbridge.aws.messaging;

import io.cloudbridge.config.CloudBridgeProperties;
import io.cloudbridge.core.messaging.MessageHandler;
import io.cloudbridge.core.messaging.QueueConsumer;
import io.cloudbridge.core.messaging.QueueConsumerFactory;
import io.cloudbridge.listener.QueueListenerEndpoint;
import software.amazon.awssdk.services.sqs.SqsClient;

public class AwsQueueConsumerFactory implements QueueConsumerFactory {

    private final SqsClient sqsClient;
    private final CloudBridgeProperties properties;

    public AwsQueueConsumerFactory(SqsClient sqsClient, CloudBridgeProperties properties) {
        this.sqsClient = sqsClient;
        this.properties = properties;
    }

    @Override
    public QueueConsumer create(QueueListenerEndpoint endpoint, MessageHandler handler) {
        return new AwsQueueConsumer(sqsClient, properties, endpoint, handler);
    }
}
