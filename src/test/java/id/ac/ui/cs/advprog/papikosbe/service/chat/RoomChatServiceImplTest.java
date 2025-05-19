package id.ac.ui.cs.advprog.papikosbe.service.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.RoomChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoomChatServiceImplTest {

    private RoomChatRepository roomChatRepository;
    private RoomChatService roomChatService;

    private UUID penyewaId;
    private UUID pemilikKosId;

    @BeforeEach
    void setUp() {
        roomChatRepository = mock(RoomChatRepository.class);
        roomChatService = new RoomChatServiceImpl(roomChatRepository);

        penyewaId = UUID.randomUUID();
        pemilikKosId = UUID.randomUUID();
    }

    @Test
    void testCreateRoomChatIfNotExists_ShouldCreateIfNotExist() {
        RoomChat room = new RoomChat(penyewaId, pemilikKosId);

        when(roomChatRepository.findAllByPenyewaId(penyewaId))
                .thenReturn(List.of()); // belum ada room

        boolean result = roomChatService.createRoomChatIfNotExists(room);

        assertTrue(result);
        verify(roomChatRepository, times(1)).save(room);
    }

    @Test
    void testCreateRoomChatIfNotExists_ShouldNotCreateIfAlreadyExists() {
        RoomChat existing = new RoomChat(penyewaId, pemilikKosId);
        RoomChat newRequest = new RoomChat(penyewaId, pemilikKosId);

        when(roomChatRepository.findAllByPenyewaId(penyewaId))
                .thenReturn(List.of(existing));

        boolean result = roomChatService.createRoomChatIfNotExists(newRequest);

        assertFalse(result);
        verify(roomChatRepository, never()).save(any());
    }

    @Test
    void testGetRoomChatById_shouldReturnRoom() {
        UUID roomId = UUID.randomUUID();
        RoomChat roomChat = new RoomChat();
        roomChat.setId(roomId);

        when(roomChatRepository.findById(roomId)).thenReturn(Optional.of(roomChat));

        RoomChat result = roomChatService.getRoomChatById(roomId);

        assertEquals(roomId, result.getId());
    }

    @Test
    void testGetRoomChatById_shouldThrowExceptionIfNotFound() {
        UUID roomId = UUID.randomUUID();

        when(roomChatRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            roomChatService.getRoomChatById(roomId);
        });
    }

    @Test
    void testGetRoomChatsByUser_shouldReturnMergedList() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        // Simulasi RoomChat sebagai penyewa
        RoomChat penyewaRoom = new RoomChat();
        penyewaRoom.setId(UUID.randomUUID());
        penyewaRoom.setPenyewaId(userId);
        penyewaRoom.setPemilikKosId(otherUserId);

        // Simulasi RoomChat sebagai pemilik
        RoomChat pemilikRoom = new RoomChat();
        pemilikRoom.setId(UUID.randomUUID());
        pemilikRoom.setPenyewaId(otherUserId);
        pemilikRoom.setPemilikKosId(userId);

        // Mock hasil query repository
        when(roomChatRepository.findAllByPenyewaId(userId)).thenReturn(List.of(penyewaRoom));
        when(roomChatRepository.findAllByPemilikKosId(userId)).thenReturn(List.of(pemilikRoom));

        List<RoomChat> result = roomChatService.getRoomChatsByUser(userId);

        assertEquals(2, result.size());
        assertTrue(result.contains(penyewaRoom));
        assertTrue(result.contains(pemilikRoom));
    }

}
