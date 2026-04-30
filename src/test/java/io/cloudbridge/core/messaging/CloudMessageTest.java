package io.cloudbridge.core.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CloudMessageTest {

    @Test
    void rejectsBlankPayload() {
        assertThrows(IllegalArgumentException.class, () -> new CloudMessage(" "));
    }

    @Test
    void createsImmutableHeaderSnapshot() {
        Map<String, String> headers = new HashMap<>();
        headers.put("type", "order.created");

        CloudMessage message = new CloudMessage("payload", headers);
        headers.put("type", "mutated");

        assertEquals("payload", message.payload());
        assertEquals("order.created", message.headers().get("type"));
        assertThrows(UnsupportedOperationException.class, () -> message.headers().put("x", "y"));
    }

    @Test
    void copiesEmptyHeadersWhenOnlyPayloadIsProvided() {
        CloudMessage message = new CloudMessage("payload");

        assertEquals("payload", message.payload());
        assertEquals(Map.of(), message.headers());
    }

    @Test
    void copiesHeaderMapRatherThanRetainingInputReference() {
        Map<String, String> headers = new HashMap<>();
        headers.put("traceId", "123");

        CloudMessage message = new CloudMessage("payload", headers);

        assertNotSame(headers, message.headers());
    }
}
