package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.TopUp;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
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

    TopUp topUp;

    @BeforeEach
    void setUp() {
        topUp = new TopUp();
        topUp.setId(UUID.randomUUID());
        topUp.setUserId(UUID.randomUUID());
        topUp.setAmount(new BigDecimal("100.00"));
        topUp.setType(TransactionType.TOP_UP);
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
