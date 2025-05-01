package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.model.TopUp;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TopUpFactory {

    public static TopUp createTopUp(UUID userId, BigDecimal amount) {
        return new TopUp(UUID.randomUUID(), userId, amount, LocalDateTime.now());
    }
}
