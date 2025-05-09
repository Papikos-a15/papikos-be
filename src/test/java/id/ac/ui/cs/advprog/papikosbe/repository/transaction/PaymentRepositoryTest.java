package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.factory.DefaultPaymentFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRepositoryTest {

    private PaymentRepository paymentRepository;

    private UUID paymentId;
    private UUID userId;
    private UUID ownerId;
    private Payment payment1;
    private Payment payment2;

    private DefaultPaymentFactory paymentFactory;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();

        paymentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        paymentFactory = new DefaultPaymentFactory();
        payment1 = new Payment(paymentId, userId, ownerId, new BigDecimal("250.00"), LocalDateTime.now());
        payment2 = paymentFactory.createPayment(userId, UUID.randomUUID(), new BigDecimal("300.00"));
    }

    @Test
    void testCreatePaymentWithNullId() {
        Payment newPayment = new Payment(null, userId, ownerId, new BigDecimal("500.00"), LocalDateTime.now());

        Payment created = paymentRepository.create(newPayment);

        assertNotNull(created.getId());
        assertEquals(new BigDecimal("500.00"), created.getAmount());
    }

    @Test
    void testSaveNewPayment() {
        Payment saved = paymentRepository.save(payment1);

        assertNotNull(saved);
        assertEquals(payment1.getId(), saved.getId());
    }

    @Test
    void testSaveOverwriteExistingPayment() {
        paymentRepository.save(payment1);
        Payment updated = new Payment(payment1.getId(), userId, ownerId, new BigDecimal("1000.00"), payment1.getTimestamp());

        Payment saved = paymentRepository.save(updated);

        Optional<Payment> found = paymentRepository.findById(payment1.getId());
        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("1000.00"), found.get().getAmount());
    }

    @Test
    void testFindByIdSuccess() {
        paymentRepository.save(payment1);

        Optional<Payment> found = paymentRepository.findById(payment1.getId());

        assertTrue(found.isPresent());
        assertEquals(payment1.getId(), found.get().getId());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Payment> found = paymentRepository.findById(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUserIdSuccess() {
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        List<Payment> payments = paymentRepository.findByUserId(userId);

        assertEquals(2, payments.size());
        assertTrue(payments.stream().allMatch(p -> p.getUserId().equals(userId)));
    }

    @Test
    void testFindByUserIdNotFound() {
        List<Payment> payments = paymentRepository.findByUserId(UUID.randomUUID());

        assertTrue(payments.isEmpty());
    }

    @Test
    void testFindAll() {
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        Iterator<Payment> iterator = paymentRepository.findAll();

        List<Payment> all = new ArrayList<>();
        iterator.forEachRemaining(all::add);

        assertEquals(2, all.size());
    }

    @Test
    void testDeleteExistingPayment() {
        paymentRepository.save(payment1);

        paymentRepository.delete(payment1.getId());

        Optional<Payment> found = paymentRepository.findById(payment1.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteNonExistentPayment() {
        // Should not throw any error
        paymentRepository.delete(UUID.randomUUID());
    }
}

