package io.cloudbridge.aws.messaging;

import io.cloudbridge.core.messaging.CloudMessage;
import java.util.LinkedHashMap;
import java.util.Map;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

final class AwsMessageMapper {

    private AwsMessageMapper() {
    }

    static Map<String, MessageAttributeValue> toAttributes(Map<String, String> headers) {
        Map<String, MessageAttributeValue> attributes = new LinkedHashMap<>();
        headers.forEach((key, value) -> attributes.put(key, MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(value)
                .build()));
        return attributes;
    }

    static CloudMessage fromMessage(Message message) {
        Map<String, String> headers = new LinkedHashMap<>();
        if (message.messageAttributes() != null) {
            message.messageAttributes().forEach((key, value) -> headers.put(key, value.stringValue()));
        }
        return new CloudMessage(message.body(), headers);
    }
}
