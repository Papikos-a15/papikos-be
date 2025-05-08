package id.ac.ui.cs.advprog.papikosbe.model.transaction;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

class TopUpTest {
    UUID topUpId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    @Test
    void testCreateTopUpSuccess() {
        LocalDateTime now = LocalDateTime.now();
        TopUp topUp = new TopUp(topUpId, userId, new BigDecimal("50.00"), now);

        assertEquals(topUpId, topUp.getId());
        assertEquals(userId, topUp.getUserId());
        assertEquals(new BigDecimal("50.00"), topUp.getAmount());
        assertEquals(now, topUp.getTimestamp());
    }

    @Test
    void testCreateTopUpNegativeAmount() {
        LocalDateTime now = LocalDateTime.now();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new TopUp(topUpId, userId, new BigDecimal("-50.00"), now);
        });

        assertEquals("Top-up amount must be positive", exception.getMessage());
    }
}
