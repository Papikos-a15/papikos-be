package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.TopUp;
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
        topUpRepository = Mockito.mock(TopUpRepository.class);

        topUpId = UUID.randomUUID();
        userId = UUID.randomUUID();

        topUp1 = new TopUp(topUpId, userId, new BigDecimal("50.00"), LocalDateTime.now());
        topUp2 = new TopUp(UUID.randomUUID(), userId, new BigDecimal("75.00"), LocalDateTime.now());
    }

    @Test
    void testSaveTopUpSuccess() {
        when(topUpRepository.save(topUp1)).thenReturn(topUp1);

        TopUp savedTopUp = topUpRepository.save(topUp1);

        assertNotNull(savedTopUp);
        assertEquals(topUp1.getAmount(), savedTopUp.getAmount());
    }

    @Test
    void testFindTopUpByIdSuccess() {
        when(topUpRepository.findById(topUpId)).thenReturn(Optional.of(topUp1));

        Optional<TopUp> found = topUpRepository.findById(topUpId);

        assertTrue(found.isPresent());
        assertEquals(topUpId, found.get().getId());
    }

    @Test
    void testFindTopUpByIdNotFound() {
        UUID unknownTopUpId = UUID.randomUUID();
        when(topUpRepository.findById(unknownTopUpId)).thenReturn(Optional.empty());

        Optional<TopUp> found = topUpRepository.findById(unknownTopUpId);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUserIdSuccess() {
        when(topUpRepository.findByUserId(userId)).thenReturn(List.of(topUp1, topUp2));

        List<TopUp> topUps = topUpRepository.findByUserId(userId);

        assertFalse(topUps.isEmpty());
        assertEquals(2, topUps.size());
        assertEquals(userId, topUps.get(0).getUserId());
    }

    @Test
    void testFindByUserIdNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        when(topUpRepository.findByUserId(unknownUserId)).thenReturn(List.of());

        List<TopUp> topUps = topUpRepository.findByUserId(unknownUserId);

        assertTrue(topUps.isEmpty());
    }
}