package id.ac.ui.cs.advprog.papikosbe.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Payment {
    private UUID id;
    private UUID userId;
    private UUID ownerId;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    public Payment(UUID id, UUID userId, UUID ownerId, BigDecimal amount, LocalDateTime timestamp) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        this.id = id;
        this.userId = userId;
        this.ownerId = ownerId;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}