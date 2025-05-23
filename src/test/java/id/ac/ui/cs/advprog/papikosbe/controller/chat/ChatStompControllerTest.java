package id.ac.ui.cs.advprog.papikosbe.controller.chat;

import id.ac.ui.cs.advprog.papikosbe.controller.chat.dto.MessageResponse;
import id.ac.ui.cs.advprog.papikosbe.controller.chat.dto.SendMessageRequest;
import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.service.chat.MessageService;
import id.ac.ui.cs.advprog.papikosbe.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ChatStompControllerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private UserService userService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private ChatStompController chatStompController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chatStompController = new ChatStompController(messageService, userService, messagingTemplate);
    }

    @Test
    void testHandleSendMessage_ToOne_shouldSendToCorrectQueue() {
        UUID senderId = UUID.randomUUID();
        UUID roomChatId = UUID.randomUUID();

        SendMessageRequest request = new SendMessageRequest();
        request.setSenderId(senderId);
        request.setContent("Test message to one");
        request.setSendType(SendType.TO_ONE);
        request.setRoomChatId(roomChatId);

        when(userService.getEmailById(senderId)).thenReturn("user@example.com");

        chatStompController.handleSendMessage(request, SimpMessageHeaderAccessor.create());

        verify(messageService).saveMessage(any(Message.class));
        verify(messagingTemplate).convertAndSend(eq("/queue/room." + roomChatId), any(MessageResponse.class));
    }

    @Test
    void testHandleSendMessage_ToAll_shouldSendToRoomNullQueue() {
        UUID senderId = UUID.randomUUID();

        SendMessageRequest request = new SendMessageRequest();
        request.setSenderId(senderId);
        request.setContent("Broadcast message");
        request.setSendType(SendType.TO_ALL);

        when(userService.getEmailById(senderId)).thenReturn("owner@example.com");

        chatStompController.handleSendMessage(request, SimpMessageHeaderAccessor.create());

        verify(messageService).saveMessage(any(Message.class));
        verify(messagingTemplate).convertAndSend(eq("/queue/room.null"), any(MessageResponse.class));
    }
}
