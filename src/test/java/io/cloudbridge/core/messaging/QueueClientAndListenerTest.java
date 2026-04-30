package io.cloudbridge.core.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Method;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class QueueClientAndListenerTest {

    @Test
    void defaultSendUsesNoOptions() {
        RecordingQueueClient client = new RecordingQueueClient();
        CloudMessage message = new CloudMessage("payload");

        client.send("orders", message);

        assertEquals("orders", client.queue);
        assertSame(message, client.message);
        assertNull(client.options);
    }

    @Test
    void explicitSendOptionsArePassedThrough() {
        RecordingQueueClient client = new RecordingQueueClient();
        SendOptions options = new SendOptions(Duration.ofSeconds(5));

        client.send("orders", new CloudMessage("payload"), options);

        assertSame(options, client.options);
    }

    @Test
    void queueListenerIsDiscoverableAtRuntime() throws NoSuchMethodException {
        Method method = SampleListener.class.getDeclaredMethod("handle", CloudMessage.class, Acknowledgement.class);
        QueueListener listener = method.getAnnotation(QueueListener.class);

        assertNotNull(listener);
        assertEquals("orders", listener.value());
        assertEquals(4, listener.concurrency());
    }

    @Test
    void queueListenerDefaultsRemainMinimal() throws NoSuchMethodException {
        Method method = SampleListener.class.getDeclaredMethod("handleDefault", CloudMessage.class);
        QueueListener listener = method.getAnnotation(QueueListener.class);

        assertNotNull(listener);
        assertEquals(1, listener.concurrency());
    }

    private static final class RecordingQueueClient implements QueueClient {
        private String queue;
        private CloudMessage message;
        private SendOptions options;

        @Override
        public void send(String queue, CloudMessage message, SendOptions options) {
            this.queue = queue;
            this.message = message;
            this.options = options;
        }
    }

    private static final class SampleListener {

        @QueueListener(value = "orders", concurrency = 4)
        void handle(CloudMessage message, Acknowledgement acknowledgement) {
        }

        @QueueListener("default-orders")
        void handleDefault(CloudMessage message) {
        }
    }
}
