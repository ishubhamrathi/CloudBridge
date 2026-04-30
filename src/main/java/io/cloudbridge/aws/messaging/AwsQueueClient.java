package io.cloudbridge.aws.messaging;

import io.cloudbridge.config.CloudBridgeProperties;
import io.cloudbridge.core.messaging.CloudMessage;
import io.cloudbridge.core.messaging.QueueClient;
import io.cloudbridge.core.messaging.SendOptions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class AwsQueueClient implements QueueClient {

    private final SqsClient sqsClient;
    private final CloudBridgeProperties.AwsProperties properties;
    private final Map<String, String> queueUrls = new ConcurrentHashMap<>();

    public AwsQueueClient(SqsClient sqsClient, CloudBridgeProperties properties) {
        this.sqsClient = sqsClient;
        this.properties = properties.getAws();
    }

    @Override
    public void send(String queue, CloudMessage message, SendOptions options) {
        try {
            SendMessageRequest.Builder builder = SendMessageRequest.builder()
                    .queueUrl(resolveQueueUrl(queue))
                    .messageBody(message.payload())
                    .messageAttributes(AwsMessageMapper.toAttributes(message.headers()));

            if (options != null && options.delay() != null && !options.delay().isZero()) {
                builder.delaySeconds((int) Math.min(900, options.delay().toSeconds()));
            }

            sqsClient.sendMessage(builder.build());
        } catch (AwsServiceException ex) {
            throw new IllegalStateException("Failed to send AWS SQS message to queue " + queue, ex);
        }
    }

    private String resolveQueueUrl(String queue) {
        return queueUrls.computeIfAbsent(queue, name -> sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                        .queueName(properties.getQueuePrefix() + name)
                        .build())
                .queueUrl());
    }
}
