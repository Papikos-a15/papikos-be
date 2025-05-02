package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.factory.TransactionFactory;
import id.ac.ui.cs.advprog.papikosbe.model.Transaction;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    TransactionFactory transactionFactory;

    @InjectMocks
    TransactionServiceImpl transactionService;

    Transaction transaction;
    UUID userId;
    BigDecimal amount;
    TransactionType type;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        amount = new BigDecimal("75.00");
        type = TransactionType.PAYMENT;

        transaction = new Transaction(UUID.randomUUID(), userId, amount, type, LocalDateTime.now());
    }

    @Test
    void testCreateTransaction() {
        when(transactionFactory.createTransaction(userId, amount, type)).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(userId, amount, type);

        assertNotNull(result);
        assertEquals(transaction.getUserId(), result.getUserId());
        assertEquals(transaction.getAmount(), result.getAmount());
    }

    @Test
    void testFindAllTransactions() {
        when(transactionFactory.createTransaction(userId, amount, type)).thenReturn(transaction);
        transactionService.createTransaction(userId, amount, type);

        List<Transaction> allTransactions = transactionService.findAll();
        assertEquals(1, allTransactions.size());
        assertEquals(transaction, allTransactions.getFirst());
    }

    @Test
    void testFindTransactionById() {
        when(transactionFactory.createTransaction(userId, amount, type)).thenReturn(transaction);
        transactionService.createTransaction(userId, amount, type);

        Transaction found = transactionService.findById(transaction.getId());
        assertEquals(transaction, found);
    }

    @Test
    void testFindAllTransactionsByUserId() {
        when(transactionFactory.createTransaction(userId, amount, type)).thenReturn(transaction);
        transactionService.createTransaction(userId, amount, type);

        List<Transaction> userTransactions = transactionService.findAllByUserId(userId);
        assertFalse(userTransactions.isEmpty());
        assertEquals(userId, userTransactions.getFirst().getUserId());
    }

    @Test
    void testFindTransactionByType(){
        when(transactionFactory.createTransaction(userId, amount, type)).thenReturn(transaction);
        transactionService.createTransaction(userId, amount, type);

        List<Transaction> paymentTransactions = transactionService.findByType(type);
        assertFalse(paymentTransactions.isEmpty());
        assertEquals(type, paymentTransactions.getFirst().getType());
    }

    @Test
    void testFindTransactionByDate(){
        when(transactionFactory.createTransaction(userId, amount, type)).thenReturn(transaction);
        transactionService.createTransaction(userId, amount, type);

        assertTrue(transactionService.findByDate(transaction.getTimestamp()).contains(transaction));
    }
}
