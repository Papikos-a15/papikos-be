package id.ac.ui.cs.advprog.papikosbe.model;


import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RoomChatTest {

    @Test
    public void testCreateRoomChat() {
        String pemilikKosId = UUID.randomUUID().toString();
        String penyewaId = UUID.randomUUID().toString();

        RoomChat roomChat = new RoomChat(penyewaId, pemilikKosId);

        assertEquals(penyewaId, roomChat.getPenyewaId());
        assertEquals(pemilikKosId, roomChat.getPemilikKosId());
        assertNotNull(roomChat.getCreatedAt());
    }

    @Test
    public void testRoomChatSetterGetter() {
        RoomChat roomChat = new RoomChat();

        String id = UUID.randomUUID().toString();
        String penyewaId = UUID.randomUUID().toString();
        String pemilikKosId = UUID.randomUUID().toString();
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
