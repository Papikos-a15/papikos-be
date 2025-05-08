package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletRepositoryTest {

    private WalletRepository walletRepository;

    private UUID walletId;
    private UUID userId;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        walletRepository = Mockito.mock(WalletRepository.class);

        // Shared data
        walletId = UUID.randomUUID();
        userId = UUID.randomUUID();
        wallet = new Wallet(walletId, userId, new BigDecimal("100.00"));
    }

    @Test
    void testFindByUserIdSuccess() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Optional<Wallet> found = walletRepository.findByUserId(userId);

        assertTrue(found.isPresent());
        assertEquals(userId, found.get().getUserId());
    }

    @Test
    void testFindByUserIdNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        when(walletRepository.findByUserId(unknownUserId)).thenReturn(Optional.empty());

        Optional<Wallet> found = walletRepository.findByUserId(unknownUserId);

        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateBalanceSuccess() {
        Wallet updatedWallet = new Wallet(walletId, userId, new BigDecimal("200.00"));
        when(walletRepository.updateBalance(updatedWallet)).thenReturn(updatedWallet);

        Wallet result = walletRepository.updateBalance(updatedWallet);

        assertNotNull(result);
        assertEquals(new BigDecimal("200.00"), result.getBalance());
    }
}