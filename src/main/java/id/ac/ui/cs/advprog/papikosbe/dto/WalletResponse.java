package id.ac.ui.cs.advprog.papikosbe.dto;

import id.ac.ui.cs.advprog.papikosbe.enums.WalletStatus;
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
    private WalletStatus status;

    public WalletResponse(UUID id, BigDecimal balance, UUID userId, WalletStatus status) {
        this.id = id;
        this.balance = balance;
        this.userId = userId;
        this.status = status;
    }
}


