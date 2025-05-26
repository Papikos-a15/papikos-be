package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionCreator {
    Transaction create(UUID userId, BigDecimal amount, UUID ownerId) throws Exception;
}