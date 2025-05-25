package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.enums.WalletStatus;
import id.ac.ui.cs.advprog.papikosbe.factory.TransactionFactory;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.booking.PaymentBooking;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.PaymentBookingRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.TransactionRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.WalletRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PaymentBookingRepository paymentBookingRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionFactory transactionFactory;

    @Autowired
    private WalletService walletService;

    @Override
    public Transaction getTransactionById(UUID userId) {
        // Synchronous method for fetching transaction by ID
        return transactionRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
    public List<Transaction> getUserTransactions(UUID userId) {
        // Synchronous method for fetching user transactions (both payments and top-ups)
        List<Payment> payments = transactionRepository.findPaymentsByUser(userId);
        List<TopUp> topUps = transactionRepository.findTopUpsByUser(userId);

        List<Transaction> all = Stream.concat(payments.stream(), topUps.stream())
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .collect(Collectors.toList());

        return all;
    }

    @Override
    public List<Transaction> getTransactionByDate(LocalDateTime date) {
        // Synchronous method to fetch transactions by date
        return transactionRepository.findByDate(LocalDate.from(date));
    }

    /*** Payment Methods ***/
    @Override
    public CompletableFuture<Payment> createPayment(UUID tenantId, UUID ownerId, BigDecimal amount) throws Exception {
        validatePayment(tenantId, ownerId, amount);

        Payment payment = (Payment) transactionFactory.createTransaction(
                TransactionType.PAYMENT, tenantId, amount, ownerId
        );

        Wallet tenantWallet = walletRepository.findByUserId(tenantId).get();
        Wallet ownerWallet = walletRepository.findByUserId(ownerId).get();

        TransactionStatus status = payment.process(tenantWallet, ownerWallet);

        if (status == TransactionStatus.COMPLETED) {
            walletRepository.save(tenantWallet);
            walletRepository.save(ownerWallet);
            Payment savedPayment = transactionRepository.save(payment);
            return CompletableFuture.completedFuture(savedPayment);
        } else {
            throw new Exception("Pembayaran gagal: " + status);
        }
    }

    /*** TopUp Methods ***/
    @Override
    public CompletableFuture<TopUp> createTopUp(UUID userId, BigDecimal amount) throws Exception {
        validateTopUp(userId, amount);

        TopUp topUp = (TopUp) transactionFactory.createTransaction(
                TransactionType.TOP_UP, userId, amount, null
        );

        Wallet userWallet = walletRepository.findByUserId(userId).get();

        TransactionStatus status = topUp.process(userWallet, null);

        if (status == TransactionStatus.COMPLETED) {
            walletRepository.save(userWallet);
            TopUp savedTopUp = transactionRepository.save(topUp);
            return CompletableFuture.completedFuture(savedTopUp);
        } else {
            throw new Exception("Top up gagal: " + status);
        }
    }

    /*** Validation Methods ***/
    public void validatePayment(UUID tenantId, UUID ownerId, BigDecimal amount) throws Exception {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Jumlah pembayaran harus lebih dari 0");
        }

        if (tenantId.equals(ownerId)) {
            throw new Exception("Tenant dan owner tidak boleh sama");
        }

        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new Exception("Tenant tidak ditemukan"));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new Exception("Owner tidak ditemukan"));

        Wallet tenantWallet = walletService.getOrCreateWallet(tenant);
        Wallet ownerWallet = walletService.getOrCreateWallet(owner);

        if (tenantWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new Exception("Wallet tenant tidak aktif");
        }

        if (ownerWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new Exception("Wallet owner tidak aktif");
        }

        if (tenantWallet.getBalance().compareTo(amount) < 0) {
            throw new Exception("Saldo tenant tidak mencukupi");
        }
    }

    public void validateTopUp(UUID userId, BigDecimal amount) throws Exception {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Jumlah top up harus lebih dari 0");
        }

        if (amount.compareTo(new BigDecimal("10000")) < 0) {
            throw new Exception("Minimum top up adalah Rp 10.000");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User tidak ditemukan"));

        Wallet userWallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new Exception("Wallet user tidak ditemukan"));

        if (userWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new Exception("Wallet user tidak aktif");
        }
    }

    @Override
    public CompletableFuture<List<Payment>> getPaymentsByTenant(UUID tenantId) {
        return CompletableFuture.supplyAsync(() -> transactionRepository.findPaymentsByTenant(tenantId));
    }

    @Override
    public CompletableFuture<List<Payment>> getPaymentsByOwner(UUID ownerId) {
        return CompletableFuture.supplyAsync(() -> transactionRepository.findPaymentsByOwner(ownerId));
    }

    @Override
    public CompletableFuture<List<TopUp>> getTopUpsByUser(UUID userId) {
        return CompletableFuture.supplyAsync(() -> transactionRepository.findTopUpsByUser(userId));
    }

    @Override
    public CompletableFuture<Payment> refundPayment(UUID paymentId, UUID requesterId) throws Exception {
        // Find the original payment
        Payment originalPayment = transactionRepository.findPaymentById(paymentId)
                .orElseThrow(() -> new Exception("Transaksi pembayaran tidak ditemukan"));

        if (originalPayment.getStatus() != TransactionStatus.COMPLETED) {
            throw new Exception("Transaksi belum selesai, tidak dapat direfund");
        }

        if (!originalPayment.getOwner().getId().equals(requesterId)) {
            throw new Exception("Hanya pemilik kos (owner) yang dapat melakukan refund");
        }

        // Find the associated booking through payment_booking table
        PaymentBooking paymentBooking = paymentBookingRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new Exception("Booking terkait dengan pembayaran tidak ditemukan"));

        // Get the booking and update its status
        Booking booking = bookingRepository.findById(paymentBooking.getBookingId())
                .orElseThrow(() -> new Exception("Booking tidak ditemukan"));

        // Check if booking can be refunded (e.g., not already cancelled or completed)
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new Exception("Booking sudah dibatalkan atau sudah direfund");
        }

        // Proceed with wallet operations
        UUID tenantId = originalPayment.getUser().getId(); // Tenant = user yg membayar
        UUID ownerId = originalPayment.getOwner().getId();
        BigDecimal amount = originalPayment.getAmount();

        Wallet tenantWallet = walletRepository.findByUserId(tenantId)
                .orElseThrow(() -> new Exception("Wallet tenant tidak ditemukan"));

        Wallet ownerWallet = walletRepository.findByUserId(ownerId)
                .orElseThrow(() -> new Exception("Wallet owner tidak ditemukan"));

        if (ownerWallet.getBalance().compareTo(amount) < 0) {
            throw new Exception("Saldo owner tidak mencukupi untuk refund");
        }

        // Create refund payment
        Payment refundPayment = new Payment();
        refundPayment.setUser(originalPayment.getOwner()); // Owner sends refund
        refundPayment.setOwner(originalPayment.getUser()); // Tenant receives refund
        refundPayment.setAmount(amount);
        refundPayment.setType(TransactionType.PAYMENT); // Use REFUND type if available, or PAYMENT
        refundPayment.setStatus(TransactionStatus.PENDING);
        refundPayment.setCreatedAt(LocalDateTime.now());
        refundPayment.setPaidDate(LocalDateTime.now());

        // Process the refund transaction
        TransactionStatus status = refundPayment.process(ownerWallet, tenantWallet);

        if (status == TransactionStatus.COMPLETED) {
            originalPayment.setStatus(TransactionStatus.REFUNDED);
            transactionRepository.save(originalPayment);

            // Save wallet changes
            walletRepository.save(ownerWallet);
            walletRepository.save(tenantWallet);

            // Save refund payment
            Payment savedRefund = transactionRepository.save(refundPayment);

            // Optionally: Create a new payment_booking entry for the refund transaction
            // or add a field to track refund relationships

            return CompletableFuture.completedFuture(savedRefund);
        } else {
            throw new Exception("Refund gagal: " + status);
        }
    }
    
    @Override
    public void processBookingPayment(UUID bookingId, UUID paymentId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Booking is not in PENDING_PAYMENT status");
        }

        PaymentBooking paymentBooking = PaymentBooking.builder()
                .bookingId(bookingId)
                .paymentId(paymentId)
                .build();

        paymentBookingRepository.save(paymentBooking);

        booking.setStatus(BookingStatus.PAID);
        bookingRepository.save(booking);
    }

}