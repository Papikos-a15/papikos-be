package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MessageRepositoryTest {

    private MessageRepository messageRepository;
    private UUID roomId;
    private UUID senderId;

    @BeforeEach
    void setUp() {
        messageRepository = new MessageRepository();
        roomId = UUID.randomUUID();
        senderId = UUID.randomUUID();
    }

    @Test
    void testCreateAndGetMessageById() {
        Message message = new Message(roomId, senderId, "Hello World!");
        messageRepository.createMessage(message);

        Message found = messageRepository.getMessageById(message.getId());

        assertNotNull(found);
        assertEquals("Hello World!", found.getContent());
    }

    @Test
    void testEditMessage() {
        Message message = new Message(roomId, senderId, "Old Message");
        messageRepository.createMessage(message);

        message.setContent("New Message");
        messageRepository.editMessage(message);

        Message updated = messageRepository.getMessageById(message.getId());
        assertEquals("New Message", updated.getContent());
    }

    @Test
    void testDeleteMessage() {
        Message message = new Message(roomId, senderId, "To be deleted");
        messageRepository.createMessage(message);

        boolean deleted = messageRepository.deleteMessage(message.getId());

        assertTrue(deleted);
        assertNull(messageRepository.getMessageById(message.getId()));
    }

    @Test
    void testGetMessagesByRoomIdSortedByTimestamp() throws InterruptedException {
        Message message1 = new Message(roomId, senderId, "First");
        Thread.sleep(10); // biar beda timestamp
        Message message2 = new Message(roomId, senderId, "Second");

        messageRepository.createMessage(message1);
        messageRepository.createMessage(message2);

        List<Message> messages = messageRepository.getMessagesByRoomId(roomId);

        assertEquals(2, messages.size());
        assertEquals("First", messages.get(0).getContent());
        assertEquals("Second", messages.get(1).getContent());
    }
}