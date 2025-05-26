package id.ac.ui.cs.advprog.papikosbe.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PaymentTransactionCreatorTest {

    @Mock
    private UserRepository userRepository;

    private PaymentTransactionCreator paymentTransactionCreator;

    private UUID userId;
    private UUID ownerId;
    private User tenant;
    private User owner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentTransactionCreator = new PaymentTransactionCreator(userRepository);

        userId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        tenant = new Tenant();
        tenant.setId(userId);

        owner = new Owner();
        owner.setId(ownerId);
    }

    @Test
    void testCreatePaymentSuccess() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(tenant));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        BigDecimal amount = BigDecimal.valueOf(100);
        Payment result = (Payment) paymentTransactionCreator.create(userId, amount, ownerId);

        assertNotNull(result);
        assertEquals(tenant, result.getUser());
        assertEquals(owner, result.getOwner());
        assertEquals(amount, result.getAmount());

        verify(userRepository).findById(userId);
        verify(userRepository).findById(ownerId);
    }

    @Test
    void testCreatePaymentThrowsWhenOwnerIdIsNull() {
        Exception exception = assertThrows(Exception.class, () ->
                paymentTransactionCreator.create(userId, BigDecimal.TEN, null)
        );

        assertEquals("Owner ID is required for Payment", exception.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testCreatePaymentThrowsWhenTenantNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () ->
                paymentTransactionCreator.create(userId, BigDecimal.TEN, ownerId)
        );

        assertEquals("Tenant not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).findById(ownerId);
    }

    @Test
    void testCreatePaymentThrowsWhenOwnerNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(tenant));
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () ->
                paymentTransactionCreator.create(userId, BigDecimal.TEN, ownerId)
        );

        assertEquals("Owner not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository).findById(ownerId);
    }
}

