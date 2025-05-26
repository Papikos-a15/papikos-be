package id.ac.ui.cs.advprog.papikosbe.dto;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private UUID id;
    private TransactionType type;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private UUID userId;
    private UUID ownerId;
    private String status_message;
    private String message;
    private String error;

    public TransactionResponse(String status_message, String message) {
        this.status_message = status_message;
        this.message = message;
    }

}
