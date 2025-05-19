package id.ac.ui.cs.advprog.papikosbe.strategy.chat;

import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class SendToOneStrategyTest {

    private MessageRepository messageRepository;
    private SendStrategy sendStrategy;

    @BeforeEach
    void setUp() {
        messageRepository = mock(MessageRepository.class);
        sendStrategy = new SendToOneStrategy(messageRepository);
    }

    @Test
    void testSend_ShouldSaveMessageToRepository() {
        Message message = mock(Message.class);
        sendStrategy.send(message);

        verify(messageRepository, times(1)).save(message);
    }

    @Test
    void testGetType_ShouldReturnToOne() {
        assert sendStrategy.getType() == SendType.TO_ONE;
    }
}
