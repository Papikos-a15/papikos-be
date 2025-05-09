package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TopUpRepositoryTest {

    private TopUpRepository topUpRepository;

    private UUID topUpId;
    private UUID userId;
    private TopUp topUp1;
    private TopUp topUp2;

    @BeforeEach
    void setUp() {
        topUpRepository = new TopUpRepository();

        topUpId = UUID.randomUUID();
        userId = UUID.randomUUID();

        topUp1 = new TopUp(topUpId, userId, new BigDecimal("50.00"), LocalDateTime.now());
        topUp2 = new TopUp(UUID.randomUUID(), userId, new BigDecimal("75.00"), LocalDateTime.now());

        topUpRepository.save(topUp1);
        topUpRepository.save(topUp2);
    }

    @Test
    void testSaveTopUpCreatesNew() {
        TopUp newTopUp = new TopUp(UUID.randomUUID(), userId, new BigDecimal("100.00"), LocalDateTime.now());

        TopUp savedTopUp = topUpRepository.save(newTopUp);

        assertNotNull(savedTopUp);
        assertEquals(newTopUp.getAmount(), savedTopUp.getAmount());
    }

    @Test
    void testSaveTopUpUpdatesExisting() {
        TopUp updatedTopUp = new TopUp(topUp1.getId(), userId, new BigDecimal("200.00"), topUp1.getTimestamp());

        TopUp saved = topUpRepository.save(updatedTopUp);

        Optional<TopUp> found = topUpRepository.findById(topUp1.getId());

        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("200.00"), found.get().getAmount());
    }

    @Test
    void testFindTopUpByIdSuccess() {
        Optional<TopUp> found = topUpRepository.findById(topUp1.getId());

        assertTrue(found.isPresent());
        assertEquals(topUp1.getId(), found.get().getId());
    }

    @Test
    void testFindTopUpByIdNotFound() {
        Optional<TopUp> found = topUpRepository.findById(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUserIdSuccess() {
        List<TopUp> topUps = topUpRepository.findByUserId(userId);

        assertEquals(2, topUps.size());
        assertTrue(topUps.stream().allMatch(t -> t.getUserId().equals(userId)));
    }

    @Test
    void testFindByUserIdNotFound() {
        List<TopUp> topUps = topUpRepository.findByUserId(UUID.randomUUID());

        assertTrue(topUps.isEmpty());
    }

    @Test
    void testFindByDateMatch() {
        LocalDateTime date = topUp1.getTimestamp();

        List<TopUp> result = topUpRepository.findByDate(date);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(t -> t.getId().equals(topUp1.getId())));
    }

    @Test
    void testFindByDateNoMatch() {
        LocalDateTime date = LocalDateTime.of(1990, 1, 1, 0, 0);

        List<TopUp> result = topUpRepository.findByDate(date);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAll() {
        List<TopUp> all = topUpRepository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void testDeleteTopUp() {
        topUpRepository.delete(topUp1.getId());

        Optional<TopUp> result = topUpRepository.findById(topUp1.getId());

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteNonExistentTopUp() {
        // Should not throw error
        topUpRepository.delete(UUID.randomUUID());
    }
}
