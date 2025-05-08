package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.factory.TransactionFactory;
import id.ac.ui.cs.advprog.papikosbe.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final Map<UUID, Transaction> transactionRepository = new HashMap<>();
    private final TransactionFactory transactionFactory;

    public TransactionServiceImpl(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    public Transaction createTransaction(UUID userId, BigDecimal amount, TransactionType type) {
        Transaction transaction = transactionFactory.createTransaction(userId, amount, type);
        return create(transaction);
    }

    @Override
    public Transaction create(Transaction transaction) {
        Transaction created = transactionFactory.createTransaction(
                transaction.getUserId(),
                transaction.getAmount(),
                transaction.getType()
        );
        transactionRepository.put(created.getId(), created);
        return created;
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

    @Override
    public List<Transaction> findByType(TransactionType type) {
        return transactionRepository.values().stream()
                .filter(transaction -> transaction.getType().equals(type))
                .toList();
    }

    @Override
    public List<Transaction> findByDate(LocalDateTime date){
        return transactionRepository.values().stream()
                .filter(transaction -> transaction.getTimestamp().toLocalDate().equals(date.toLocalDate()))
                .toList();
    }
}
