package id.ac.ui.cs.advprog.papikosbe.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

class PaymentTest {
    UUID paymentId = UUID.randomUUID();
    UUID paymentId2 = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    
    @Test
    void testCreatePaymentSuccess() {
        Payment payment = new Payment(
                paymentId,
                userId,
                ownerId,
                new BigDecimal("150.00"),
                LocalDateTime.now()
        );

        assertNotNull(payment);
        assertEquals(paymentId, payment.getId());
        assertEquals(userId, payment.getUserId());
        assertEquals(ownerId, payment.getOwnerId());
        assertEquals(new BigDecimal("150.00"), payment.getAmount());
        assertNotNull(payment.getTimestamp());
    }

    @Test
    void testCreatePaymentZeroOrNegativeAmount() {
        LocalDateTime now = LocalDateTime.now();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Payment(paymentId, userId, ownerId, new BigDecimal("-10.00"), now);
        });

        assertEquals("Payment amount must be positive", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            new Payment(paymentId2, userId, ownerId, BigDecimal.ZERO, now);
        });

        assertEquals("Payment amount must be positive", exception.getMessage());
    }

    @Test
    void testCreatePaymentNullTimestamp() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Payment(paymentId, userId, ownerId, new BigDecimal("100.00"), null);
        });

        assertEquals("Timestamp cannot be null", exception.getMessage());
    }
}

