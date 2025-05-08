package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.RoomChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RoomChatRepositoryTest {

    private RoomChatRepository repository;
    private RoomChat room1;
    private RoomChat room2;

    @BeforeEach
    public void setUp() {
        repository = new RoomChatRepository();

        room1 = new RoomChat(UUID.randomUUID(), UUID.randomUUID());
        room2 = new RoomChat(UUID.randomUUID(), UUID.randomUUID());

        repository.createRoomChat(room1);
        repository.createRoomChat(room2);
    }

    @Test
    public void testCreateRoomChat() {
        RoomChat newRoom = new RoomChat(UUID.randomUUID(), UUID.randomUUID());
        repository.createRoomChat(newRoom);

        RoomChat fetched = repository.getRoomChatById(newRoom.getId());
        assertNotNull(fetched);
        assertEquals(newRoom.getId(), fetched.getId());
        assertEquals(newRoom.getPenyewaId(), fetched.getPenyewaId());
    }

    @Test
    public void testGetRoomChatById() {
        RoomChat found = repository.getRoomChatById(room1.getId());
        assertNotNull(found);
        assertEquals(room1.getId(), found.getId());
    }

    @Test
    public void testGetRoomChatById_NotFound() {
        RoomChat found = repository.getRoomChatById(UUID.randomUUID());
        assertNull(found);
    }

    @Test
    public void testGetRoomChatsByUser() {
        List<RoomChat> chatsByPenyewa = repository.getRoomChatsByUser(room1.getPenyewaId());
        assertTrue(chatsByPenyewa.stream().anyMatch(r -> r.getId().equals(room1.getId())));

        List<RoomChat> chatsByPemilik = repository.getRoomChatsByUser(room1.getPemilikKosId());
        assertTrue(chatsByPemilik.stream().anyMatch(r -> r.getId().equals(room1.getId())));
    }
}