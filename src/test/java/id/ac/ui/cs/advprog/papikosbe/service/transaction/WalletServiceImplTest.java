package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.WalletRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    UserRepository userRepository;

    Wallet wallet;
    User user;
    UUID walletId;
    UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        walletId = UUID.randomUUID();

        user = Tenant.builder()
                .email("nae@example.com")
                .password("securepass123")
                .build();
        user.setId(userId);

        wallet = new Wallet(user, new BigDecimal("100.00"));
        wallet.setId(walletId);
    }


    @Test
    void testCreateWallet() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet createdWallet = walletService.create(user.getId());

        assertNotNull(createdWallet);
        assertEquals(user, createdWallet.getUser());
    }

    @Test
    void testCreateWallet_UserNotFound() {
        UUID unknownUserId = UUID.randomUUID();

        when(userRepository.findById(unknownUserId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            walletService.create(unknownUserId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(unknownUserId);
        verify(walletRepository, never()).save(any(Wallet.class));
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
        UUID walletId = wallet.getId();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet updatedWallet = new Wallet(user, new BigDecimal("500.00"));
        updatedWallet.setId(walletId);

        Wallet editedWallet = walletService.edit(walletId, updatedWallet);

        assertNotNull(editedWallet);
        assertEquals(new BigDecimal("500.00"), editedWallet.getBalance());
    }

    @Test
    void testEditWallet_WalletNotFound() {
        UUID walletId = UUID.randomUUID();
        Wallet updatedWallet = new Wallet(user, new BigDecimal("500.00"));
        updatedWallet.setId(walletId);

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        Wallet result = walletService.edit(walletId, updatedWallet);

        assertNull(result);
        verify(walletRepository).findById(walletId);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testDeleteWallet() {
        UUID walletId = wallet.getId();

        doNothing().when(walletRepository).deleteById(walletId);

        walletService.delete(walletId);

        verify(walletRepository, times(1)).deleteById(walletId);
    }

    @Test
    void testFindByUserId_WalletExists() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.findByUserId(userId);

        assertNotNull(result);
        assertEquals(wallet, result);
        verify(walletRepository).findByUserId(userId);
    }

    @Test
    void testFindByUserId_WalletNotFound() {
        UUID userId = UUID.randomUUID();

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
        Wallet result = walletService.findByUserId(userId);
        assertNull(result);

        verify(walletRepository).findByUserId(userId);
    }

    @Test
    void testGetOrCreateWallet_WalletExists() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getOrCreateWallet(user);

        assertNotNull(result);
        assertEquals(wallet, result);
        verify(walletRepository).findByUserId(userId);
        verifyNoMoreInteractions(walletRepository);
    }

    @Test
    void testGetOrCreateWallet_WalletDoesNotExist() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Wallet newWallet = new Wallet(user, BigDecimal.ZERO);

        // Override create behavior
        WalletService walletServiceSpy = Mockito.spy(walletService);
        doReturn(newWallet).when(walletServiceSpy).create(userId);

        Wallet result = walletServiceSpy.getOrCreateWallet(user);

        assertNotNull(result);
        assertEquals(newWallet, result);
        verify(walletServiceSpy).create(userId);
        verify(walletRepository).findByUserId(userId);
    }
}
