package id.ac.ui.cs.advprog.papikosbe.model.chat;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    void testMessageConstructorShouldInitializeCorrectly() {
        UUID roomChatId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String content = "Halo, ini pesan pertama.";

        Message message = new Message(roomChatId, senderId, content);

        assertNotNull(message.getId());
        assertEquals(roomChatId, message.getRoomChatId());
        assertEquals(senderId, message.getSenderId());
        assertEquals(content, message.getContent());
        assertNotNull(message.getTimestamp());
        assertFalse(message.isEdited());
    }

    @Test
    void testSetAndGetMethods() {
        Message message = new Message(UUID.randomUUID(), UUID.randomUUID(), "Initial");

        message.setContent("Pesan diedit");
        message.setEdited(true);

        assertEquals("Pesan diedit", message.getContent());
        assertTrue(message.isEdited());
    }
}