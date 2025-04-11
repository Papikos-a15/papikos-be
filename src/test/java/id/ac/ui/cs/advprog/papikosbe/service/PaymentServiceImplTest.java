package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Payment;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @InjectMocks
    PaymentServiceImpl paymentService;

    Payment payment;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");
        LocalDateTime date = LocalDateTime.now();

        payment = new Payment(id, userId, ownerId, amount, date);
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