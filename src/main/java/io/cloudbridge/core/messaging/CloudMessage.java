package io.cloudbridge.core.messaging;

import java.util.Map;
import java.util.Objects;

/**
 * Immutable message envelope shared across all provider adapters.
 */
public record CloudMessage(String payload, Map<String, String> headers) {

    public CloudMessage {
        payload = requirePayload(payload);
        headers = Map.copyOf(Objects.requireNonNull(headers, "headers must not be null"));
    }

    public CloudMessage(String payload) {
        this(payload, Map.of());
    }

    private static String requirePayload(String payload) {
        String value = Objects.requireNonNull(payload, "payload must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("payload must not be blank");
        }
        return value;
    }
}
