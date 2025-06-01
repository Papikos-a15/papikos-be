package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TransactionService {
    Transaction getTransactionById(UUID userId) throws Exception;
    List<Transaction> getUserTransactions(UUID userId) throws Exception;

    CompletableFuture<Payment> createPayment(UUID tenantId, UUID ownerId, BigDecimal amount) throws Exception;
    List<Payment> getPaymentsByTenant(UUID tenantId);
    List<Payment> getPaymentsByOwner(UUID ownerId);

    CompletableFuture<TopUp> createTopUp(UUID userId, BigDecimal amount) throws Exception;
    List<TopUp> getTopUpsByUser(UUID userId);

    CompletableFuture<Payment> refundPayment(UUID paymentId, UUID requesterId) throws Exception;

    void processBookingPayment(UUID bookingId, UUID paymentId) throws Exception;
}

