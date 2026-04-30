package io.cloudbridge.listener;

import io.cloudbridge.core.messaging.Acknowledgement;
import io.cloudbridge.core.messaging.CloudMessage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class QueueListenerMethodInvoker {

    public void invoke(QueueListenerEndpoint endpoint, CloudMessage message, Acknowledgement acknowledgement) throws Exception {
        Method method = endpoint.method();
        method.setAccessible(true);
        Object[] arguments = resolveArguments(method, message, acknowledgement);
        try {
            method.invoke(endpoint.bean(), arguments);
        } catch (InvocationTargetException ex) {
            Throwable target = ex.getTargetException();
            if (target instanceof Exception exception) {
                throw exception;
            }
            throw new IllegalStateException("Listener invocation failed", target);
        }
    }

    private Object[] resolveArguments(Method method, CloudMessage message, Acknowledgement acknowledgement) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == CloudMessage.class) {
                arguments[i] = message;
                continue;
            }
            if (parameterType == String.class) {
                arguments[i] = message.payload();
                continue;
            }
            if (parameterType == Acknowledgement.class) {
                arguments[i] = acknowledgement;
                continue;
            }
            throw new IllegalStateException("Unsupported @QueueListener parameter type: " + parameterType.getName());
        }
        return arguments;
    }
}
