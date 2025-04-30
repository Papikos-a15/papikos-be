package id.ac.ui.cs.advprog.papikosbe.factory;
import id.ac.ui.cs.advprog.papikosbe.model.Payment;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentFactory {
    Payment createPayment(UUID userId, UUID ownerId, BigDecimal amount);
}
