package id.ac.ui.cs.advprog.papikosbe.controller.chat;

import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.RoomChatRepository;
import id.ac.ui.cs.advprog.papikosbe.service.chat.MessageService;
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
public class MessageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MessageService messageService;

    @Mock
    private RoomChatRepository roomChatRepository;

    @InjectMocks
    private MessageController messageController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
    }

    @Test
    void testSendMessage_shouldReturnCreated() throws Exception {
        String requestBody = """
                {
                    "roomChatId": "00000000-0000-0000-0000-000000000001",
                    "senderId": "00000000-0000-0000-0000-000000000002",
                    "content": "Halo!",
                    "sendType": "TO_ONE"
                }
                """;

        UUID roomChatId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        RoomChat mockRoom = new RoomChat(UUID.randomUUID(), UUID.randomUUID());

        when(roomChatRepository.findById(roomChatId)).thenReturn(Optional.of(mockRoom));

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(messageService).saveMessage(any(Message.class));
    }

    @Test
    void testEditMessage_shouldReturnOk() throws Exception {
        UUID messageId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        RoomChat room = new RoomChat(UUID.randomUUID(), UUID.randomUUID());
        Message message = new Message(room, senderId, "Old", SendType.TO_ONE);
        message.setId(messageId);

        when(messageService.getMessageById(messageId)).thenReturn(message);

        String requestBody = "\"Updated content\"";

        mockMvc.perform(put("/api/messages/" + messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(messageService).editMessage(any(Message.class));
    }

    @Test
    void testDeleteMessage_shouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(messageService.deleteMessage(id)).thenReturn(true);

        mockMvc.perform(delete("/api/messages/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteMessage_shouldReturnNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(messageService.deleteMessage(id)).thenReturn(false);

        mockMvc.perform(delete("/api/messages/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetMessagesByRoom_shouldReturnOk() throws Exception {
        UUID roomId = UUID.randomUUID();
        when(messageService.getMessagesByRoomId(roomId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/messages")
                        .param("roomId", roomId.toString()))
                .andExpect(status().isOk());
    }
}