package id.ac.ui.cs.advprog.papikosbe.repository.chat;

import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RoomChatRepository roomChatRepository;

    private RoomChat room;
    private UUID senderId;

    @BeforeEach
    void setUp() {
        senderId = UUID.randomUUID();
        room = new RoomChat(UUID.randomUUID(), UUID.randomUUID());
        roomChatRepository.save(room);
    }

    @Test
    void testCreateAndFindById() {
        Message message = new Message(room, senderId, "Hello!", SendType.TO_ONE);
        messageRepository.save(message);

        Optional<Message> found = messageRepository.findById(message.getId());

        assertTrue(found.isPresent());
        assertEquals("Hello!", found.get().getContent());
        assertEquals(room.getId(), found.get().getRoomChat().getId());
        assertEquals(SendType.TO_ONE, found.get().getSendType());
    }

    @Test
    void testUpdateMessage() {
        Message message = new Message(room, senderId, "Original Message", SendType.TO_ONE);
        messageRepository.save(message);

        message.setContent("Updated Message");
        message.setEdited(true);
        messageRepository.save(message);

        Message updated = messageRepository.findById(message.getId()).orElseThrow();
        assertEquals("Updated Message", updated.getContent());
        assertTrue(updated.isEdited());
    }

    @Test
    void testDeleteMessage() {
        Message message = new Message(room, senderId, "To be deleted", SendType.TO_ONE);
        messageRepository.save(message);

        UUID messageId = message.getId();
        messageRepository.deleteById(messageId);

        assertFalse(messageRepository.findById(messageId).isPresent());
    }

    @Test
    void testFindByRoomChatOrderByTimestampAsc() throws InterruptedException {
        Message message1 = new Message(room, senderId, "First", SendType.TO_ONE);
        Thread.sleep(10);
        Message message2 = new Message(room, senderId, "Second", SendType.TO_ONE);

        messageRepository.saveAll(List.of(message1, message2));

        List<Message> result = messageRepository.findByRoomChatOrderByTimestampAsc(room);

        assertEquals(2, result.size());
        assertEquals("First", result.get(0).getContent());
        assertEquals("Second", result.get(1).getContent());
    }
}