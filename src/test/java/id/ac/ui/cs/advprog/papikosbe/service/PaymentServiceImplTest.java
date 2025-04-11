package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Payment;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @InjectMocks
    PaymentServiceImpl paymentService;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("50.00"));
        payment.setType(TransactionType.PAYMENT);
    }

    @Test
    void testCreatePayment() {
        assertEquals(payment, paymentService.create(payment));
    }

    @Test
    void testFindAllPayments() {
        paymentService.create(payment);
        assertEquals(payment, paymentService.findAll().getFirst());
    }

    @Test
    void testFindPaymentById() {
        paymentService.create(payment);
        assertEquals(payment, paymentService.findById(payment.getId()));
    }
}