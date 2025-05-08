package id.ac.ui.cs.advprog.papikosbe.controller.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
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
                    "content": "Halo!"
                }
                """;

        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    void testDeleteMessage_shouldReturnOk() throws Exception {
        UUID messageId = UUID.randomUUID();
        when(messageService.deleteMessage(messageId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/messages/" + messageId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMessages_shouldReturnList() throws Exception {
        UUID roomId = UUID.randomUUID();
        when(messageService.getMessagesByRoomId(roomId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/messages")
                        .param("roomId", roomId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testEditMessage_shouldReturnOk() throws Exception {
        UUID messageId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();

        Message existingMessage = new Message(roomId, senderId, "Pesan lama");
        existingMessage.setId(messageId);

        when(messageService.getMessageById(messageId)).thenReturn(existingMessage);

        String newContent = "Pesan baru";
        String requestBody = "\"" + newContent + "\"";

        mockMvc.perform(put("/api/v1/messages/" + messageId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(messageService, times(1)).editMessage(any(Message.class));
    }
}