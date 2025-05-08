package id.ac.ui.cs.advprog.papikosbe.controller.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.service.chat.RoomChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roomchats")
public class RoomChatController {

    private final RoomChatService roomChatService;

    public RoomChatController(RoomChatService roomChatService) {
        this.roomChatService = roomChatService;
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomChat> getRoomChatById(@PathVariable UUID roomId) {
        RoomChat roomChat = roomChatService.getRoomChatById(roomId);
        if (roomChat == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(roomChat);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoomChat>> getRoomChatsByUser(@PathVariable UUID userId) {
        List<RoomChat> roomChats = roomChatService.getRoomChatsByUser(userId);
        return ResponseEntity.ok(roomChats);
    }

    @PostMapping
    public ResponseEntity<Boolean> createRoomChat(@RequestBody RoomChat roomChat) {
        boolean created = roomChatService.createRoomChatIfNotExists(roomChat);
        return ResponseEntity.ok(created);
    }
}