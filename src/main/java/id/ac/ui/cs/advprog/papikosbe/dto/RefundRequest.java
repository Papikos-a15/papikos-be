package id.ac.ui.cs.advprog.papikosbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {
    private UUID paymentId;
    private UUID requesterId;
}
