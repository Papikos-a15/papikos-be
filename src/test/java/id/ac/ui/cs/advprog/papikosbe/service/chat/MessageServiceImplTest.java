package id.ac.ui.cs.advprog.papikosbe.service.chat;

import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.MessageRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.RoomChatRepository;
import id.ac.ui.cs.advprog.papikosbe.strategy.chat.SendStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageServiceImplTest {

    private MessageRepository messageRepository;
    private RoomChatRepository roomChatRepository;
    private SendStrategy sendToOneStrategy;
    private MessageService messageService;

    private RoomChat room;
    private UUID senderId;

    @BeforeEach
    void setUp() {
        messageRepository = mock(MessageRepository.class);
        roomChatRepository = mock(RoomChatRepository.class);
        sendToOneStrategy = mock(SendStrategy.class);

        List<SendStrategy> strategies = List.of(sendToOneStrategy);
        when(sendToOneStrategy.getType()).thenReturn(SendType.TO_ONE);

        messageService = new MessageServiceImpl(messageRepository, roomChatRepository, strategies);

        senderId = UUID.randomUUID();
        room = new RoomChat(UUID.randomUUID(), UUID.randomUUID());
    }

    @Test
    void testSaveMessage_ShouldUseStrategySend() {
        Message message = new Message(room, senderId, "Halo!", SendType.TO_ONE);
        messageService.saveMessage(message);

        verify(sendToOneStrategy, times(1)).send(message);
    }

    @Test
    void testEditMessage_ShouldSetEditedAndSave() {
        Message message = new Message(room, senderId, "Old", SendType.TO_ONE);
        messageService.editMessage(message);

        assertTrue(message.isEdited());
        verify(messageRepository, times(1)).save(message);
    }

    @Test
    void testDeleteMessage_ShouldDeleteWhenExists() {
        UUID messageId = UUID.randomUUID();
        when(messageRepository.existsById(messageId)).thenReturn(true);

        boolean result = messageService.deleteMessage(messageId);

        assertTrue(result);
        verify(messageRepository).deleteById(messageId);
    }

    @Test
    void testDeleteMessage_ShouldReturnFalseIfNotExists() {
        UUID messageId = UUID.randomUUID();
        when(messageRepository.existsById(messageId)).thenReturn(false);

        boolean result = messageService.deleteMessage(messageId);

        assertFalse(result);
        verify(messageRepository, never()).deleteById(any());
    }

    @Test
    void testGetMessagesByRoomId_ShouldReturnSortedMessages() {
        UUID roomId = UUID.randomUUID();
        when(roomChatRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(messageRepository.findByRoomChatOrderByTimestampAsc(room))
                .thenReturn(List.of(
                        new Message(room, senderId, "First", SendType.TO_ONE),
                        new Message(room, senderId, "Second", SendType.TO_ONE)
                ));

        List<Message> result = messageService.getMessagesByRoomId(roomId);

        assertEquals(2, result.size());
        assertEquals("First", result.get(0).getContent());
    }

    @Test
    void testGetMessageById_ShouldReturnMessage() {
        UUID id = UUID.randomUUID();
        Message mock = new Message(room, senderId, "Hello", SendType.TO_ONE);

        when(messageRepository.findById(id)).thenReturn(Optional.of(mock));

        Message result = messageService.getMessageById(id);
        assertEquals("Hello", result.getContent());
    }

    @Test
    void testGetMessageById_NotFound_ShouldThrow() {
        UUID id = UUID.randomUUID();
        when(messageRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> messageService.getMessageById(id));
    }
}
