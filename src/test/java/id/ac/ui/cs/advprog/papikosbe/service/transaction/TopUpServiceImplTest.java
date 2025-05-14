package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.factory.TopUpFactory;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.TopUpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopUpServiceImplTest {

    @Mock
    private TopUpRepository topUpRepository;

    @Mock
    private TopUpFactory topUpFactory;

    @InjectMocks
    private TopUpServiceImpl topUpService;

    private TopUp topUp;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        topUp = TopUpFactory.createTopUp(userId, amount); // Menggunakan factory
    }

    @Test
    void testCreateTopUp() {
        when(topUpRepository.save(topUp)).thenReturn(topUp); // Mocking behavior

        TopUp result = topUpService.create(topUp);

        assertEquals(topUp, result);
        verify(topUpRepository).save(topUp);
    }

    @Test
    void testFindAllTopUps() {
        List<TopUp> topUps = List.of(topUp);

        when(topUpRepository.findAll()).thenReturn(topUps); // Mocking behavior

        List<TopUp> result = topUpService.findAll();

        assertEquals(1, result.size());
        assertEquals(topUp, result.getFirst());

        verify(topUpRepository).findAll();
    }

    @Test
    void testFindTopUpById() {
        when(topUpRepository.findById(topUp.getId())).thenReturn(Optional.of(topUp)); // Mocking behavior

        TopUp result = topUpService.findById(topUp.getId());

        assertEquals(topUp, result);
        verify(topUpRepository).findById(topUp.getId());
    }
}
