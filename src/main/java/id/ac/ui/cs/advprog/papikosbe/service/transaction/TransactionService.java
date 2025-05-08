package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    Transaction create(Transaction transaction);
    List<Transaction> findAll();
    Transaction findById(UUID id);
    List<Transaction> findAllByUserId(UUID userId);
    List<Transaction> findByType(TransactionType type);
    List<Transaction> findByDate(LocalDateTime date);
}
