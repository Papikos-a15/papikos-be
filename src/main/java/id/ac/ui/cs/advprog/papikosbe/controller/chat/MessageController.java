package id.ac.ui.cs.advprog.papikosbe.controller.chat;

import id.ac.ui.cs.advprog.papikosbe.controller.chat.dto.MessageResponse;
import id.ac.ui.cs.advprog.papikosbe.controller.chat.dto.SendMessageRequest;
import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.RoomChatRepository;
import id.ac.ui.cs.advprog.papikosbe.service.chat.MessageService;
import id.ac.ui.cs.advprog.papikosbe.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final RoomChatRepository roomChatRepository;
    private final UserService userService;

    public MessageController(MessageService messageService, RoomChatRepository roomChatRepository, UserService userService) {
        this.messageService = messageService;
        this.roomChatRepository = roomChatRepository;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Void> sendMessage(@RequestBody SendMessageRequest request) {
        RoomChat room = roomChatRepository.findById(request.getRoomChatId())
                .orElseThrow(() -> new IllegalArgumentException("RoomChat not found"));

        Message message = new Message(
                room,
                request.getSenderId(),
                request.getContent(),
                request.getSendType()
        );

        messageService.saveMessage(message);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> editMessage(@PathVariable UUID id, @RequestBody String newContent) {
        Message found = messageService.getMessageById(id);
        found.setContent(newContent);
        found.setEdited(true);
        messageService.editMessage(found);
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteMessage(@PathVariable UUID id) {
        boolean deleted = messageService.deleteMessage(id);
        return deleted ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getMessagesByRoom(@RequestParam UUID roomId) {
        List<Message> messages = messageService.getMessagesByRoomId(roomId);

        List<MessageResponse> response = messages.stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getRoomChat().getId(),
                        message.getSenderId(),
                        userService.getEmailById(message.getSenderId()),
                        message.getContent(),
                        message.getTimestamp().toString(),
                        message.isEdited(),
                        message.getSendType()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}