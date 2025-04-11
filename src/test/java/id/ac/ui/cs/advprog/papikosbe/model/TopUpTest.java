package id.ac.ui.cs.advprog.papikosbe.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

class TopUpTest {

    @Test
    void testCreateTopUpSuccess() {
        LocalDateTime now = LocalDateTime.now();
        TopUp topUp = new TopUp(1L, 10L, new BigDecimal("50.00"), now);

        assertEquals(1L, topUp.getId());
        assertEquals(10L, topUp.getUserId());
        assertEquals(new BigDecimal("50.00"), topUp.getAmount());
        assertEquals(now, topUp.getTimestamp());
    }

    @Test
    void testCreateTopUpNegativeAmount() {
        LocalDateTime now = LocalDateTime.now();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new TopUp(1L, 10L, new BigDecimal("-50.00"), now);
        });

        assertEquals("Top-up amount must be positive", exception.getMessage());
    }
}
