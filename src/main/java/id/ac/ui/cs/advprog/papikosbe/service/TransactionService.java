package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    Transaction create(Transaction transaction);
    List<Transaction> findAll();
    Transaction findById(UUID id);
    List<Transaction> findAllByUserId(UUID userId);
}
