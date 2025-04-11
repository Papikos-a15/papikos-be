package id.ac.ui.cs.advprog.papikosbe.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

class TransactionTest {

    @Test
    void testCreateTransactionSuccess() {
        Transaction transaction = new Transaction(
                1L,
                10L,
                new BigDecimal("500.00"),
                "TOP_UP",
                LocalDateTime.now()
        );

        assertNotNull(transaction);
        assertEquals(1L, transaction.getId());
        assertEquals(10L, transaction.getUserId());
        assertEquals(new BigDecimal("500.00"), transaction.getAmount());
        assertEquals("TOP_UP", transaction.getType());
        assertNotNull(transaction.getTimestamp());
    }

    @Test
    void testCreateTransactionZeroOrNegativeAmount() {
        LocalDateTime now = LocalDateTime.now();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(1L, 10L, new BigDecimal("-100.00"), "PAYMENT", now);
        });

        assertEquals("Transaction amount must be positive", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(2L, 10L, BigDecimal.ZERO, "TOP_UP", now);
        });

        assertEquals("Transaction amount must be positive", exception.getMessage());
    }

    @Test
    void testCreateTransactionNullOrEmptyType() {
        LocalDateTime now = LocalDateTime.now();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(1L, 10L, new BigDecimal("100.00"), null, now);
        });

        assertEquals("Transaction type cannot be null or empty", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(2L, 10L, new BigDecimal("100.00"), "", now);
        });

        assertEquals("Transaction type cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreateTransactionNullTimestamp() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(1L, 10L, new BigDecimal("100.00"), "TOP_UP", null);
        });

        assertEquals("Timestamp cannot be null", exception.getMessage());
    }

}