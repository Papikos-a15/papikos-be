package id.ac.ui.cs.advprog.papikosbe.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TopUpTransactionCreatorTest {

    @Mock
    private UserRepository userRepository;

    private TopUpTransactionCreator topUpTransactionCreator;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        topUpTransactionCreator = new TopUpTransactionCreator(userRepository);

        userId = UUID.randomUUID();
        user = new Tenant();
        user.setId(userId);
    }

    @Test
    void testCreateTopUpSuccess() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BigDecimal amount = BigDecimal.valueOf(200);
        TopUp result = (TopUp) topUpTransactionCreator.create(userId, amount, null);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(amount, result.getAmount());

        verify(userRepository).findById(userId);
    }

    @Test
    void testCreateTopUpThrowsWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () ->
                topUpTransactionCreator.create(userId, BigDecimal.TEN, null)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
    }
}

