package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final Map<UUID, Transaction> transactionRepository = new HashMap<>();

    @Override
    public Transaction create(Transaction transaction) {
        transactionRepository.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactionRepository.values());
    }

    @Override
    public Transaction findById(UUID id) {
        return transactionRepository.get(id);
    }

    @Override
    public List<Transaction> findAllByUserId(UUID userId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactionRepository.values()) {
            if (transaction.getUserId().equals(userId)) {
                result.add(transaction);
            }
        }
        return result;
    }
}