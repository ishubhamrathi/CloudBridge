package io.cloudbridge.core.messaging;

public record SimpleCloudCapabilities(
        boolean supportsDelayedDelivery,
        boolean supportsAcknowledgement,
        boolean supportsDeadLetterQueue
) implements CloudCapabilities {
}
