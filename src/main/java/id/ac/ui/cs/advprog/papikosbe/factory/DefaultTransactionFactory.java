package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DefaultTransactionFactory implements TransactionFactory {
    @Override
    public Transaction createTransaction(UUID userId, BigDecimal amount, TransactionType type) {
        return new Transaction(UUID.randomUUID(), userId, amount, type, LocalDateTime.now());
    }
}
