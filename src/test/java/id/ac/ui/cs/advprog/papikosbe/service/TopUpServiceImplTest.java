package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.factory.TopUpFactory;
import id.ac.ui.cs.advprog.papikosbe.model.TopUp;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TopUpServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TopUpServiceImplTest {

    @InjectMocks
    TopUpServiceImpl topUpService;

    private TopUp topUp;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        topUp = TopUpFactory.createTopUp(userId, amount); // Menggunakan factory
    }

    @Test
    void testCreateTopUp() {
        assertEquals(topUp, topUpService.create(topUp));
    }

    @Test
    void testFindAllTopUps() {
        topUpService.create(topUp);
        assertEquals(topUp, topUpService.findAll().getFirst());
    }

    @Test
    void testFindTopUpById() {
        topUpService.create(topUp);
        assertEquals(topUp, topUpService.findById(topUp.getId()));
    }
}
