package io.cloudbridge.core.messaging;

/**
 * Contract for explicit message acknowledgement in listener flows.
 */
public interface Acknowledgement {

    void ack();

    void nack(Throwable cause);
}
