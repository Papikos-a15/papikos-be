package id.ac.ui.cs.advprog.papikosbe.service.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.RoomChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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
}
