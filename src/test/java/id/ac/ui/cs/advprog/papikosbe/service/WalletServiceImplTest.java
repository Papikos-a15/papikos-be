package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @InjectMocks
    WalletServiceImpl walletService;

    Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setUserId(UUID.randomUUID());
        wallet.setBalance(BigDecimal.ZERO);
    }

    @Test
    void testCreateWallet() {
        assertEquals(wallet, walletService.create(wallet));
    }

    @Test
    void testFindAllWallets() {
        walletService.create(wallet);
        assertEquals(wallet, walletService.findAll().getFirst());
    }

    @Test
    void testFindWalletById() {
        walletService.create(wallet);
        assertEquals(wallet, walletService.findById(wallet.getId()));
    }

    @Test
    void testEditWallet() {
        walletService.create(wallet);
        Wallet updatedWallet = new Wallet();
        updatedWallet.setBalance(new BigDecimal("500.00"));

        assertEquals(updatedWallet.getBalance(), walletService.edit(wallet.getId(), updatedWallet).getBalance());
    }

    @Test
    void testDeleteWallet() {
        walletService.create(wallet);
        walletService.delete(wallet.getId());
        assertTrue(walletService.findAll().isEmpty());
    }
}