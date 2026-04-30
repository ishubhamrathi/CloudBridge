package io.cloudbridge.azure.messaging;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import io.cloudbridge.core.messaging.MessageHandler;
import io.cloudbridge.core.messaging.QueueConsumer;
import io.cloudbridge.core.messaging.QueueConsumerFactory;
import io.cloudbridge.listener.QueueListenerEndpoint;

public class AzureQueueConsumerFactory implements QueueConsumerFactory {

    private final ServiceBusClientBuilder clientBuilder;

    public AzureQueueConsumerFactory(ServiceBusClientBuilder clientBuilder) {
        this.clientBuilder = clientBuilder;
    }

    @Override
    public QueueConsumer create(QueueListenerEndpoint endpoint, MessageHandler handler) {
        return new AzureQueueConsumer(clientBuilder, endpoint, handler);
    }
}
