package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    Transaction getTransactionById(UUID userId) throws Exception;
    List<Transaction> getUserTransactions(UUID userId) throws Exception;
    List<Transaction> getTransactionByDate(LocalDateTime date);
    // Payment
    Payment createPayment(UUID tenantId, UUID ownerId, BigDecimal amount) throws Exception;
    List<Payment> getPaymentsByTenant(UUID tenantId);
    List<Payment> getPaymentsByOwner(UUID ownerId);
    // Top-up
    TopUp createTopUp(UUID userId, BigDecimal amount) throws Exception;
    List<TopUp> getTopUpsByUser(UUID userId);
}
