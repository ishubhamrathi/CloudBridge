package io.cloudbridge.core.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class SendOptionsTest {

    @Test
    void storesDelay() {
        SendOptions options = new SendOptions(Duration.ofSeconds(5));

        assertEquals(Duration.ofSeconds(5), options.delay());
    }

    @Test
    void rejectsNegativeDelay() {
        assertThrows(IllegalArgumentException.class, () -> new SendOptions(Duration.ofSeconds(-1)));
    }
}
