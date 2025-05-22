package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.WalletStatus;
import id.ac.ui.cs.advprog.papikosbe.factory.TransactionFactory;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.TransactionRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.WalletRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionFactory transactionFactory;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void testCreatePayment_Success() throws Exception {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30000");

        Tenant tenant = Tenant.builder().email("tenant@example.com").password("tenantpass").build();
        tenant.setId(tenantId);

        Owner owner = Owner.builder().email("owner@example.com").password("ownerpass").build();
        owner.setId(ownerId);

        Wallet tenantWallet = new Wallet();
        tenantWallet.setUser(tenant);
        tenantWallet.setStatus(WalletStatus.ACTIVE);
        tenantWallet.setBalance(new BigDecimal("100000"));

        Wallet ownerWallet = new Wallet();
        ownerWallet.setUser(owner);
        ownerWallet.setStatus(WalletStatus.ACTIVE);
        ownerWallet.setBalance(new BigDecimal("50000"));

        Payment payment = new Payment();
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setAmount(amount);

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(tenantWallet));
        when(walletRepository.findByUserId(ownerId)).thenReturn(Optional.of(ownerWallet));
        when(transactionFactory.createTransaction(TransactionType.PAYMENT, tenantId, amount, ownerId))
                .thenReturn(payment);

        Payment result = transactionService.createPayment(tenantId, ownerId, amount);

        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        verify(walletRepository).save(tenantWallet);
        verify(walletRepository).save(ownerWallet);
        verify(transactionRepository).save(payment);
    }

    @Test
    void testCreatePayment_Failure_InsufficientBalance() throws Exception {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("150000");

        Tenant tenant = Tenant.builder().email("tenant@example.com").password("tenantpass").build();
        tenant.setId(tenantId);

        Owner owner = Owner.builder().email("owner@example.com").password("ownerpass").build();
        owner.setId(ownerId);

        Wallet tenantWallet = new Wallet();
        tenantWallet.setUser(tenant);
        tenantWallet.setStatus(WalletStatus.ACTIVE);
        tenantWallet.setBalance(new BigDecimal("50000"));

        Wallet ownerWallet = new Wallet();
        ownerWallet.setUser(owner);
        ownerWallet.setStatus(WalletStatus.ACTIVE);
        ownerWallet.setBalance(new BigDecimal("100000"));

        Payment payment = new Payment();
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setAmount(amount);

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(tenantWallet));
        when(walletRepository.findByUserId(ownerId)).thenReturn(Optional.of(ownerWallet));

        assertThrows(Exception.class, () -> transactionService.createPayment(tenantId, ownerId, amount));
    }

    @Test
    void testCreateTopUp_Success() throws Exception {
        UUID tenantId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30000");

        Tenant tenant = Tenant.builder().email("tenant@example.com").password("tenantpass").build();
        tenant.setId(tenantId);

        Wallet tenantWallet = new Wallet();
        tenantWallet.setUser(tenant);
        tenantWallet.setStatus(WalletStatus.ACTIVE);
        tenantWallet.setBalance(new BigDecimal("50000"));

        TopUp topUp = new TopUp();
        topUp.setUser(tenant);
        topUp.setAmount(amount);
        topUp.setStatus(TransactionStatus.COMPLETED);

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(tenantWallet));
        when(transactionFactory.createTransaction(TransactionType.TOP_UP, tenantId, amount, null)).thenReturn(topUp);

        TopUp result = transactionService.createTopUp(tenantId, amount);

        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        verify(walletRepository).save(tenantWallet);
        verify(transactionRepository).save(topUp);
    }

    @Test
    void testCreateTopUp_Failure_InvalidAmount() throws Exception {
        UUID tenantId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("5000"); // assume this is below minimum allowed

        Tenant tenant = Tenant.builder().email("tenant@example.com").password("tenantpass").build();
        tenant.setId(tenantId);

        Wallet tenantWallet = new Wallet();
        tenantWallet.setUser(tenant);
        tenantWallet.setStatus(WalletStatus.ACTIVE);
        tenantWallet.setBalance(new BigDecimal("50000"));

        TopUp topUp = new TopUp();
        topUp.setUser(tenant);
        topUp.setAmount(amount);

        assertThrows(Exception.class, () -> transactionService.createTopUp(tenantId, amount));
    }

    @Test
    void testGetTransactionById() throws Exception {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Payment payment = new Payment();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(payment));

        // Act
        Transaction result = transactionService.getTransactionById(transactionId);

        // Assert
        assertNotNull(result);
        assertEquals(payment, result);
    }

    @Test
    void testGetTransactionById_TransactionNotFound() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> transactionService.getTransactionById(transactionId));
    }

    @Test
    void testGetUserTransactions() throws Exception {
        // Arrange
        List<Payment> payments = Collections.singletonList(new Payment());
        List<TopUp> topUps = Collections.singletonList(new TopUp());
        UUID userId = UUID.randomUUID();

        when(transactionRepository.findPaymentsByUser(userId)).thenReturn(payments);
        when(transactionRepository.findTopUpsByUser(userId)).thenReturn(topUps);

        // Act
        List<Transaction> result = transactionService.getUserTransactions(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());  // One payment and one top-up
    }

    @Test
    void testGetTransactionByDate() {
        // Arrange
        LocalDateTime date = LocalDateTime.now();
        List<Transaction> transactions = Collections.singletonList(new Payment());
        when(transactionRepository.findByDate(LocalDate.from(date))).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getTransactionByDate(date);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}