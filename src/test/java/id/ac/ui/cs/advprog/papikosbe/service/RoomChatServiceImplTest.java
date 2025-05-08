package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.RoomChatRepository;
import id.ac.ui.cs.advprog.papikosbe.service.chat.RoomChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoomChatServiceImplTest {

    RoomChatRepository roomChatRepository;
    RoomChatServiceImpl roomChatService;

    @BeforeEach
    void setUp() {
        roomChatRepository = mock(RoomChatRepository.class);
        roomChatService = new RoomChatServiceImpl(roomChatRepository);
    }

    @Test
    void testCreateRoomChatIfNotExistsShouldReturnTrue() {
        RoomChat chat = new RoomChat(UUID.randomUUID(), UUID.randomUUID());

        when(roomChatRepository.getRoomChatsByUser(any())).thenReturn(Collections.emptyList());

        boolean result = roomChatService.createRoomChatIfNotExists(chat);

        assertTrue(result);
        verify(roomChatRepository, times(1)).createRoomChat(chat);
    }

    @Test
    void testCreateRoomChatIfAlreadyExistsShouldReturnFalse() {
        UUID penyewaId = UUID.randomUUID();
        UUID pemilikKosId = UUID.randomUUID();

        RoomChat existing = new RoomChat(penyewaId, pemilikKosId);
        List<RoomChat> mockChats = List.of(existing);

        when(roomChatRepository.getRoomChatsByUser(penyewaId)).thenReturn(mockChats);

        RoomChat newAttempt = new RoomChat(penyewaId, pemilikKosId);
        boolean result = roomChatService.createRoomChatIfNotExists(newAttempt);

        assertFalse(result);
        verify(roomChatRepository, never()).createRoomChat(any());
    }
    @Test
    void testGetRoomChatById() {
        UUID id = UUID.randomUUID();
        RoomChat chat = new RoomChat(UUID.randomUUID(), UUID.randomUUID());
        chat.setId(id);

        when(roomChatRepository.getRoomChatById(id)).thenReturn(chat);

        RoomChat result = roomChatService.getRoomChatById(id);

        assertEquals(chat, result);
    }

    @Test
    void testGetRoomChatsByUser() {
        UUID userId = UUID.randomUUID();
        List<RoomChat> chats = List.of(new RoomChat(UUID.randomUUID(), UUID.randomUUID()));

        when(roomChatRepository.getRoomChatsByUser(userId)).thenReturn(chats);

        List<RoomChat> result = roomChatService.getRoomChatsByUser(userId);

        assertEquals(chats, result);
    }
}