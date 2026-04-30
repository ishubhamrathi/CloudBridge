package io.cloudbridge.core.messaging;

import io.cloudbridge.listener.QueueListenerEndpoint;

public interface QueueConsumerFactory {

    QueueConsumer create(QueueListenerEndpoint endpoint, MessageHandler handler);
}
