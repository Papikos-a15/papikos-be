package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;


public interface TransactionFactory {
    Transaction createTransaction(UUID userId, BigDecimal amount, TransactionType type);
}
