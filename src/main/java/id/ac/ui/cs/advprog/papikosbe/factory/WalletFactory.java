package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.model.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public class WalletFactory {

    public static Wallet createWallet(UUID userId) {
        return new Wallet(UUID.randomUUID(), userId, BigDecimal.ZERO); // default saldo 0
    }
}
