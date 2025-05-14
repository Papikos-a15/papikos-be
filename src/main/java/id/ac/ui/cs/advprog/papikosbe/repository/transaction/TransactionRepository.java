package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TransactionRepository {
    private final List<Transaction> transactionData = new ArrayList<>();

    public Transaction save(Transaction transaction) {
        Optional<Transaction> existing = findById(transaction.getId());
        existing.ifPresent(transactionData::remove); // Remove jika ada
        transactionData.add(transaction);
        return transaction;
    }

    public Iterator<Transaction> findAll() {
        return transactionData.iterator();
    }

    public Optional<Transaction> findById(UUID transactionId) {
        for (Transaction transaction : transactionData) {
            if (transaction.getId().equals(transactionId)) {
                return Optional.of(transaction);
            }
        }
        return Optional.empty();
    }

    public List<Transaction> findAllByUserId(UUID userId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactionData) {
            if (transaction.getUserId().equals(userId)) {
                result.add(transaction);
            }
        }
        return result;
    }

    public List<Transaction> findAllByUserIdAndTransactionType(UUID userId, TransactionType type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactionData) {
            if (transaction.getUserId().equals(userId) && transaction.getType() == type) {
                result.add(transaction);
            }
        }
        return result;
    }

    public void delete(UUID transactionId) {
        findById(transactionId).ifPresent(transactionData::remove);
    }
}