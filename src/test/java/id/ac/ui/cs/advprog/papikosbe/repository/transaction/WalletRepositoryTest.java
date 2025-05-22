package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.WalletStatus;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository UserRepository;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        user = Tenant.builder()
                .email("nae@example.com")
                .password("securepass123")
                .build();

        user = UserRepository.save(user);

        wallet = new Wallet(user, new BigDecimal("100.00"));
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet = walletRepository.save(wallet);
    }


    @Test
    void testSaveAndFindById() {
        Optional<Wallet> found = walletRepository.findById(wallet.getId());
        assertTrue(found.isPresent());
        assertEquals(wallet.getId(), found.get().getId());
    }

    @Test
    void testFindByUserId() {
        Optional<Wallet> found = walletRepository.findByUserId(user.getId());
        assertTrue(found.isPresent());
        assertEquals(wallet.getId(), found.get().getId());
    }

    @Test
    void testUpdateBalance() {
        wallet.setBalance(new BigDecimal("250.00"));
        Wallet updated = walletRepository.save(wallet);

        assertEquals(new BigDecimal("250.00"), updated.getBalance());
    }

    @Test
    void testDelete() {
        walletRepository.delete(wallet);
        Optional<Wallet> found = walletRepository.findById(wallet.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        var all = walletRepository.findAll();
        assertEquals(1, all.size());
    }
}
