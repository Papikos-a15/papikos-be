package id.ac.ui.cs.advprog.papikosbe.controller.chat;

import id.ac.ui.cs.advprog.papikosbe.controller.chat.dto.MessageResponse;
import id.ac.ui.cs.advprog.papikosbe.controller.chat.dto.SendMessageRequest;
import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.service.chat.MessageService;
import id.ac.ui.cs.advprog.papikosbe.service.user.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class ChatStompController {

    private final MessageService messageService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatStompController(MessageService messageService, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void handleSendMessage(SendMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        Message message = Message.builder()
                .id(UUID.randomUUID())
                .senderId(request.getSenderId())
                .content(request.getContent())
                .timestamp(LocalDateTime.now())
                .isEdited(false)
                .sendType(request.getSendType())
                .build();

        if (request.getSendType() == SendType.TO_ONE) {
            message.setRoomChat(RoomChat.builder().id(request.getRoomChatId()).build());
        }

        messageService.saveMessage(message);

        MessageResponse response = new MessageResponse(
                message.getId(),
                message.getRoomChat() != null ? message.getRoomChat().getId() : null,
                message.getSenderId(),
                userService.getEmailById(message.getSenderId()),
                message.getContent(),
                message.getTimestamp().toString(),
                message.isEdited(),
                message.getSendType()
        );

        String destination = "/queue/room." + response.getRoomChatId();
        messagingTemplate.convertAndSend(destination, response);
    }
}