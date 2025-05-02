package id.ac.ui.cs.advprog.papikosbe.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

class WalletTest {
    UUID walletId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    @Test
    void testCreateWalletSuccess() {
        Wallet wallet = new Wallet(walletId, userId, new BigDecimal("100.00"));

        assertEquals(walletId, wallet.getId());
        assertEquals(userId, wallet.getUserId());
        assertEquals(new BigDecimal("100.00"), wallet.getBalance());
    }

    @Test
    void testUpdateBalanceSuccess() {
        Wallet wallet = new Wallet(walletId, userId, new BigDecimal("100.00"));
        wallet.setBalance(new BigDecimal("150.00"));

        assertEquals(new BigDecimal("150.00"), wallet.getBalance());
    }

    @Test
    void testCreateWalletNullBalance() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Wallet(walletId, userId, null);
        });

        assertEquals("Balance cannot be null", exception.getMessage());
    }
}

