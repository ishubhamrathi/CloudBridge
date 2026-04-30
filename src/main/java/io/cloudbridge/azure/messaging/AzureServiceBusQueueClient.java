package io.cloudbridge.azure.messaging;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import io.cloudbridge.core.messaging.CloudMessage;
import io.cloudbridge.core.messaging.QueueClient;
import io.cloudbridge.core.messaging.SendOptions;
import java.time.OffsetDateTime;

public class AzureServiceBusQueueClient implements QueueClient {

    private final ServiceBusClientBuilder clientBuilder;

    public AzureServiceBusQueueClient(ServiceBusClientBuilder clientBuilder) {
        this.clientBuilder = clientBuilder;
    }

    @Override
    public void send(String queue, CloudMessage message, SendOptions options) {
        ServiceBusMessage serviceBusMessage = new ServiceBusMessage(message.payload());
        serviceBusMessage.getApplicationProperties().putAll(message.headers());
        if (options != null && options.delay() != null && !options.delay().isZero()) {
            serviceBusMessage.setScheduledEnqueueTime(OffsetDateTime.now().plus(options.delay()));
        }
        try (ServiceBusSenderClient sender = clientBuilder.sender().queueName(queue).buildClient()) {
            sender.sendMessage(serviceBusMessage);
        }
    }
}
