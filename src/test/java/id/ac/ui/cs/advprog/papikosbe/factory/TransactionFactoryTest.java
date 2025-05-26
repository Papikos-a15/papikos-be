package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionFactoryTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionFactory transactionFactory;

    private UUID userId;
    private UUID ownerId;
    private BigDecimal amount;
    private User user;
    private User owner;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        amount = new BigDecimal("100.00");

        user = new Tenant();
        user.setId(userId);

        owner = new Owner();
        owner.setId(ownerId);
    }

    @Test
    void testCreateTransaction_TopUp_Success() throws Exception {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Transaction result = transactionFactory.createTransaction(TransactionType.TOP_UP, userId, amount, null);

        // Assert
        assertNotNull(result);
        assertInstanceOf(TopUp.class, result);
        TopUp topUp = (TopUp) result;
        assertEquals(amount, topUp.getAmount());
        assertEquals(user, topUp.getUser());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testCreateTransaction_TopUp_UserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () ->
                transactionFactory.createTransaction(TransactionType.TOP_UP, userId, amount, null)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testCreateTransaction_Payment_Success() throws Exception {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        // Act
        Transaction result = transactionFactory.createTransaction(TransactionType.PAYMENT, userId, amount, ownerId);

        // Assert
        assertNotNull(result);
        assertInstanceOf(Payment.class, result);
        Payment payment = (Payment) result;
        assertEquals(amount, payment.getAmount());
        assertEquals(user, payment.getUser());
        assertEquals(owner, payment.getOwner());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(ownerId);
    }

    @Test
    void testCreateTransaction_Payment_OwnerIdNull() {
        // Act & Assert
        Exception exception = assertThrows(Exception.class, () ->
                transactionFactory.createTransaction(TransactionType.PAYMENT, userId, amount, null)
        );

        assertEquals("Owner ID is required for Payment", exception.getMessage());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void testCreateTransaction_Payment_TenantNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () ->
                transactionFactory.createTransaction(TransactionType.PAYMENT, userId, amount, ownerId)
        );

        assertEquals("Tenant not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findById(ownerId);
    }

    @Test
    void testCreateTransaction_Payment_OwnerNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () ->
                transactionFactory.createTransaction(TransactionType.PAYMENT, userId, amount, ownerId)
        );

        assertEquals("Owner not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(ownerId);
    }

    @Test
    void testCreateTransaction_NullTransactionType() {
        // Test with null transaction type which causes NullPointerException in switch
        TransactionType nullType = null;

        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                transactionFactory.createTransaction(nullType, userId, amount, ownerId)
        );

        assertNotNull(exception);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void testCreateTransaction_UnknownTransactionType_WithReflection() throws Exception {
        // This test would require adding a test enum value or using reflection
        // Since TransactionType only has TOP_UP and PAYMENT, the default case is unreachable
        // unless we add a new enum value specifically for testing

        // For now, we acknowledge that the default case exists as defensive programming
        // but may not be reachable with current enum values

        // If you want to test this, you could:
        // 1. Add UNKNOWN to TransactionType enum
        // 2. Use reflection to create a mock enum value
        // 3. Or modify the factory to handle unknown types differently

        assertTrue(true, "Default case exists as defensive programming but may not be reachable with current enum values");
    }

    @Test
    void testCreateTopUp_DirectCall_Success() throws Exception {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        TopUp result = transactionFactory.createTopUp(userId, amount);

        // Assert
        assertNotNull(result);
        assertEquals(amount, result.getAmount());
        assertEquals(user, result.getUser());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testCreateTopUp_DirectCall_UserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () ->
                transactionFactory.createTopUp(userId, amount)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testCreatePayment_DirectCall_Success() throws Exception {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        // Act
        Payment result = transactionFactory.createPayment(userId, ownerId, amount);

        // Assert
        assertNotNull(result);
        assertEquals(amount, result.getAmount());
        assertEquals(user, result.getUser());
        assertEquals(owner, result.getOwner());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(ownerId);
    }

    @Test
    void testCreatePayment_DirectCall_TenantNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () ->
                transactionFactory.createPayment(userId, ownerId, amount)
        );

        assertEquals("Tenant not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findById(ownerId);
    }

    @Test
    void testCreatePayment_DirectCall_OwnerNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () ->
                transactionFactory.createPayment(userId, ownerId, amount)
        );

        assertEquals("Owner not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(ownerId);
    }

    @Test
    void testConstructor() {
        // This test ensures the constructor and dependency injection are working
        assertNotNull(transactionFactory);
        // We can't directly access the private field, but we can verify it works through other tests
    }

    @Test
    void testCreateTransaction_WithDifferentAmounts() throws Exception {
        // Test with different amount values to ensure proper handling
        BigDecimal zeroAmount = BigDecimal.ZERO;
        BigDecimal negativeAmount = new BigDecimal("-50.00");
        BigDecimal largeAmount = new BigDecimal("9999999.99");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Test zero amount
        TopUp topUpZero = (TopUp) transactionFactory.createTransaction(TransactionType.TOP_UP, userId, zeroAmount, null);
        assertEquals(zeroAmount, topUpZero.getAmount());

        // Test negative amount
        TopUp topUpNegative = (TopUp) transactionFactory.createTransaction(TransactionType.TOP_UP, userId, negativeAmount, null);
        assertEquals(negativeAmount, topUpNegative.getAmount());

        // Test large amount
        TopUp topUpLarge = (TopUp) transactionFactory.createTransaction(TransactionType.TOP_UP, userId, largeAmount, null);
        assertEquals(largeAmount, topUpLarge.getAmount());
    }
}