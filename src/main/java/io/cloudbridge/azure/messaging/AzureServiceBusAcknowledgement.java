package io.cloudbridge.azure.messaging;

import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import io.cloudbridge.core.messaging.Acknowledgement;
import java.util.concurrent.atomic.AtomicBoolean;

public class AzureServiceBusAcknowledgement implements Acknowledgement {

    private final ServiceBusReceivedMessageContext context;
    private final AtomicBoolean completed = new AtomicBoolean();

    public AzureServiceBusAcknowledgement(ServiceBusReceivedMessageContext context) {
        this.context = context;
    }

    @Override
    public void ack() {
        if (completed.compareAndSet(false, true)) {
            context.complete();
        }
    }

    @Override
    public void nack(Throwable cause) {
        if (completed.compareAndSet(false, true)) {
            context.abandon();
        }
    }
}
