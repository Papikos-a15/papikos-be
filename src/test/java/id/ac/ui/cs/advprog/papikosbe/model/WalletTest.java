package id.ac.ui.cs.advprog.papikosbe.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

class WalletTest {

    @Test
    void testCreateWalletSuccess() {
        Wallet wallet = new Wallet(1L, 10L, new BigDecimal("100.00"));

        assertEquals(1L, wallet.getId());
        assertEquals(10L, wallet.getUserId());
        assertEquals(new BigDecimal("100.00"), wallet.getBalance());
    }

    @Test
    void testUpdateBalanceSuccess() {
        Wallet wallet = new Wallet(1L, 10L, new BigDecimal("100.00"));
        wallet.setBalance(new BigDecimal("150.00"));

        assertEquals(new BigDecimal("150.00"), wallet.getBalance());
    }

    @Test
    void testCreateWalletNullBalance() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Wallet(1L, 10L, null);
        });

        assertEquals("Balance cannot be null", exception.getMessage());
    }
}

