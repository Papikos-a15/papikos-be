package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @BeforeEach
    void setUp() {
        paymentRepository = Mockito.mock(PaymentRepository.class);

        paymentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        payment1 = new Payment(paymentId, userId, ownerId, new BigDecimal("250.00"), LocalDateTime.now());
        payment2 = new Payment(UUID.randomUUID(), userId, UUID.randomUUID(), new BigDecimal("300.00"), LocalDateTime.now());
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