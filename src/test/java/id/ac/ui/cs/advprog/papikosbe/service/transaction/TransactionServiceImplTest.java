package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.WalletStatus;
import id.ac.ui.cs.advprog.papikosbe.exception.InactiveWalletException;
import id.ac.ui.cs.advprog.papikosbe.factory.TransactionFactory;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.booking.PaymentBooking;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.observer.handler.EventHandlerContext;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.PaymentBookingRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.TransactionRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.WalletRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import id.ac.ui.cs.advprog.papikosbe.service.booking.BookingServiceImpl;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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

    @Mock
    private EventHandlerContext eventHandlerContext;

    @Spy
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
    void testCreatePayment_ThrowsIfTenantWalletNotFound() throws Exception {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("10000");

        doNothing().when(transactionService).validatePayment(tenantId, ownerId, amount);
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> {
            transactionService.createPayment(tenantId, ownerId, amount).join();
        });

        assertTrue(ex.getMessage().contains("Tenant wallet not found"));
    }

    @Test
    void testCreatePayment_ThrowsIfOwnerWalletNotFound() throws Exception {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("10000");

        doNothing().when(transactionService).validatePayment(tenantId, ownerId, amount);
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(new Wallet()));
        when(walletRepository.findByUserId(ownerId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> {
            transactionService.createPayment(tenantId, ownerId, amount).join();
        });

        assertTrue(ex.getMessage().contains("Owner wallet not found"));
    }


    @Test
    void testCreateTopUpFailsIfStatusNotCompleted() throws Exception {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("20000");

        User user = new Tenant();
        user.setId(userId);

        Wallet userWallet = new Wallet();
        userWallet.setUser(user);
        userWallet.setBalance(new BigDecimal("50000"));
        userWallet.setStatus(WalletStatus.ACTIVE);

        TopUp topUp = Mockito.spy(new TopUp());
        topUp.setUser(user);
        topUp.setAmount(amount);
        topUp.setStatus(TransactionStatus.PENDING);

        doNothing().when(transactionService).validateTopUp(userId, amount);

        when(transactionFactory.createTransaction(TransactionType.TOP_UP, userId, amount, null))
                .thenReturn(topUp);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(userWallet));

        doReturn(TransactionStatus.FAILED).when(topUp).process(userWallet, null);

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.createTopUp(userId, amount).join()
        );

        assertTrue(ex.getMessage().contains("Top up gagal"));
    }


    @Test
    void testCreatePaymentFailsIfStatusNotCompleted() throws Exception {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30000");

        User tenant = new Tenant(); tenant.setId(tenantId);
        User owner = new Owner(); owner.setId(ownerId);

        Wallet tenantWallet = new Wallet();
        tenantWallet.setUser(tenant);
        tenantWallet.setBalance(new BigDecimal("50000"));
        tenantWallet.setStatus(WalletStatus.ACTIVE);

        Wallet ownerWallet = new Wallet();
        ownerWallet.setUser(owner);
        ownerWallet.setBalance(new BigDecimal("100000"));
        ownerWallet.setStatus(WalletStatus.ACTIVE);

        Payment payment = Mockito.spy(new Payment());
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setAmount(amount);
        payment.setStatus(TransactionStatus.PENDING); // status tidak sukses

        // Mock validasi payment
        doNothing().when(transactionService).validatePayment(tenantId, ownerId, amount);

        when(transactionFactory.createTransaction(TransactionType.PAYMENT, tenantId, amount, ownerId))
                .thenReturn(payment);

        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(tenantWallet));
        when(walletRepository.findByUserId(ownerId)).thenReturn(Optional.of(ownerWallet));

        // Simulasikan proses gagal
        doReturn(TransactionStatus.FAILED).when(payment).process(tenantWallet, ownerWallet);

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.createPayment(tenantId, ownerId, amount).join()
        );

        assertTrue(ex.getMessage().contains("Pembayaran gagal"));
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
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(tenantWallet));
        when(transactionFactory.createTransaction(TransactionType.TOP_UP, tenantId, amount, null)).thenReturn(topUp);
        when(topUp.process(tenantWallet, null)).thenReturn(TransactionStatus.COMPLETED);
        when(transactionRepository.save(topUp)).thenReturn(topUp);
        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(walletService.getOrCreateWallet(tenant)).thenReturn(tenantWallet);

        // Call the asynchronous method
        CompletableFuture<TopUp> resultFuture = transactionService.createTopUp(tenantId, amount);

        // Wait for the result
        TopUp result = resultFuture.join();

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
    void testRefundPayment_ThrowsIfPaymentNotFound() {
        UUID paymentId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> {
            transactionService.refundPayment(paymentId, requesterId).join();
        });

        assertTrue(ex.getMessage().contains("Transaksi pembayaran tidak ditemukan"));
    }

    @Test
    void testRefundPayment_ThrowsIfPaymentBookingNotFound() throws Exception {
        UUID paymentId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setStatus(TransactionStatus.COMPLETED);
        Owner owner = new Owner();
        owner.setId(requesterId);
        payment.setOwner(owner);

        when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentBookingRepository.findByPaymentId(paymentId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> {
            transactionService.refundPayment(paymentId, requesterId).join();
        });

        assertTrue(ex.getMessage().contains("Booking terkait dengan pembayaran tidak ditemukan"));
    }

    @Test
    void testRefundPayment_ThrowsIfBookingNotFound() throws Exception {
        UUID paymentId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setStatus(TransactionStatus.COMPLETED);
        Owner owner = new Owner();
        owner.setId(requesterId);
        payment.setOwner(owner);

        PaymentBooking paymentBooking = new PaymentBooking();
        paymentBooking.setBookingId(bookingId);
        paymentBooking.setPaymentId(paymentId);

        when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentBookingRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentBooking));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> {
            transactionService.refundPayment(paymentId, requesterId).join();
        });

        assertTrue(ex.getMessage().contains("Booking tidak ditemukan"));
    }

    @Test
    void testRefundPayment_ThrowsIfTenantWalletNotFound() throws Exception {
        UUID paymentId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = requesterId;
        UUID bookingId = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setStatus(TransactionStatus.COMPLETED);
        User tenant = new Tenant();
        tenant.setId(tenantId);
        Owner owner = new Owner();
        owner.setId(ownerId);
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setAmount(new BigDecimal("10000"));

        PaymentBooking paymentBooking = new PaymentBooking();
        paymentBooking.setBookingId(bookingId);
        paymentBooking.setPaymentId(paymentId);

        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setStatus(BookingStatus.APPROVED);

        when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentBookingRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentBooking));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.empty()); // tenant wallet not found

        Exception ex = assertThrows(Exception.class, () -> {
            transactionService.refundPayment(paymentId, requesterId).join();
        });

        assertTrue(ex.getMessage().contains("Wallet tenant tidak ditemukan"));
    }

    @Test
    void testRefundPayment_ThrowsIfOwnerWalletNotFound() throws Exception {
        UUID paymentId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = requesterId;
        UUID bookingId = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setStatus(TransactionStatus.COMPLETED);
        User tenant = new Tenant();
        tenant.setId(tenantId);
        Owner owner = new Owner();
        owner.setId(ownerId);
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setAmount(new BigDecimal("10000"));

        PaymentBooking paymentBooking = new PaymentBooking();
        paymentBooking.setBookingId(bookingId);
        paymentBooking.setPaymentId(paymentId);

        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setStatus(BookingStatus.APPROVED);

        when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentBookingRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentBooking));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(new Wallet()));
        when(walletRepository.findByUserId(ownerId)).thenReturn(Optional.empty()); // owner wallet not found

        Exception ex = assertThrows(Exception.class, () -> {
            transactionService.refundPayment(paymentId, requesterId).join();
        });

        assertTrue(ex.getMessage().contains("Wallet owner tidak ditemukan"));
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
    void testValidatePaymentFailsIfAmountNull() {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.validatePayment(tenantId, ownerId, null)
        );

        assertTrue(ex.getMessage().contains("lebih dari 0"));
    }

    @Test
    void testValidatePaymentFailsIfAmountZero() {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.validatePayment(tenantId, ownerId, BigDecimal.ZERO)
        );

        assertTrue(ex.getMessage().contains("lebih dari 0"));
    }

    @Test
    void testValidatePaymentFailsIfTenantNotFound() {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("20000");

        when(userRepository.findById(tenantId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.validatePayment(tenantId, ownerId, amount)
        );

        assertTrue(ex.getMessage().contains("Tenant tidak ditemukan"));
    }

    @Test
    void testValidatePaymentFailsIfOwnerNotFound() {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("20000");

        User tenant = new Tenant();
        tenant.setId(tenantId);

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.validatePayment(tenantId, ownerId, amount)
        );

        assertTrue(ex.getMessage().contains("Owner tidak ditemukan"));
    }

    @Test
    void testValidatePaymentFailsIfTenantWalletInactive() {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("20000");

        User tenant = new Tenant(); tenant.setId(tenantId);
        User owner = new Owner(); owner.setId(ownerId);

        Wallet tenantWallet = new Wallet(); tenantWallet.setStatus(WalletStatus.CLOSED);
        Wallet ownerWallet = new Wallet(); ownerWallet.setStatus(WalletStatus.ACTIVE);

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(walletService.getOrCreateWallet(tenant)).thenReturn(tenantWallet);
        when(walletService.getOrCreateWallet(owner)).thenReturn(ownerWallet);

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.validatePayment(tenantId, ownerId, amount)
        );

        assertTrue(ex.getMessage().contains("Wallet tenant tidak aktif"));
    }

    @Test
    void testValidatePaymentFailsIfOwnerWalletInactive() {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("20000");

        User tenant = new Tenant(); tenant.setId(tenantId);
        User owner = new Owner(); owner.setId(ownerId);

        Wallet tenantWallet = new Wallet(); tenantWallet.setStatus(WalletStatus.ACTIVE);
        Wallet ownerWallet = new Wallet(); ownerWallet.setStatus(WalletStatus.CLOSED);

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(walletService.getOrCreateWallet(tenant)).thenReturn(tenantWallet);
        when(walletService.getOrCreateWallet(owner)).thenReturn(ownerWallet);

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.validatePayment(tenantId, ownerId, amount)
        );

        assertTrue(ex.getMessage().contains("Wallet owner tidak aktif"));
    }

    @Test
    void testValidatePaymentFailsIfTenantBalanceInsufficient() {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50000");

        User tenant = new Tenant(); tenant.setId(tenantId);
        User owner = new Owner(); owner.setId(ownerId);

        Wallet tenantWallet = new Wallet(); tenantWallet.setStatus(WalletStatus.ACTIVE); tenantWallet.setBalance(new BigDecimal("10000"));
        Wallet ownerWallet = new Wallet(); ownerWallet.setStatus(WalletStatus.ACTIVE);

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(walletService.getOrCreateWallet(tenant)).thenReturn(tenantWallet);
        when(walletService.getOrCreateWallet(owner)).thenReturn(ownerWallet);

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.validatePayment(tenantId, ownerId, amount)
        );

        assertTrue(ex.getMessage().contains("Saldo tenant tidak mencukupi"));
    }

    @Test
    void testValidatePaymentSuccess() {
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30000");

        User tenant = new Tenant(); tenant.setId(tenantId);
        User owner = new Owner(); owner.setId(ownerId);

        Wallet tenantWallet = new Wallet(); tenantWallet.setStatus(WalletStatus.ACTIVE); tenantWallet.setBalance(new BigDecimal("50000"));
        Wallet ownerWallet = new Wallet(); ownerWallet.setStatus(WalletStatus.ACTIVE);

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(walletService.getOrCreateWallet(tenant)).thenReturn(tenantWallet);
        when(walletService.getOrCreateWallet(owner)).thenReturn(ownerWallet);

        assertDoesNotThrow(() -> transactionService.validatePayment(tenantId, ownerId, amount));
    }

    @Test
    void testCreateTopUp_ThrowsIfUserWalletNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("20000");

        doNothing().when(transactionService).validateTopUp(userId, amount);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> {
            transactionService.createTopUp(userId, amount).join();
        });

        assertTrue(ex.getMessage().contains("Wallet for user"));
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
    void testValidateTopUpFailsIfAmountLessThanOrEqualToZero() {
        UUID userId = UUID.randomUUID();

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.validateTopUp(userId, BigDecimal.ZERO)
        );

        assertTrue(ex.getMessage().contains("lebih dari 0"));
    }

    @Test
    void testValidateTopUpFailsIfAmountBelowMinimum() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("5000");

        Exception ex = assertThrows(Exception.class, () ->
                transactionService.validateTopUp(userId, amount)
        );

        assertTrue(ex.getMessage().contains("Minimum top up"));
    }


    @Test
    void testValidateTopUpFailsIfWalletInactive() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("15000");

        User user = Tenant.builder().email("test@example.com").password("pass").build();
        user.setId(userId);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setStatus(WalletStatus.CLOSED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletService.getOrCreateWallet(user)).thenReturn(wallet);

        InactiveWalletException ex = assertThrows(InactiveWalletException.class, () ->
                transactionService.validateTopUp(userId, amount)
        );

        assertTrue(ex.getMessage().contains("tidak aktif"));
    }

    @Test
    void testValidateTopUpSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("20000");

        User user = Tenant.builder().email("test@example.com").password("pass").build();
        user.setId(userId);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setStatus(WalletStatus.ACTIVE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletService.getOrCreateWallet(user)).thenReturn(wallet);

        assertDoesNotThrow(() -> transactionService.validateTopUp(userId, amount));
    }

    @Test
    void testRefundPayment_Success() throws Exception {
        UUID paymentId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30000");

        Tenant tenant = Tenant.builder().email("tenant@example.com").password("pass").build();
        tenant.setId(tenantId);

        Owner owner = Owner.builder().email("owner@example.com").password("pass").build();
        owner.setId(ownerId);

        Wallet tenantWallet = new Wallet();
        tenantWallet.setUser(tenant);
        tenantWallet.setBalance(new BigDecimal("10000"));
        tenantWallet.setStatus(WalletStatus.ACTIVE);

        Wallet ownerWallet = new Wallet();
        ownerWallet.setUser(owner);
        ownerWallet.setBalance(new BigDecimal("100000"));
        ownerWallet.setStatus(WalletStatus.ACTIVE);

        Payment originalPayment = Mockito.spy(new Payment());
        originalPayment.setId(paymentId);
        originalPayment.setUser(tenant);
        originalPayment.setOwner(owner);
        originalPayment.setAmount(amount);
        originalPayment.setStatus(TransactionStatus.COMPLETED); // <--- penting

        PaymentBooking paymentBooking = new PaymentBooking();
        paymentBooking.setPaymentId(paymentId);
        paymentBooking.setBookingId(bookingId);

        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setStatus(BookingStatus.APPROVED);

        when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(originalPayment));
        when(paymentBookingRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentBooking));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(tenantWallet));
        when(walletRepository.findByUserId(ownerId)).thenReturn(Optional.of(ownerWallet));

        when(transactionRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        CompletableFuture<Payment> resultFuture = transactionService.refundPayment(paymentId, ownerId);
        Payment refund = resultFuture.join();

        // ASSERT
        assertNotNull(refund);
        assertEquals(TransactionStatus.COMPLETED, refund.getStatus());
        verify(transactionRepository).save(originalPayment);
        verify(walletRepository).save(ownerWallet);
        verify(walletRepository).save(tenantWallet);
    }

    @Test
    void testRefundPayment_ThrowsIfRequesterNotOwner() {
        UUID paymentId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID(); // Bukan owner

        Owner owner = Owner.builder().email("owner@example.com").password("pass").build();
        owner.setId(UUID.randomUUID());

        Payment payment = new Payment();
        payment.setOwner(owner);
        payment.setStatus(TransactionStatus.COMPLETED);

        when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(payment));

        Exception ex = assertThrows(Exception.class, () -> {
            transactionService.refundPayment(paymentId, requesterId).join();
        });

        assertTrue(ex.getMessage().contains("Hanya pemilik kos"));
    }

    @Test
    void testRefundPayment_ThrowsIfRefundStatusNotCompleted() {
        UUID paymentId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30000");

        Tenant tenant = Tenant.builder().email("tenant@example.com").password("pass").build();
        tenant.setId(tenantId);

        Owner owner = Owner.builder().email("owner@example.com").password("pass").build();
        owner.setId(ownerId);

        Wallet tenantWallet = new Wallet();
        tenantWallet.setUser(tenant);
        tenantWallet.setBalance(new BigDecimal("10000"));

        Wallet ownerWallet = new Wallet();
        ownerWallet.setUser(owner);
        ownerWallet.setBalance(new BigDecimal("100000"));

        Payment originalPayment = new Payment();
        originalPayment.setId(paymentId);
        originalPayment.setUser(tenant);
        originalPayment.setOwner(owner);
        originalPayment.setAmount(amount);
        originalPayment.setStatus(TransactionStatus.COMPLETED);

        PaymentBooking paymentBooking = new PaymentBooking();
        paymentBooking.setPaymentId(paymentId);
        paymentBooking.setBookingId(bookingId);

        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setStatus(BookingStatus.APPROVED);

        Payment failedRefund = new Payment();
        failedRefund.setStatus(TransactionStatus.FAILED);

        // Mocking
        when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(originalPayment));
        when(paymentBookingRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentBooking));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(tenantWallet));
        when(walletRepository.findByUserId(ownerId)).thenReturn(Optional.of(ownerWallet));

        // Test
        Exception ex = assertThrows(Exception.class, () -> {
            transactionService.refundPayment(paymentId, ownerId).join();
        });

        assertTrue(ex.getMessage().contains("Refund gagal: FAILED"));
    }


    @Test
    void testRefundPayment_ThrowsIfBookingCancelled() {
        UUID paymentId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();

        Owner owner = Owner.builder().email("owner@example.com").password("pass").build();
        owner.setId(ownerId);

        Payment payment = new Payment();
        payment.setStatus(TransactionStatus.COMPLETED);
        payment.setOwner(owner);

        PaymentBooking paymentBooking = new PaymentBooking();
        paymentBooking.setBookingId(bookingId);
        paymentBooking.setPaymentId(paymentId);

        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setStatus(BookingStatus.CANCELLED); // <--- ini penting

        when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentBookingRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentBooking));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Exception ex = assertThrows(Exception.class, () -> {
            transactionService.refundPayment(paymentId, ownerId).join();
        });

        assertTrue(ex.getMessage().contains("Booking sudah dibatalkan"));
    }

    @Test
    void testRefundPayment_ThrowsIfOwnerBalanceInsufficient() {
        UUID paymentId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100000");

        Tenant tenant = Tenant.builder().email("tenant@example.com").password("pass").build();
        tenant.setId(tenantId);

        Owner owner = Owner.builder().email("owner@example.com").password("pass").build();
        owner.setId(ownerId);

        Wallet tenantWallet = new Wallet();
        tenantWallet.setUser(tenant);
        tenantWallet.setBalance(new BigDecimal("0"));

        Wallet ownerWallet = new Wallet();
        ownerWallet.setUser(owner);
        ownerWallet.setBalance(new BigDecimal("50000")); // <--- kurang dari amount
        ownerWallet.setStatus(WalletStatus.ACTIVE);

        Payment payment = new Payment();
        payment.setStatus(TransactionStatus.COMPLETED);
        payment.setAmount(amount);
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setId(paymentId);

        PaymentBooking paymentBooking = new PaymentBooking();
        paymentBooking.setBookingId(bookingId);
        paymentBooking.setPaymentId(paymentId);

        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setStatus(BookingStatus.APPROVED);

        when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentBookingRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentBooking));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(walletRepository.findByUserId(tenantId)).thenReturn(Optional.of(tenantWallet));
        when(walletRepository.findByUserId(ownerId)).thenReturn(Optional.of(ownerWallet));

        Exception ex = assertThrows(Exception.class, () -> {
            transactionService.refundPayment(paymentId, ownerId).join();
        });

        assertTrue(ex.getMessage().contains("Saldo owner tidak mencukupi"));
    }


    @Test
    void processBookingPayment_successfulFlow() throws Exception {
        UUID bookingId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        transactionService.processBookingPayment(bookingId, paymentId);

        verify(paymentBookingRepository).save(argThat(payment ->
                payment.getBookingId().equals(bookingId) &&
                        payment.getPaymentId().equals(paymentId)
        ));
        assertEquals(BookingStatus.PAID, booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void processBookingPayment_bookingNotFound_throwsException() {
        UUID bookingId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.processBookingPayment(bookingId, paymentId)
        );

        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void processBookingPayment_invalidStatus_throwsException() {
        UUID bookingId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setStatus(BookingStatus.CANCELLED); // any status other than PENDING_PAYMENT

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                transactionService.processBookingPayment(bookingId, paymentId)
        );

        assertEquals("Booking is not in PENDING_PAYMENT status", exception.getMessage());
    }

    @Test
    void getPaymentsByTenant_shouldReturnPayments() throws Exception {
        UUID tenantId = UUID.randomUUID();
        List<Payment> mockPayments = List.of(new Payment(), new Payment());

        when(transactionRepository.findPaymentsByTenant(tenantId)).thenReturn(mockPayments);

        List<Payment> result = transactionService.getPaymentsByTenant(tenantId);

        assertEquals(mockPayments, result);
        verify(transactionRepository).findPaymentsByTenant(tenantId);
    }

    @Test
    void getPaymentsByOwner_shouldReturnPayments() throws Exception {
        UUID ownerId = UUID.randomUUID();
        List<Payment> mockPayments = List.of(new Payment());

        when(transactionRepository.findPaymentsByOwner(ownerId)).thenReturn(mockPayments);

        List<Payment> result = transactionService.getPaymentsByOwner(ownerId);

        assertEquals(mockPayments, result);
        verify(transactionRepository).findPaymentsByOwner(ownerId);
    }

    @Test
    void getTopUpsByUser_shouldReturnTopUps() throws Exception {
        UUID userId = UUID.randomUUID();
        List<TopUp> mockTopUps = List.of(new TopUp(), new TopUp(), new TopUp());

        when(transactionRepository.findTopUpsByUser(userId)).thenReturn(mockTopUps);

        List<TopUp> result = transactionService.getTopUpsByUser(userId);

        assertEquals(mockTopUps, result);
        verify(transactionRepository).findTopUpsByUser(userId);
    }

}