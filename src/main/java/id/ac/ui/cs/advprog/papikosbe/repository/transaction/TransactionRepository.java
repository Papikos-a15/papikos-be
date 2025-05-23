package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("SELECT t FROM Transaction t WHERE CAST(t.createdAt AS date) = :date")
    List<Transaction> findByDate(@Param("date") LocalDate date);

    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId OR p.owner.id = :userId ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByUser(@Param("userId") UUID userId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.status = :status")
    long countByStatus(@Param("status") TransactionStatus status);

    // --- Payment Specific Queries ---
    @Query("SELECT p FROM Payment p WHERE p.user.id = :tenantId")
    List<Payment> findPaymentsByTenant(@Param("tenantId") UUID tenantId);

    @Query("SELECT p FROM Payment p WHERE p.owner.id = :ownerId")
    List<Payment> findPaymentsByOwner(@Param("ownerId") UUID ownerId);

    // --- TopUp Specific Queries ---
    @Query("SELECT t FROM TopUp t WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
    List<TopUp> findTopUpsByUser(@Param("userId") UUID userId);

}