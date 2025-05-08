package id.ac.ui.cs.advprog.papikosbe.controller.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.service.chat.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<Void> sendMessage(@RequestBody Message message) {
        messageService.saveMessage(message);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> editMessage(@PathVariable UUID id, @RequestBody String newContent) {
        Message found = messageService.getMessageById(id);
        if (found == null) {
            return ResponseEntity.notFound().build();
        }

        found.setContent(newContent);
        found.setEdited(true);
        messageService.editMessage(found);
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteMessage(@PathVariable UUID id) {
        boolean deleted = messageService.deleteMessage(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping
    public ResponseEntity<List<Message>> getMessagesByRoom(@RequestParam UUID roomId) {
        List<Message> messages = messageService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }
}
