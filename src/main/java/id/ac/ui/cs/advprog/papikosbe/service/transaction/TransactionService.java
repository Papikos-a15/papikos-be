package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.dto.PaymentRequest;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TransactionService {
    // Synchronous Methods
    Transaction getTransactionById(UUID userId) throws Exception;
    List<Transaction> getUserTransactions(UUID userId) throws Exception;
    List<Transaction> getTransactionByDate(LocalDateTime date);

    // Asynchronous Methods for Payment and TopUp
    CompletableFuture<Payment> createPayment(UUID tenantId, UUID ownerId, BigDecimal amount) throws Exception;
    CompletableFuture<List<Payment>> getPaymentsByTenant(UUID tenantId);
    CompletableFuture<List<Payment>> getPaymentsByOwner(UUID ownerId);

    CompletableFuture<TopUp> createTopUp(UUID userId, BigDecimal amount) throws Exception;
    CompletableFuture<List<TopUp>> getTopUpsByUser(UUID userId);

    CompletableFuture<Payment> refundPayment(UUID paymentId, UUID requesterId) throws Exception;

    CompletableFuture<Payment> processBookingPayment(UUID bookingId, PaymentRequest paymentRequest) throws Exception;
}

