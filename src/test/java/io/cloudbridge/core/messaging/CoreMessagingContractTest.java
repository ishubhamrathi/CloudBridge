package io.cloudbridge.core.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class CoreMessagingContractTest {

    @Test
    void cloudMessageRejectsNullPayloadAndHeaders() {
        assertThrows(NullPointerException.class, () -> new CloudMessage(null));
        assertThrows(NullPointerException.class, () -> new CloudMessage("payload", null));
    }

    @Test
    void sendOptionsRejectsNullDelay() {
        assertThrows(NullPointerException.class, () -> new SendOptions(null));
    }

    @Test
    void queueClientDefaultSendIsARealDefaultMethod() throws NoSuchMethodException {
        Method method = QueueClient.class.getMethod("send", String.class, CloudMessage.class);

        assertTrue(method.isDefault());
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    void queueListenerIsRuntimeVisibleAndMethodTargeted() {
        Retention retention = QueueListener.class.getAnnotation(Retention.class);
        Target target = QueueListener.class.getAnnotation(Target.class);

        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
        assertNotNull(target);
        assertEquals(1, target.value().length);
        assertEquals(ElementType.METHOD, target.value()[0]);
    }

    @Test
    void cloudCapabilitiesAndAcknowledgementExposeStableContracts() throws NoSuchMethodException {
        Method delayed = CloudCapabilities.class.getMethod("supportsDelayedDelivery");
        Method acknowledgement = CloudCapabilities.class.getMethod("supportsAcknowledgement");
        Method dlq = CloudCapabilities.class.getMethod("supportsDeadLetterQueue");
        Method ack = Acknowledgement.class.getMethod("ack");
        Method nack = Acknowledgement.class.getMethod("nack", Throwable.class);

        assertTrue(Modifier.isAbstract(delayed.getModifiers()));
        assertTrue(Modifier.isAbstract(acknowledgement.getModifiers()));
        assertTrue(Modifier.isAbstract(dlq.getModifiers()));
        assertEquals(boolean.class, delayed.getReturnType());
        assertEquals(boolean.class, acknowledgement.getReturnType());
        assertEquals(boolean.class, dlq.getReturnType());
        assertEquals(void.class, ack.getReturnType());
        assertEquals(void.class, nack.getReturnType());
    }
}
