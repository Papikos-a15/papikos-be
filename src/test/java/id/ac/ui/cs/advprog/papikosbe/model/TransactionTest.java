package id.ac.ui.cs.advprog.papikosbe.model;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

class TransactionTest {
    UUID transactionId = UUID.randomUUID();
    UUID transactionId2 = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    
    @Test
    void testCreateTransactionSuccess() {
        Transaction transaction = new Transaction(
                transactionId,
                userId,
                new BigDecimal("500.00"),
                TransactionType.TOP_UP,
                LocalDateTime.now()
        );

        assertNotNull(transaction);
        assertEquals(transactionId, transaction.getId());
        assertEquals(userId, transaction.getUserId());
        assertEquals(new BigDecimal("500.00"), transaction.getAmount());
        assertEquals(TransactionType.TOP_UP, transaction.getType());
        assertNotNull(transaction.getTimestamp());
    }

    @Test
    void testCreateTransactionZeroOrNegativeAmount() {
        LocalDateTime now = LocalDateTime.now();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(transactionId, userId, new BigDecimal("-100.00"), TransactionType.PAYMENT, now);
        });

        assertEquals("Transaction amount must be positive", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(transactionId2, userId, BigDecimal.ZERO, TransactionType.TOP_UP, now);
        });

        assertEquals("Transaction amount must be positive", exception.getMessage());
    }

    @Test
    void testCreateTransactionNullType() {
        LocalDateTime now = LocalDateTime.now();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(transactionId, userId, new BigDecimal("100.00"), null, now);
        });

        assertEquals("Transaction type cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreateTransactionNullTimestamp() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(transactionId, userId, new BigDecimal("100.00"), TransactionType.TOP_UP, null);
        });

        assertEquals("Timestamp cannot be null", exception.getMessage());
    }

}