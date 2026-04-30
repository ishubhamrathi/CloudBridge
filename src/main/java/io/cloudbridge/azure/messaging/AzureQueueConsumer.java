package io.cloudbridge.azure.messaging;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import io.cloudbridge.core.messaging.CloudMessage;
import io.cloudbridge.core.messaging.MessageHandler;
import io.cloudbridge.core.messaging.QueueConsumer;
import io.cloudbridge.listener.QueueListenerEndpoint;
import java.util.LinkedHashMap;
import java.util.Map;

public class AzureQueueConsumer implements QueueConsumer {

    private final ServiceBusProcessorClient processorClient;

    public AzureQueueConsumer(ServiceBusClientBuilder clientBuilder, QueueListenerEndpoint endpoint, MessageHandler handler) {
        this.processorClient = clientBuilder.processor()
                .queueName(endpoint.queue())
                .disableAutoComplete()
                .maxConcurrentCalls(endpoint.concurrency())
                .processMessage(context -> {
                    Map<String, String> headers = new LinkedHashMap<>();
                    context.getMessage().getApplicationProperties().forEach((key, value) -> headers.put(key, String.valueOf(value)));
                    CloudMessage message = new CloudMessage(context.getMessage().getBody().toString(), headers);
                    AzureServiceBusAcknowledgement acknowledgement = new AzureServiceBusAcknowledgement(context);
                    try {
                        handler.handle(message, acknowledgement);
                    } catch (Exception ex) {
                        acknowledgement.nack(ex);
                    }
                })
                .processError(errorContext -> {
                })
                .buildProcessorClient();
    }

    @Override
    public void start() {
        processorClient.start();
    }

    @Override
    public void stop() {
        processorClient.close();
    }
}
