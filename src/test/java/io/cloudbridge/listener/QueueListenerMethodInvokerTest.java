package io.cloudbridge.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.cloudbridge.core.messaging.Acknowledgement;
import io.cloudbridge.core.messaging.CloudMessage;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class QueueListenerMethodInvokerTest {

    @Test
    void invokesListenerWithCloudMessageAndAcknowledgement() throws Exception {
        SampleBean bean = new SampleBean();
        Method method = SampleBean.class.getDeclaredMethod("handle", CloudMessage.class, Acknowledgement.class);
        QueueListenerEndpoint endpoint = new QueueListenerEndpoint("sampleBean", bean, method, "orders", 2);

        new QueueListenerMethodInvoker().invoke(endpoint, new CloudMessage("payload"), new TestAcknowledgement());

        assertEquals("payload", bean.seenPayload);
        assertEquals(1, bean.invocations);
    }

    @Test
    void invokesListenerWithPayloadString() throws Exception {
        StringBean bean = new StringBean();
        Method method = StringBean.class.getDeclaredMethod("handle", String.class);
        QueueListenerEndpoint endpoint = new QueueListenerEndpoint("stringBean", bean, method, "orders", 1);

        new QueueListenerMethodInvoker().invoke(endpoint, new CloudMessage("payload"), new TestAcknowledgement());

        assertEquals("payload", bean.seenPayload);
    }

    @Test
    void rejectsUnsupportedParameterType() throws NoSuchMethodException {
        UnsupportedBean bean = new UnsupportedBean();
        Method method = UnsupportedBean.class.getDeclaredMethod("handle", Integer.class);
        QueueListenerEndpoint endpoint = new QueueListenerEndpoint("unsupportedBean", bean, method, "orders", 1);

        assertThrows(IllegalStateException.class, () ->
                new QueueListenerMethodInvoker().invoke(endpoint, new CloudMessage("payload"), new TestAcknowledgement()));
    }

    static final class SampleBean {
        private int invocations;
        private String seenPayload;

        void handle(CloudMessage message, Acknowledgement acknowledgement) {
            invocations++;
            seenPayload = message.payload();
        }
    }

    static final class StringBean {
        private String seenPayload;

        void handle(String payload) {
            seenPayload = payload;
        }
    }

    static final class UnsupportedBean {
        void handle(Integer payload) {
        }
    }

    static final class TestAcknowledgement implements Acknowledgement {
        @Override
        public void ack() {
        }

        @Override
        public void nack(Throwable cause) {
        }
    }
}
