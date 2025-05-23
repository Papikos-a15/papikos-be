package id.ac.ui.cs.advprog.papikosbe.controller.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.service.chat.RoomChatService;
import id.ac.ui.cs.advprog.papikosbe.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RoomChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoomChatService roomChatService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RoomChatController roomChatController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomChatController).build();
    }

    @Test
    void testGetRoomChatById_shouldReturnRoomChat() throws Exception {
        UUID roomId = UUID.randomUUID();
        RoomChat mockRoom = new RoomChat(UUID.randomUUID(), UUID.randomUUID());

        when(roomChatService.getRoomChatById(roomId)).thenReturn(mockRoom);

        mockMvc.perform(get("/api/roomchats/" + roomId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoomChatsByUser_shouldReturnList() throws Exception {
        UUID userId = UUID.randomUUID();
        when(roomChatService.getRoomChatsByUser(userId)).thenReturn(List.of());

        mockMvc.perform(get("/api/roomchats/user/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateOrFindRoomChat_shouldReturn201AndRoomId() throws Exception {
        UUID roomChatId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        when(roomChatService.findOrCreateRoomChat(any(), any()))
                .thenReturn(RoomChat.builder().id(roomChatId).build());

        String requestBody = """
            {
                "penyewaId": "00000000-0000-0000-0000-000000000001",
                "pemilikKosId": "00000000-0000-0000-0000-000000000002"
            }
            """;

        mockMvc.perform(post("/api/roomchats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json("\"" + roomChatId.toString() + "\"")); // karena response berupa JSON string literal
    }


    @Test
    void testCreateOrFindRoomChat_shouldReturnExistingRoomIdIfAlreadyExists() throws Exception {
        UUID existingRoomId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        when(roomChatService.findOrCreateRoomChat(any(), any()))
                .thenReturn(RoomChat.builder().id(existingRoomId).build());

        String requestBody = """
            {
                "penyewaId": "00000000-0000-0000-0000-000000000001",
                "pemilikKosId": "00000000-0000-0000-0000-000000000002"
            }
            """;

        mockMvc.perform(post("/api/roomchats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json("\"" + existingRoomId.toString() + "\""));
    }

    @Test
    void testGetRoomChatsByUser() throws Exception {
        UUID userId = UUID.randomUUID();
        RoomChat roomChat = new RoomChat();
        roomChat.setPenyewaId(userId);
        roomChat.setPemilikKosId(UUID.randomUUID());
        roomChat.setCreatedAt(LocalDateTime.now());
        roomChat.setId(UUID.randomUUID());

        // Mock repository dan service
        when(roomChatService.getRoomChatsByUser(userId)).thenReturn(List.of(roomChat));
        when(userService.getEmailById(any(UUID.class))).thenReturn("email@test.com");

        mockMvc.perform(get("/api/roomchats/user/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoomChatsByUser_whenUserIsPenyewa() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID pemilikId = UUID.randomUUID();

        RoomChat roomChat = new RoomChat();
        roomChat.setId(UUID.randomUUID());
        roomChat.setPenyewaId(userId); // sama dengan userId
        roomChat.setPemilikKosId(pemilikId);
        roomChat.setCreatedAt(LocalDateTime.now());

        when(roomChatService.getRoomChatsByUser(userId)).thenReturn(List.of(roomChat));
        when(userService.getEmailById(pemilikId)).thenReturn("pemilik@email.com");

        mockMvc.perform(get("/api/roomchats/user/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoomChatsByUser_whenUserIsPemilik() throws Exception {
        UUID penyewaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID(); // ini sebagai pemilik
        RoomChat roomChat = new RoomChat();
        roomChat.setId(UUID.randomUUID());
        roomChat.setPenyewaId(penyewaId); // beda dengan userId
        roomChat.setPemilikKosId(userId);
        roomChat.setCreatedAt(LocalDateTime.now());

        when(roomChatService.getRoomChatsByUser(userId)).thenReturn(List.of(roomChat));
        when(userService.getEmailById(penyewaId)).thenReturn("penyewa@email.com");

        mockMvc.perform(get("/api/roomchats/user/" + userId))
                .andExpect(status().isOk());
    }
}