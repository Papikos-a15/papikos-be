package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Message;
import id.ac.ui.cs.advprog.papikosbe.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessageServiceImplTest {

    private MessageRepository messageRepository;
    private MessageServiceImpl messageService;

    @BeforeEach
    void setUp() {
        messageRepository = mock(MessageRepository.class);
        messageService = new MessageServiceImpl(messageRepository);
    }

    @Test
    void testSaveMessageShouldCallRepository() {
        Message message = new Message(UUID.randomUUID(), UUID.randomUUID(), "Hello");
        doNothing().when(messageRepository).createMessage(message);

        messageService.saveMessage(message);

        verify(messageRepository, times(1)).createMessage(message);
    }

    @Test
    void testEditMessageShouldCallRepository() {
        Message message = new Message(UUID.randomUUID(), UUID.randomUUID(), "Edited");
        doNothing().when(messageRepository).editMessage(message);

        messageService.editMessage(message);

        verify(messageRepository, times(1)).editMessage(message);
    }

    @Test
    void testDeleteMessageShouldReturnTrueIfDeleted() {
        UUID messageId = UUID.randomUUID();
        when(messageRepository.deleteMessage(messageId)).thenReturn(true);

        boolean result = messageService.deleteMessage(messageId);

        assertTrue(result);
        verify(messageRepository, times(1)).deleteMessage(messageId);
    }

    @Test
    void testGetMessagesByRoomIdShouldReturnMessages() {
        UUID roomId = UUID.randomUUID();
        List<Message> messages = List.of(new Message(UUID.randomUUID(), roomId, "Hey"));

        when(messageRepository.getMessagesByRoomId(roomId)).thenReturn(messages);

        List<Message> result = messageService.getMessagesByRoomId(roomId);

        assertEquals(messages, result);
        verify(messageRepository, times(1)).getMessagesByRoomId(roomId);
    }
}
