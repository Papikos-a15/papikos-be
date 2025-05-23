package id.ac.ui.cs.advprog.papikosbe.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class WalletResponse {
    private UUID id;
    private BigDecimal balance;
    private UUID userId;

    public WalletResponse(UUID id, BigDecimal balance, UUID userId) {
        this.id = id;
        this.balance = balance;
        this.userId = userId;
    }
}


