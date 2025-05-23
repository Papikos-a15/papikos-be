package id.ac.ui.cs.advprog.papikosbe.model.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void testCreateWalletSuccess() {
        UUID walletId = UUID.randomUUID();

        User user = Tenant.builder()
                .email("nae@example.com")
                .password("securepass123")
                .build();

        Wallet wallet = new Wallet(user, new BigDecimal("100.00"));
        wallet.setId(walletId);

        assertEquals(walletId, wallet.getId());
        assertEquals(user, wallet.getUser());
        assertEquals(new BigDecimal("100.00"), wallet.getBalance());
    }

    @Test
    void testUpdateBalanceSuccess() {
        User user = Tenant.builder()
                .email("nae@example.com")
                .password("securepass123")
                .build();

        Wallet wallet = new Wallet(user, new BigDecimal("100.00"));
        wallet.setBalance(new BigDecimal("150.00"));

        assertEquals(new BigDecimal("150.00"), wallet.getBalance());
    }

    @Test
    void testCreateWalletNullBalance() {
        User user = Tenant.builder()
                .email("nae@example.com")
                .password("securepass123")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Wallet(user, null);
        });

        assertEquals("Balance cannot be null", exception.getMessage());
    }
}
