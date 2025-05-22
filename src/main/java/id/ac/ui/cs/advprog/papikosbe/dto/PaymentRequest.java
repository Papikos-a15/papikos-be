
package id.ac.ui.cs.advprog.papikosbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private UUID tenantId;
    private UUID ownerId;
    private BigDecimal amount;
}