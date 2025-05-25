package id.ac.ui.cs.advprog.papikosbe.dto;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponse {
    private UUID id;
    private UUID fromUserId;
    private UUID toUserId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private TransactionStatus status;

    public RefundResponse(Payment payment) {
        this.id = payment.getId();
        if (payment.getUser() != null) {
            this.fromUserId = payment.getUser().getId(); // Ensuring payment.getUser() is not null
        } else {
            this.fromUserId = null;
        }

        if (payment.getOwner() != null) {
            this.toUserId = payment.getOwner().getId(); // Ensuring payment.getOwner() is not null
        } else {
            this.toUserId = null;
        }

        this.amount = payment.getAmount();
        this.createdAt = payment.getCreatedAt();
        this.status = payment.getStatus();
    }
}

