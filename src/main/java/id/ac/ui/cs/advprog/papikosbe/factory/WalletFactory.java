package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WalletFactory {
    public Wallet createWallet(User user) {
        return new Wallet(user, BigDecimal.ZERO);
    }
}

