package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.factory.PaymentFactory;
import id.ac.ui.cs.advprog.papikosbe.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    @Mock
    PaymentFactory paymentFactory;

    @InjectMocks
    PaymentServiceImpl paymentService;

    Payment mockPayment;
    UUID userId;
    UUID ownerId;
    BigDecimal amount;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        amount = new BigDecimal("50.00");
        mockPayment = new Payment(UUID.randomUUID(), userId, ownerId, amount, LocalDateTime.now());
    }

    @Test
    void testCreatePayment_UsingFactory() {
        when(paymentFactory.createPayment(userId, ownerId, amount)).thenReturn(mockPayment);

        Payment createdPayment = paymentService.createPayment(userId, ownerId, amount);

        assertEquals(mockPayment, createdPayment);
        assertEquals(userId, createdPayment.getUserId());
        assertEquals(ownerId, createdPayment.getOwnerId());
        assertEquals(amount, createdPayment.getAmount());
    }

    @Test
    void testFindAllPayments() {
        when(paymentFactory.createPayment(userId, ownerId, amount)).thenReturn(mockPayment);

        paymentService.createPayment(userId, ownerId, amount);
        assertTrue(paymentService.findAll().contains(mockPayment));
    }

    @Test
    void testFindPaymentById() {
        when(paymentFactory.createPayment(userId, ownerId, amount)).thenReturn(mockPayment);

        paymentService.createPayment(userId, ownerId, amount);
        assertEquals(mockPayment, paymentService.findById(mockPayment.getId()));
    }

    @Test
    void testFindPaymentByUserId() {
        when(paymentFactory.createPayment(userId, ownerId, amount)).thenReturn(mockPayment);

        paymentService.createPayment(userId, ownerId, amount);
        assertTrue(paymentService.findByUserId(mockPayment.getUserId()).contains(mockPayment));
    }

    @Test
    void testFindPaymentByDate() {
        when(paymentFactory.createPayment(userId, ownerId, amount)).thenReturn(mockPayment);

        paymentService.createPayment(userId, ownerId, amount);
        assertTrue(paymentService.findByDate(mockPayment.getTimestamp()).contains(mockPayment));
    }
}