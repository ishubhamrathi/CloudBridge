package io.cloudbridge.aws.messaging;

import io.cloudbridge.config.CloudBridgeProperties;
import io.cloudbridge.core.messaging.MessageHandler;
import io.cloudbridge.core.messaging.QueueConsumer;
import io.cloudbridge.listener.QueueListenerEndpoint;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

public class AwsQueueConsumer implements QueueConsumer {

    private final SqsClient sqsClient;
    private final CloudBridgeProperties properties;
    private final QueueListenerEndpoint endpoint;
    private final MessageHandler handler;
    private final AtomicBoolean running = new AtomicBoolean();
    private final List<Thread> workers = new CopyOnWriteArrayList<>();

    public AwsQueueConsumer(
            SqsClient sqsClient,
            CloudBridgeProperties properties,
            QueueListenerEndpoint endpoint,
            MessageHandler handler
    ) {
        this.sqsClient = sqsClient;
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
            Thread worker = new Thread(this::pollLoop, "cloudbridge-aws-" + endpoint.queue() + "-" + i);
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
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                        .queueName(properties.getAws().getQueuePrefix() + endpoint.queue())
                        .build())
                .queueUrl();

        while (running.get()) {
            var response = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .waitTimeSeconds(properties.getMessaging().getReceiveWaitSeconds())
                    .messageAttributeNames("All")
                    .maxNumberOfMessages(10)
                    .build());

            if (response.messages().isEmpty()) {
                sleep(Duration.ofMillis(properties.getMessaging().getIdleBackoffMs()));
                continue;
            }

            response.messages().forEach(message -> {
                AwsSqsAcknowledgement acknowledgement = new AwsSqsAcknowledgement(sqsClient, queueUrl, message.receiptHandle());
                try {
                    handler.handle(AwsMessageMapper.fromMessage(message), acknowledgement);
                } catch (Exception ex) {
                    acknowledgement.nack(ex);
                }
            });
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
