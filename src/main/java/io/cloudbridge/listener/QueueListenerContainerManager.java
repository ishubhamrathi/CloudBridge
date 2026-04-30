package io.cloudbridge.listener;

import io.cloudbridge.core.messaging.Acknowledgement;
import io.cloudbridge.core.messaging.CloudMessage;
import io.cloudbridge.core.messaging.QueueConsumer;
import io.cloudbridge.core.messaging.QueueConsumerFactory;
import io.cloudbridge.retry.RetryExecutor;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;

public class QueueListenerContainerManager implements SmartInitializingSingleton, DisposableBean {

    private final QueueListenerRegistry registry;
    private final QueueConsumerFactory consumerFactory;
    private final QueueListenerMethodInvoker methodInvoker;
    private final RetryExecutor retryExecutor;
    private final Executor executor;
    private final CopyOnWriteArrayList<QueueConsumer> consumers = new CopyOnWriteArrayList<>();

    public QueueListenerContainerManager(
            QueueListenerRegistry registry,
            QueueConsumerFactory consumerFactory,
            QueueListenerMethodInvoker methodInvoker,
            RetryExecutor retryExecutor,
            Executor executor
    ) {
        this.registry = registry;
        this.consumerFactory = consumerFactory;
        this.methodInvoker = methodInvoker;
        this.retryExecutor = retryExecutor;
        this.executor = executor;
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (QueueListenerEndpoint endpoint : registry.getEndpoints()) {
            QueueConsumer consumer = consumerFactory.create(endpoint, (message, acknowledgement) -> dispatch(endpoint, message, acknowledgement));
            consumer.start();
            consumers.add(consumer);
        }
    }

    public List<QueueConsumer> getConsumers() {
        return List.copyOf(consumers);
    }

    private void dispatch(QueueListenerEndpoint endpoint, CloudMessage message, Acknowledgement acknowledgement) {
        try {
            executor.execute(() -> retryExecutor.execute(endpoint.queue(), message, acknowledgement, () ->
                    methodInvoker.invoke(endpoint, message, acknowledgement)));
        } catch (RejectedExecutionException ex) {
            acknowledgement.nack(ex);
        }
    }

    @Override
    public void destroy() {
        for (QueueConsumer consumer : consumers) {
            consumer.stop();
        }
        consumers.clear();
    }
}
