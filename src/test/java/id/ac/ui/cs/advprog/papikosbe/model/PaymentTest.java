package id.ac.ui.cs.advprog.papikosbe.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

class PaymentTest {

    @Test
    void testCreatePaymentSuccess() {
        Payment payment = new Payment(
                1L,
                10L,
                20L,
                new BigDecimal("150.00"),
                LocalDateTime.now()
        );

        assertNotNull(payment);
        assertEquals(1L, payment.getId());
        assertEquals(10L, payment.getUserId());
        assertEquals(20L, payment.getOwnerId());
        assertEquals(new BigDecimal("150.00"), payment.getAmount());
        assertNotNull(payment.getTimestamp());
    }

    @Test
    void testCreatePaymentZeroOrNegativeAmount() {
        LocalDateTime now = LocalDateTime.now();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Payment(1L, 10L, 20L, new BigDecimal("-10.00"), now);
        });

        assertEquals("Payment amount must be positive", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            new Payment(2L, 10L, 20L, BigDecimal.ZERO, now);
        });

        assertEquals("Payment amount must be positive", exception.getMessage());
    }

    @Test
    void testCreatePaymentNullTimestamp() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Payment(1L, 10L, 20L, new BigDecimal("100.00"), null);
        });

        assertEquals("Timestamp cannot be null", exception.getMessage());
    }
}

