package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.Transaction;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionRepositoryTest {

    private TransactionRepository transactionRepository;
    private UUID userId;
    private Transaction topUp;
    private Transaction payment;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        UUID userId = UUID.randomUUID();
        Transaction topUp = new Transaction(UUID.randomUUID(), userId, new BigDecimal("500.00"), TransactionType.TOP_UP, LocalDateTime.now());
        Transaction payment = new Transaction(UUID.randomUUID(), userId, new BigDecimal("200.00"), TransactionType.PAYMENT, LocalDateTime.now());
    }

    @Test
    void testFindAllByUserIdSuccess() {
        when(transactionRepository.findAllByUserId(userId)).thenReturn(List.of(topUp, payment));

        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);

        assertFalse(transactions.isEmpty());
        assertEquals(2, transactions.size());
        assertEquals(userId, transactions.get(0).getUserId());
        assertEquals(TransactionType.TOP_UP, transactions.get(0).getType());
    }

    @Test
    void testFindAllByUserIdEmpty() {
        UUID userId = UUID.randomUUID();

        when(transactionRepository.findAllByUserId(userId)).thenReturn(List.of());

        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);

        assertTrue(transactions.isEmpty());
    }

    @Test
    void testFindAllByUserIdAndTypeSuccess() {
        when(transactionRepository.findAllByUserIdAndTransactionType(userId, TransactionType.PAYMENT)).thenReturn(List.of(payment));

        List<Transaction> transactions = transactionRepository.findAllByUserIdAndTransactionType(userId, TransactionType.PAYMENT);

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        assertEquals(TransactionType.PAYMENT, transactions.get(0).getType());
    }

    @Test
    void testFindAllByUserIdAndTypeEmpty() {
        when(transactionRepository.findAllByUserIdAndTransactionType(userId, TransactionType.TOP_UP)).thenReturn(List.of());

        List<Transaction> transactions = transactionRepository.findAllByUserIdAndTransactionType(userId, TransactionType.TOP_UP);

        assertTrue(transactions.isEmpty());
    }
}