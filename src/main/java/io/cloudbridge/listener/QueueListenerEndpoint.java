package io.cloudbridge.listener;

import java.lang.reflect.Method;
import java.util.Objects;

public record QueueListenerEndpoint(
        String beanName,
        Object bean,
        Method method,
        String queue,
        int concurrency
) {

    public QueueListenerEndpoint {
        beanName = Objects.requireNonNull(beanName, "beanName must not be null");
        bean = Objects.requireNonNull(bean, "bean must not be null");
        method = Objects.requireNonNull(method, "method must not be null");
        if (queue == null || queue.isBlank()) {
            throw new IllegalArgumentException("queue must not be blank");
        }
        if (concurrency < 1) {
            throw new IllegalArgumentException("concurrency must be greater than zero");
        }
    }
}
