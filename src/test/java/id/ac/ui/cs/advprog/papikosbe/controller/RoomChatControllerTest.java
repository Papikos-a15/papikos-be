package id.ac.ui.cs.advprog.papikosbe.controller;

import id.ac.ui.cs.advprog.papikosbe.model.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.service.RoomChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RoomChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoomChatService roomChatService;

    @InjectMocks
    private RoomChatController roomChatController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomChatController).build();
    }

    @Test
    void testGetRoomChatById_shouldReturnRoomChat() throws Exception {
        UUID roomId = UUID.randomUUID();
        RoomChat roomChat = new RoomChat(UUID.randomUUID(), UUID.randomUUID());
        when(roomChatService.getRoomChatById(roomId)).thenReturn(roomChat);

        mockMvc.perform(get("/api/v1/roomchats/" + roomId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoomChatsByUser_shouldReturnList() throws Exception {
        UUID userId = UUID.randomUUID();
        when(roomChatService.getRoomChatsByUser(userId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/roomchats/user/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateRoomChat_shouldReturnOk() throws Exception {
        String requestBody = """
                {
                    "penyewaId": "00000000-0000-0000-0000-000000000001",
                    "pemilikKosId": "00000000-0000-0000-0000-000000000002"
                }
                """;

        when(roomChatService.createRoomChatIfNotExists(any(RoomChat.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/roomchats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(roomChatService, times(1)).createRoomChatIfNotExists(any(RoomChat.class));
    }
}