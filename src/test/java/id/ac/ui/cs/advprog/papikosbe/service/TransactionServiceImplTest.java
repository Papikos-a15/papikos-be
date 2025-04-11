package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Transaction;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
        transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setUserId(UUID.randomUUID());
        transaction.setAmount(new BigDecimal("75.00"));
        transaction.setType(TransactionType.PAYMENT);
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