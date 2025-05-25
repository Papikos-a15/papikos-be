package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.WalletStatus;
import id.ac.ui.cs.advprog.papikosbe.factory.TransactionFactory;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.booking.PaymentBooking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.PaymentBookingRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.TransactionRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.WalletRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import id.ac.ui.cs.advprog.papikosbe.service.booking.BookingServiceImpl;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentBookingRepository paymentBookingRepository;

    @Mock
    private KosServiceImpl kosService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletService walletService;

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

        Payment payment = Mockito.spy(new Payment());
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setAmount(amount);

        // SETUP MOCKS
        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(tenantWallet));
        when(walletRepository.findByUserId(ownerId)).thenReturn(Optional.of(ownerWallet));
        when(walletService.getOrCreateWallet(tenant)).thenReturn(tenantWallet);
        when(walletService.getOrCreateWallet(owner)).thenReturn(ownerWallet);
        when(transactionFactory.createTransaction(TransactionType.PAYMENT, tenantId, amount, ownerId))
                .thenReturn(payment);
        when(payment.process(tenantWallet, ownerWallet)).thenReturn(TransactionStatus.COMPLETED);
        when(transactionRepository.save(payment)).thenReturn(payment);

        // ACTION - Call the asynchronous method
        CompletableFuture<Payment> resultFuture = transactionService.createPayment(tenantId, ownerId, amount);

        // Wait for the result to complete
        Payment result = resultFuture.join();  // This will block until the future completes

        // ASSERTION
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        verify(walletRepository).save(tenantWallet);
        verify(walletRepository).save(ownerWallet);
        verify(transactionRepository).save(payment);
    }


    @Test
    void testCreatePayment_Failure_InsufficientBalance() {
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

        TopUp topUp = Mockito.spy(new TopUp());
        topUp.setUser(tenant);
        topUp.setAmount(amount);
        topUp.setStatus(TransactionStatus.COMPLETED);

        // Mocking the repositories and methods
        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(tenantWallet));
        when(transactionFactory.createTransaction(TransactionType.TOP_UP, tenantId, amount, null)).thenReturn(topUp);
        when(topUp.process(tenantWallet, null)).thenReturn(TransactionStatus.COMPLETED);
        when(transactionRepository.save(topUp)).thenReturn(topUp);

        // Call the asynchronous method
        CompletableFuture<TopUp> resultFuture = transactionService.createTopUp(tenantId, amount);

        // Wait for the result
        TopUp result = resultFuture.join(); // join() waits for the result

        // Assertions
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        verify(walletRepository).save(tenantWallet);
        verify(transactionRepository).save(topUp);
    }


    @Test
    void testCreateTopUp_Failure_InvalidAmount() {
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
    void testGetTransactionById() {
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
    void testGetUserTransactions() {
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

//    @Test
//    void testRefundPayment_Success() throws Exception {
//        UUID paymentId = UUID.randomUUID();
//        UUID tenantId = UUID.randomUUID();
//        UUID ownerId = UUID.randomUUID();
//        BigDecimal amount = new BigDecimal("20000");
//
//        // Create Tenant and Owner objects
//        Tenant tenant = Tenant.builder().email("tenant@example.com").password("tenantpass").build();
//        tenant.setId(tenantId);
//
//        Owner owner = Owner.builder().email("owner@example.com").password("ownerpass").build();
//        owner.setId(ownerId);
//
//        // Create a mock Payment object
//        Payment originalPayment = new Payment();
//        originalPayment.setId(paymentId);
//        originalPayment.setUser(tenant);
//        originalPayment.setOwner(owner);
//        originalPayment.setAmount(amount);
//        originalPayment.setStatus(TransactionStatus.COMPLETED);
//
//        // Mock the repository to return the originalPayment for findPaymentById(paymentId)
//        when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(originalPayment));
//
//        // Mock other dependencies
//        Wallet tenantWallet = new Wallet();
//        tenantWallet.setUser(tenant);
//        tenantWallet.setStatus(WalletStatus.ACTIVE);
//        tenantWallet.setBalance(new BigDecimal("10000"));
//
//        Wallet ownerWallet = new Wallet();
//        ownerWallet.setUser(owner);
//        ownerWallet.setStatus(WalletStatus.ACTIVE);
//        ownerWallet.setBalance(new BigDecimal("50000"));
//
//        // Mock the walletRepository behavior
//        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(tenantWallet));
//        when(walletRepository.findByUserId(ownerId)).thenReturn(Optional.of(ownerWallet));
//
//        // Mock the paymentBookingRepository behavior
//        PaymentBooking paymentBooking = new PaymentBooking();
//        paymentBooking.setBookingId(UUID.randomUUID());  // Assign a valid bookingId
//        paymentBooking.setPaymentId(paymentId);
//        when(paymentBookingRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentBooking));
//
//        // Mock the bookingRepository behavior
//        Booking mockBooking = new Booking();
//        mockBooking.setBookingId(paymentBooking.getBookingId());
//        mockBooking.setStatus(BookingStatus.PENDING_PAYMENT);  // Set a valid booking status
//        when(bookingRepository.findById(paymentBooking.getBookingId())).thenReturn(Optional.of(mockBooking));
//
//        // When refund is processed
//        CompletableFuture<Payment> resultFuture = transactionService.refundPayment(paymentId, ownerId);
//        Payment result = resultFuture.join();
//
//        // Assertions and verifications
//        assertNotNull(result);
//        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
//
//        // Verify repository interactions
//        verify(walletRepository).save(ownerWallet);
//        verify(walletRepository).save(tenantWallet);
//        verify(transactionRepository).save(any(Payment.class));  // Verify save was called
//        verify(paymentBookingRepository).findByPaymentId(paymentId);  // Verify the repository method was called
//    }

    @Test
    void testRefundPayment_ThrowsIfNotCompleted() {
        UUID paymentId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setStatus(TransactionStatus.PENDING);

        when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(payment));

        Exception ex = assertThrows(Exception.class, () -> {
            transactionService.refundPayment(paymentId, requesterId).join();
        });

        assertTrue(ex.getMessage().contains("Transaksi belum selesai"));
    }

    @Test
    void testValidatePaymentFailsIfTenantEqualsOwner() {
        UUID id = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("10000");

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.validatePayment(id, id, amount)
        );

        assertTrue(ex.getMessage().contains("tidak boleh sama"));
    }

    @Test
    void testValidateTopUpFailsIfAmountNull() {
        UUID userId = UUID.randomUUID();

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.validateTopUp(userId, null)
        );

        assertTrue(ex.getMessage().contains("lebih dari 0"));
    }

    @Test
    void testValidateTopUpFailsIfWalletNotFound() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50000");

        Tenant user = Tenant.builder().email("user@example.com").password("123").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.validateTopUp(userId, amount)
        );

        assertTrue(ex.getMessage().contains("Wallet user tidak ditemukan"));
    }

    @Test
    void testGetTransactionByDate() {
        // Arrange
        LocalDateTime now = LocalDateTime.of(2025, 5, 25, 10, 30); // bebas waktu
        LocalDate dateOnly = LocalDate.from(now); // yang dikirim ke repo
        Payment transaction1 = new Payment();
        TopUp transaction2 = new TopUp();
        List<Transaction> expected = List.of(transaction1, transaction2);

        when(transactionRepository.findByDate(dateOnly)).thenReturn(expected);

        // Act
        List<Transaction> result = transactionService.getTransactionByDate(now);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expected, result);
        verify(transactionRepository).findByDate(dateOnly);
    }
}