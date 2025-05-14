package id.ac.ui.cs.advprog.papikosbe.model.transaction;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TopUp {
    private UUID id;
    private UUID userId;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    public TopUp(UUID id, UUID userId, BigDecimal amount, LocalDateTime timestamp) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Top-up amount must be positive");
        }
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
