package io.cloudbridge.listener;

import io.cloudbridge.config.CloudBridgeProperties;
import io.cloudbridge.core.messaging.QueueListener;
import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class QueueListenerAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final QueueListenerRegistry registry;
    private final CloudBridgeProperties properties;

    public QueueListenerAnnotationBeanPostProcessor(QueueListenerRegistry registry, CloudBridgeProperties properties) {
        this.registry = registry;
        this.properties = properties;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> userClass = ClassUtils.getUserClass(bean);
        ReflectionUtils.doWithMethods(userClass, method -> registerEndpoint(beanName, bean, method));
        return bean;
    }

    private void registerEndpoint(String beanName, Object bean, Method method) {
        QueueListener annotation = AnnotationUtils.findAnnotation(method, QueueListener.class);
        if (annotation == null) {
            return;
        }
        int concurrency = annotation.concurrency() > 0
                ? annotation.concurrency()
                : properties.getMessaging().getListener().getDefaultConcurrency();
        registry.register(new QueueListenerEndpoint(beanName, bean, method, annotation.value(), concurrency));
    }
}
