package id.ac.ui.cs.advprog.papikosbe.model.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.WalletStatus;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TopUpTest {

    private TopUp topUp;
    private Wallet userWallet;
    private User user;

    @BeforeEach
    void setUp() {
        user = mock(User.class);

        userWallet = new Wallet();
        userWallet.setUser(user);
        userWallet.setStatus(WalletStatus.ACTIVE);
        userWallet.setBalance(new BigDecimal("50000"));

        topUp = new TopUp();
        topUp.setUser(user);
        topUp.setAmount(new BigDecimal("100000"));
    }

    @Test
    void validateTransaction_ShouldNotThrow_WhenValid() {
        assertDoesNotThrow(() -> topUp.validateTransaction(userWallet));
    }

    @Test
    void validateTransaction_ShouldThrow_WhenUserIsNull() {
        topUp.setUser(null);
        Exception exception = assertThrows(Exception.class, () -> topUp.validateTransaction(userWallet));
        assertEquals("User tidak ditemukan", exception.getMessage());
    }

    @Test
    void validateTransaction_ShouldThrow_WhenWalletIsNull() {
        Exception exception = assertThrows(Exception.class, () -> topUp.validateTransaction(null));
        assertEquals("Wallet user tidak valid", exception.getMessage());
    }

    @Test
    void validateTransaction_ShouldThrow_WhenWalletIsInactive() {
        userWallet.setStatus(WalletStatus.CLOSED);
        Exception exception = assertThrows(Exception.class, () -> topUp.validateTransaction(userWallet));
        assertEquals("Wallet user tidak valid", exception.getMessage());
    }

    @Test
    void validateTransaction_ShouldThrow_WhenAmountIsZero() {
        topUp.setAmount(BigDecimal.ZERO);
        Exception exception = assertThrows(Exception.class, () -> topUp.validateTransaction(userWallet));
        assertEquals("Jumlah top up harus lebih dari 0", exception.getMessage());
    }

    @Test
    void validateTransaction_ShouldThrow_WhenAmountIsNegative() {
        topUp.setAmount(new BigDecimal("-10000"));
        Exception exception = assertThrows(Exception.class, () -> topUp.validateTransaction(userWallet));
        assertEquals("Jumlah top up harus lebih dari 0", exception.getMessage());
    }

    @Test
    void doProcess_ShouldAddBalanceAndSetTopUpDate() throws Exception {
        BigDecimal initialBalance = userWallet.getBalance();

        topUp.doProcess(userWallet, null);

        assertEquals(initialBalance.add(topUp.getAmount()), userWallet.getBalance());
        assertNotNull(topUp.getTopUpDate());
        assertTrue(topUp.getTopUpDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
