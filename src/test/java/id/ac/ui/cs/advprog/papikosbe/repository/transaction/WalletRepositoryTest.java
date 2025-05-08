package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletRepositoryTest {

    private WalletRepository walletRepository;

    private UUID walletId;
    private UUID userId;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        walletRepository = new WalletRepository(); // Jangan pakai mock

        walletId = UUID.randomUUID();
        userId = UUID.randomUUID();
        wallet = new Wallet(walletId, userId, new BigDecimal("100.00"));

        walletRepository.create(wallet);
    }

    @Test
    void testCreate() {
        Wallet newWallet = new Wallet(null, UUID.randomUUID(), new BigDecimal("50.00"));
        Wallet created = walletRepository.create(newWallet);

        assertNotNull(created.getId());
        assertEquals(new BigDecimal("50.00"), created.getBalance());
    }

    @Test
    void testEditSuccess() {
        Wallet updatedData = new Wallet(walletId, userId, new BigDecimal("300.00"));
        Wallet edited = walletRepository.edit(walletId, updatedData);

        assertNotNull(edited);
        assertEquals(new BigDecimal("300.00"), edited.getBalance());
    }

    @Test
    void testEditNotFound() {
        UUID unknownId = UUID.randomUUID();
        Wallet updatedData = new Wallet(unknownId, userId, new BigDecimal("300.00"));

        Wallet result = walletRepository.edit(unknownId, updatedData);

        assertNull(result);
    }

    @Test
    void testFindAll() {
        Iterator<Wallet> iterator = walletRepository.findAll();
        assertTrue(iterator.hasNext());
        Wallet found = iterator.next();
        assertEquals(walletId, found.getId());
    }

    @Test
    void testFindByIdSuccess() {
        Optional<Wallet> found = walletRepository.findById(walletId);
        assertTrue(found.isPresent());
        assertEquals(walletId, found.get().getId());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Wallet> found = walletRepository.findById(UUID.randomUUID());
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUserIdSuccess() {
        Optional<Wallet> found = walletRepository.findByUserId(userId);
        assertTrue(found.isPresent());
        assertEquals(userId, found.get().getUserId());
    }

    @Test
    void testFindByUserIdNotFound() {
        Optional<Wallet> found = walletRepository.findByUserId(UUID.randomUUID());
        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateBalanceSuccess() {
        Wallet updated = new Wallet(walletId, userId, new BigDecimal("200.00"));
        Wallet result = walletRepository.updateBalance(updated);

        assertNotNull(result);
        assertEquals(new BigDecimal("200.00"), result.getBalance());
    }

    @Test
    void testUpdateBalanceNotFound() {
        Wallet unknown = new Wallet(UUID.randomUUID(), userId, new BigDecimal("123.45"));
        Wallet result = walletRepository.updateBalance(unknown);
        assertNull(result);
    }

    @Test
    void testDelete() {
        walletRepository.delete(walletId);
        Optional<Wallet> deleted = walletRepository.findById(walletId);
        assertFalse(deleted.isPresent());
    }
}