package id.ac.ui.cs.advprog.papikosbe.factory;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WalletFactoryTest {

    private WalletFactory walletFactory;

    @BeforeEach
    void setUp() {
        walletFactory = new WalletFactory();
    }

    @Test
    void testCreateWallet() {
        User user = new Tenant();
        user.setId(java.util.UUID.randomUUID());

        Wallet wallet = walletFactory.createWallet(user);

        assertNotNull(wallet);
        assertEquals(user, wallet.getUser());
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }
}

