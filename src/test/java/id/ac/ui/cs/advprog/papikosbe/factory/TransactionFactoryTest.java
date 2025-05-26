package id.ac.ui.cs.advprog.papikosbe.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.UUID;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionFactoryTest {

    private TopUpTransactionCreator topUpCreator;
    private PaymentTransactionCreator paymentCreator;
    private TransactionFactory transactionFactory;

    private UUID userId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        topUpCreator = mock(TopUpTransactionCreator.class);
        paymentCreator = mock(PaymentTransactionCreator.class);
        transactionFactory = new TransactionFactory(topUpCreator, paymentCreator);

        userId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
    }

    @Test
    void testCreateTopUpTransactionSuccess() throws Exception {
        TopUp topUp = new TopUp();
        when(topUpCreator.create(userId, BigDecimal.TEN, null)).thenReturn(topUp);

        Transaction result = transactionFactory.createTransaction(TransactionType.TOP_UP, userId, BigDecimal.TEN, null);

        assertEquals(topUp, result);
        verify(topUpCreator).create(userId, BigDecimal.TEN, null);
        verifyNoInteractions(paymentCreator);
    }

    @Test
    void testCreatePaymentTransactionSuccess() throws Exception {
        Payment payment = new Payment();
        when(paymentCreator.create(userId, BigDecimal.valueOf(50), ownerId)).thenReturn(payment);

        Transaction result = transactionFactory.createTransaction(TransactionType.PAYMENT, userId, BigDecimal.valueOf(50), ownerId);

        assertEquals(payment, result);
        verify(paymentCreator).create(userId, BigDecimal.valueOf(50), ownerId);
        verifyNoInteractions(topUpCreator);
    }

    @Test
    void testCreateTransactionThrowsWhenUnknownType() {
        TransactionType unknownType = mock(TransactionType.class);
        assertThrows(IllegalArgumentException.class, () ->
                transactionFactory.createTransaction(unknownType, userId, BigDecimal.TEN, ownerId)
        );
    }
}

