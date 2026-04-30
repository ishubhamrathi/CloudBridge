package io.cloudbridge.retry;

import io.cloudbridge.config.CloudBridgeProperties;
import io.cloudbridge.core.messaging.CloudMessage;
import io.cloudbridge.core.messaging.QueueClient;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultDeadLetterPublisher implements DeadLetterPublisher {

    private final QueueClient queueClient;
    private final String dlqSuffix;

    public DefaultDeadLetterPublisher(QueueClient queueClient, CloudBridgeProperties properties) {
        this.queueClient = queueClient;
        this.dlqSuffix = properties.getMessaging().getDlqSuffix();
    }

    @Override
    public void publish(String sourceQueue, CloudMessage message, Throwable cause) {
        Map<String, String> headers = new LinkedHashMap<>(message.headers());
        headers.put("x-cloudbridge-source-queue", sourceQueue);
        headers.put("x-cloudbridge-error-type", cause.getClass().getName());
        headers.put("x-cloudbridge-error-message", cause.getMessage() == null ? "" : cause.getMessage());
        queueClient.send(sourceQueue + dlqSuffix, new CloudMessage(message.payload(), headers));
    }
}
