package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.*;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private Tenant tenant;
    private Owner owner;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder()
                .email("tenant@example.com")
                .password("pass")
                .build();
        owner = Owner.builder()
                .email("owner@example.com")
                .password("pass")
                .build();
        userRepository.save(tenant);
        userRepository.save(owner);
    }

    @Test
    void testFindByDate() {
        Payment payment = new Payment();
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setAmount(new BigDecimal("10000"));
        payment.setStatus(TransactionStatus.COMPLETED);
        payment.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(payment);

        List<Transaction> transactions = transactionRepository.findByDate(LocalDate.now());
        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
    }

    @Test
    void testFindPaymentsByUser() {
        Payment payment = new Payment();
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setAmount(new BigDecimal("10000"));
        payment.setStatus(TransactionStatus.COMPLETED);
        payment.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(payment);

        List<Payment> byTenant = transactionRepository.findPaymentsByUser(tenant.getId());
        List<Payment> byOwner = transactionRepository.findPaymentsByUser(owner.getId());

        assertEquals(1, byTenant.size());
        assertEquals(1, byOwner.size());
    }

    @Test
    void testCountByStatus() {
        Payment payment1 = new Payment();
        payment1.setUser(tenant);
        payment1.setOwner(owner);
        payment1.setAmount(new BigDecimal("5000"));
        payment1.setStatus(TransactionStatus.COMPLETED);
        payment1.setCreatedAt(LocalDateTime.now());

        Payment payment2 = new Payment();
        payment2.setUser(tenant);
        payment2.setOwner(owner);
        payment2.setAmount(new BigDecimal("5000"));
        payment2.setStatus(TransactionStatus.PENDING);
        payment2.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(payment1);
        transactionRepository.save(payment2);

        long completedCount = transactionRepository.countByStatus(TransactionStatus.COMPLETED);
        long pendingCount = transactionRepository.countByStatus(TransactionStatus.PENDING);

        assertEquals(1, completedCount);
        assertEquals(1, pendingCount);
    }

    @Test
    void testFindPaymentsByTenant() {
        Payment payment = new Payment();
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setAmount(new BigDecimal("10000"));
        payment.setStatus(TransactionStatus.COMPLETED);
        payment.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(payment);

        List<Payment> payments = transactionRepository.findPaymentsByTenant(tenant.getId());
        assertEquals(1, payments.size());
        assertEquals(tenant.getId(), payments.get(0).getUser().getId());
    }

    @Test
    void testFindPaymentsByOwner() {
        Payment payment = new Payment();
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setAmount(new BigDecimal("10000"));
        payment.setStatus(TransactionStatus.COMPLETED);
        payment.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(payment);

        List<Payment> payments = transactionRepository.findPaymentsByOwner(owner.getId());
        assertEquals(1, payments.size());
        assertEquals(owner.getId(), payments.get(0).getOwner().getId());
    }

    @Test
    void testFindTopUpsByUser() {
        TopUp topUp = new TopUp();
        topUp.setUser(tenant);
        topUp.setAmount(new BigDecimal("20000"));
        topUp.setStatus(TransactionStatus.COMPLETED);
        topUp.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(topUp);

        List<TopUp> topUps = transactionRepository.findTopUpsByUser(tenant.getId());
        assertEquals(1, topUps.size());
        assertEquals(tenant.getId(), topUps.get(0).getUser().getId());
    }
}
