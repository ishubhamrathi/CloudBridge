package io.cloudbridge.core.messaging;

/**
 * Describes the portable features an adapter can support.
 */
public interface CloudCapabilities {

    boolean supportsDelayedDelivery();

    boolean supportsAcknowledgement();

    boolean supportsDeadLetterQueue();
}
