package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.factory.WalletFactory;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @InjectMocks
    WalletServiceImpl walletService;

    @Mock
    WalletRepository walletRepository;

    @Mock
    WalletFactory walletFactory;

    Wallet wallet;
    UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        wallet = new Wallet(UUID.randomUUID(), userId, BigDecimal.ZERO); // manual, bukan dari factory
    }

    @Test
    void testCreateWallet() {
        when(walletFactory.createWallet(userId)).thenReturn(wallet);
        when(walletRepository.create(any(Wallet.class))).thenReturn(wallet);

        Wallet createdWallet = walletService.create(userId);

        assertNotNull(createdWallet);
        assertEquals(userId, createdWallet.getUserId());
    }

    @Test
    void testFindAllWallets() {
        Iterator<Wallet> walletIterator = Collections.singletonList(wallet).iterator();
        when(walletRepository.findAll()).thenReturn(walletIterator);

        var wallets = walletService.findAll();

        assertNotNull(wallets);
        assertEquals(1, wallets.size());
        assertEquals(wallet, wallets.getFirst());
    }

    @Test
    void testFindWalletById() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        Wallet foundWallet = walletService.findById(wallet.getId());

        assertNotNull(foundWallet);
        assertEquals(wallet.getId(), foundWallet.getId());
    }

    @Test
    void testEditWallet() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        Wallet updatedWallet = new Wallet(wallet.getId(), userId, new BigDecimal("500.00"));

        Wallet editedWallet = walletService.edit(wallet.getId(), updatedWallet);

        assertNotNull(editedWallet);
        assertEquals(new BigDecimal("500.00"), editedWallet.getBalance());
    }

    @Test
    void testDeleteWallet() {
        UUID walletId = wallet.getId();
        doNothing().when(walletRepository).delete(walletId);

        walletService.delete(walletId);

        verify(walletRepository, times(1)).delete(walletId);
    }
}
