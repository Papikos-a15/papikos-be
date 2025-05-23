package id.ac.ui.cs.advprog.papikosbe.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class WalletRequest {
    private UUID id;
    private UUID userId;
    private BigDecimal balance;
}
