package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.factory.WalletFactory;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

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
    User user;

    @BeforeEach
    void setUp() {
        UUID walletId = UUID.randomUUID();

        user = Tenant.builder()
                .email("nae@example.com")
                .password("securepass123")
                .build();

        wallet = new Wallet(user, new BigDecimal("100.00"));
        wallet.setId(walletId);
    }

    @Test
    void testCreateWallet() {
        when(walletFactory.createWallet(user)).thenReturn(wallet);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet createdWallet = walletService.create(user.getId());

        assertNotNull(createdWallet);
        assertEquals(user, createdWallet.getUser());
    }

    @Test
    void testFindAllWallets() {
        when(walletRepository.findAll()).thenReturn(List.of(wallet));

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

        Wallet updatedWallet = new Wallet(user, new BigDecimal("500.00"));
        updatedWallet.setId(wallet.getId());

        Wallet editedWallet = walletService.edit(wallet.getId(), updatedWallet);

        assertNotNull(editedWallet);
        assertEquals(new BigDecimal("500.00"), editedWallet.getBalance());
    }

    @Test
    void testDeleteWallet() {
        UUID walletId = wallet.getId();
        doNothing().when(walletRepository).delete(wallet);

        walletService.delete(walletId);

        verify(walletRepository, times(1)).delete(wallet);
    }
}
