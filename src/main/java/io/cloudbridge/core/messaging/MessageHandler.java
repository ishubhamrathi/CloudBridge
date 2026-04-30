package io.cloudbridge.core.messaging;

@FunctionalInterface
public interface MessageHandler {

    void handle(CloudMessage message, Acknowledgement acknowledgement) throws Exception;
}
