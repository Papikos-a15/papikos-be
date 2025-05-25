package id.ac.ui.cs.advprog.papikosbe.strategy.chat;

import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.MessageRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.RoomChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class SendToAllStrategyTest {

    private MessageRepository messageRepository;
    private RoomChatRepository roomChatRepository;
    private SendStrategy sendStrategy;

    private UUID pemilikKosId;

    @BeforeEach
    void setUp() {
        messageRepository = mock(MessageRepository.class);
        roomChatRepository = mock(RoomChatRepository.class);
        sendStrategy = new SendToAllStrategy(messageRepository, roomChatRepository); // RED ❌ (belum ada)

        pemilikKosId = UUID.randomUUID();
    }

    @Test
    void testSend_ShouldBroadcastMessageToAllRooms() {
        RoomChat room1 = new RoomChat(UUID.randomUUID(), pemilikKosId);
        RoomChat room2 = new RoomChat(UUID.randomUUID(), pemilikKosId);
        Message broadcast = new Message(null, pemilikKosId, "Pengumuman", SendType.TO_ALL);

        when(roomChatRepository.findAllByPemilikKosId(pemilikKosId))
                .thenReturn(List.of(room1, room2));

        sendStrategy.send(broadcast);

        verify(messageRepository, times(2)).save(any(Message.class));
    }

    @Test
    void testGetType_ShouldReturnToAll() {
        assert sendStrategy.getType() == SendType.TO_ALL;
    }
}
