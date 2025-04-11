package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Transaction;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @InjectMocks
    TransactionServiceImpl transactionService;

    Transaction transaction;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("75.00");
        TransactionType type = TransactionType.PAYMENT;
        LocalDateTime date = LocalDateTime.now();

        transaction = new Transaction(id, userId, amount, type, date);
    }

    @Test
    void testCreateTransaction() {
        assertEquals(transaction, transactionService.create(transaction));
    }

    @Test
    void testFindAllTransactions() {
        transactionService.create(transaction);
        assertEquals(transaction, transactionService.findAll().getFirst());
    }

    @Test
    void testFindTransactionById() {
        transactionService.create(transaction);
        assertEquals(transaction, transactionService.findById(transaction.getId()));
    }

    @Test
    void testFindAllTransactionsByUserId() {
        transactionService.create(transaction);
        List<Transaction> transactions = transactionService.findAllByUserId(transaction.getUserId());
        assertFalse(transactions.isEmpty());
        assertEquals(transaction.getUserId(), transactions.getFirst().getUserId());
    }
}