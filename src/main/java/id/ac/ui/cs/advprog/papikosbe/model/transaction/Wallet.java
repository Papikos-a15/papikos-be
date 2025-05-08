package id.ac.ui.cs.advprog.papikosbe.model.transaction;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class Wallet {

    private UUID id;
    private UUID userId;
    private BigDecimal balance;

    public Wallet(UUID id, UUID userId, BigDecimal balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }
        this.id = id;
        this.userId = userId;
        this.balance = balance;
    }
}
