package io.cloudbridge.aws.messaging;

import io.cloudbridge.core.messaging.Acknowledgement;
import java.util.concurrent.atomic.AtomicBoolean;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;

public class AwsSqsAcknowledgement implements Acknowledgement {

    private final SqsClient sqsClient;
    private final String queueUrl;
    private final String receiptHandle;
    private final AtomicBoolean completed = new AtomicBoolean();

    public AwsSqsAcknowledgement(SqsClient sqsClient, String queueUrl, String receiptHandle) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        this.receiptHandle = receiptHandle;
    }

    @Override
    public void ack() {
        if (completed.compareAndSet(false, true)) {
            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(receiptHandle)
                    .build());
        }
    }

    @Override
    public void nack(Throwable cause) {
        completed.compareAndSet(false, true);
    }
}
