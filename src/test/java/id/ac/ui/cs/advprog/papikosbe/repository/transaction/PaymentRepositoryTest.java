package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.factory.DefaultPaymentFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        paymentRepository = Mockito.mock(PaymentRepository.class);

        paymentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        paymentFactory = new DefaultPaymentFactory();
        payment1 = new Payment(paymentId, userId, ownerId, new BigDecimal("250.00"), java.time.LocalDateTime.now());
        payment2 = paymentFactory.createPayment(userId, UUID.randomUUID(), new BigDecimal("300.00"));
    }

    @Test
    void testSavePaymentSuccess() {
        when(paymentRepository.save(payment1)).thenReturn(payment1);

        Payment savedPayment = paymentRepository.save(payment1);

        assertNotNull(savedPayment);
        assertEquals(payment1.getAmount(), savedPayment.getAmount());
    }

    @Test
    void testFindPaymentByIdSuccess() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment1));

        Optional<Payment> found = paymentRepository.findById(paymentId);

        assertTrue(found.isPresent());
        assertEquals(paymentId, found.get().getId());
    }

    @Test
    void testFindPaymentByIdNotFound() {
        UUID unknownPaymentId = UUID.randomUUID();
        when(paymentRepository.findById(unknownPaymentId)).thenReturn(Optional.empty());

        Optional<Payment> found = paymentRepository.findById(unknownPaymentId);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUserIdSuccess() {
        when(paymentRepository.findByUserId(userId)).thenReturn(List.of(payment1, payment2));

        List<Payment> payments = paymentRepository.findByUserId(userId);

        assertFalse(payments.isEmpty());
        assertEquals(2, payments.size());
        assertEquals(userId, payments.get(0).getUserId());
    }

    @Test
    void testFindByUserIdNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        when(paymentRepository.findByUserId(unknownUserId)).thenReturn(List.of());

        List<Payment> payments = paymentRepository.findByUserId(unknownUserId);

        assertTrue(payments.isEmpty());
    }
}
