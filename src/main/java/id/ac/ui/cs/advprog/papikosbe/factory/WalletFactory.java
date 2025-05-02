package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.model.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class WalletFactory {

    public Wallet createWallet(UUID userId) {
        return new Wallet(UUID.randomUUID(), userId, BigDecimal.ZERO);
    }
}
