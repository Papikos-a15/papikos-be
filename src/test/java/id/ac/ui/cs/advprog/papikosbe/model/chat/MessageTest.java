package id.ac.ui.cs.advprog.papikosbe.model.chat;

import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    void testMessageConstructorShouldInitializeCorrectly() {
        UUID penyewaId = UUID.randomUUID();
        UUID pemilikKosId = UUID.randomUUID();
        RoomChat roomChat = new RoomChat(penyewaId, pemilikKosId);
        UUID senderId = UUID.randomUUID();
        String content = "Halo, ini pesan pertama.";

        Message message = new Message(roomChat, senderId, content, SendType.TO_ONE);

        assertNotNull(message.getId());
        assertEquals(roomChat, message.getRoomChat());
        assertEquals(senderId, message.getSenderId());
        assertEquals(content, message.getContent());
        assertNotNull(message.getTimestamp());
        assertFalse(message.isEdited());
        assertEquals(SendType.TO_ONE, message.getSendType());
    }

    @Test
    void testSetAndGetMethods() {
        RoomChat roomChat = new RoomChat(UUID.randomUUID(), UUID.randomUUID());
        Message message = new Message(roomChat, UUID.randomUUID(), "Initial", SendType.TO_ALL);

        message.setContent("Pesan diedit");
        message.setEdited(true);
        message.setSendType(SendType.TO_ONE);

        assertEquals("Pesan diedit", message.getContent());
        assertTrue(message.isEdited());
        assertEquals(SendType.TO_ONE, message.getSendType());
    }

}
