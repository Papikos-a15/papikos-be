package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class TransactionFactory {

    private final Map<TransactionType, TransactionCreator> creators;

    @Autowired
    public TransactionFactory(TopUpTransactionCreator topUpCreator,
                              PaymentTransactionCreator paymentCreator) {
        this.creators = Map.of(
                TransactionType.TOP_UP, topUpCreator,
                TransactionType.PAYMENT, paymentCreator
        );
    }

    public Transaction createTransaction(TransactionType type, UUID userId, BigDecimal amount, UUID ownerId) throws Exception {
        TransactionCreator creator = creators.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown transaction type: " + type);
        }
        return creator.create(userId, amount, ownerId);
    }
}
