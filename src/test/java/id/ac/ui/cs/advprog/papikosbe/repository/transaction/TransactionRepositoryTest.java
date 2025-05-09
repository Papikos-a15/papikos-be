package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionRepositoryTest {

    private TransactionRepository transactionRepository;
    private UUID userId;
    private Transaction topUp;
    private Transaction payment;

    @BeforeEach
    void setUp() {
        transactionRepository = new TransactionRepository(); // Gunakan implementasi nyata, bukan mock
        userId = UUID.randomUUID();

        topUp = new Transaction(UUID.randomUUID(), userId, new BigDecimal("500.00"), TransactionType.TOP_UP, LocalDateTime.now());
        payment = new Transaction(UUID.randomUUID(), userId, new BigDecimal("200.00"), TransactionType.PAYMENT, LocalDateTime.now());

        transactionRepository.save(topUp);
        transactionRepository.save(payment);
    }

    @Test
    void testSaveAndFindById() {
        Transaction transaction = new Transaction(UUID.randomUUID(), userId, new BigDecimal("100.00"), TransactionType.PAYMENT, LocalDateTime.now());
        transactionRepository.save(transaction);

        Optional<Transaction> found = transactionRepository.findById(transaction.getId());

        assertTrue(found.isPresent());
        assertEquals(transaction.getAmount(), found.get().getAmount());
    }

    @Test
    void testFindAll() {
        Iterator<Transaction> all = transactionRepository.findAll();
        List<Transaction> resultList = new ArrayList<>();
        all.forEachRemaining(resultList::add);

        assertEquals(2, resultList.size());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Transaction> result = transactionRepository.findById(UUID.randomUUID());
        assertFalse(result.isPresent());
    }

    @Test
    void testFindAllByUserIdSuccess() {
        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);

        assertFalse(transactions.isEmpty());
        assertEquals(2, transactions.size());
    }

    @Test
    void testFindAllByUserIdEmpty() {
        List<Transaction> transactions = transactionRepository.findAllByUserId(UUID.randomUUID());
        assertTrue(transactions.isEmpty());
    }

    @Test
    void testFindAllByUserIdAndTypeSuccess() {
        List<Transaction> transactions = transactionRepository.findAllByUserIdAndTransactionType(userId, TransactionType.TOP_UP);

        assertEquals(1, transactions.size());
        assertEquals(TransactionType.TOP_UP, transactions.get(0).getType());
    }

    @Test
    void testFindAllByUserIdAndTypeEmpty() {
        List<Transaction> transactions = transactionRepository.findAllByUserIdAndTransactionType(UUID.randomUUID(), TransactionType.TOP_UP);
        assertTrue(transactions.isEmpty());
    }

    @Test
    void testDeleteTransaction() {
        transactionRepository.delete(topUp.getId());

        Optional<Transaction> result = transactionRepository.findById(topUp.getId());
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteTransactionNotFound() {
        // No exception should be thrown even if ID not found
        transactionRepository.delete(UUID.randomUUID());
    }
}
