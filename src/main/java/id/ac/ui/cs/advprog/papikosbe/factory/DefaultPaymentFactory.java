package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DefaultPaymentFactory implements PaymentFactory {

    @Override
    public Payment createPayment(UUID userId, UUID ownerId, BigDecimal amount) {
        UUID id = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();
        return new Payment(id, userId, ownerId, amount, timestamp);
    }
}
