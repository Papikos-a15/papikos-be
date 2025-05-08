package id.ac.ui.cs.advprog.papikosbe.model;


import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RoomChatTest {

    @Test
    public void testCreateRoomChat() {
        UUID pemilikKosId = UUID.randomUUID();
        UUID penyewaId = UUID.randomUUID();

        RoomChat roomChat = new RoomChat(penyewaId, pemilikKosId);

        assertEquals(penyewaId, roomChat.getPenyewaId());
        assertEquals(pemilikKosId, roomChat.getPemilikKosId());
        assertNotNull(roomChat.getCreatedAt());
    }

    @Test
    public void testRoomChatSetterGetter() {
        RoomChat roomChat = new RoomChat(UUID.randomUUID(), UUID.randomUUID());

        UUID id = UUID.randomUUID();
        UUID penyewaId = UUID.randomUUID();
        UUID pemilikKosId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        roomChat.setId(id);
        roomChat.setPenyewaId(penyewaId);
        roomChat.setPemilikKosId(pemilikKosId);
        roomChat.setCreatedAt(now);

        assertEquals(id, roomChat.getId());
        assertEquals(penyewaId, roomChat.getPenyewaId());
        assertEquals(pemilikKosId, roomChat.getPemilikKosId());
        assertEquals(now, roomChat.getCreatedAt());
    }
}
