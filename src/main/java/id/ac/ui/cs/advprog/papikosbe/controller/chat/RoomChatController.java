package id.ac.ui.cs.advprog.papikosbe.controller.chat;

import id.ac.ui.cs.advprog.papikosbe.controller.chat.dto.RoomChatRequest;
import id.ac.ui.cs.advprog.papikosbe.controller.chat.dto.RoomChatResponse;
import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.service.chat.RoomChatService;
import id.ac.ui.cs.advprog.papikosbe.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roomchats")
public class RoomChatController {

    private final RoomChatService roomChatService;
    private final UserService userService;

    public RoomChatController(RoomChatService roomChatService, UserService userService) {
        this.roomChatService = roomChatService;
        this.userService = userService;
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomChat> getRoomChatById(@PathVariable UUID roomId) {
        RoomChat roomChat = roomChatService.getRoomChatById(roomId);
        return ResponseEntity.ok(roomChat);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoomChatResponse>> getRoomChatsByUser(@PathVariable UUID userId) {
        List<RoomChat> roomChats = roomChatService.getRoomChatsByUser(userId);

        List<RoomChatResponse> result = roomChats.stream().map(room -> {
            UUID lawanId = room.getPenyewaId().equals(userId)
                    ? room.getPemilikKosId()
                    : room.getPenyewaId();

            String email = userService.getEmailById(lawanId);

            return new RoomChatResponse(
                    room.getId(),
                    lawanId,
                    email,
                    room.getCreatedAt()
            );
        }).toList();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<UUID> createOrFindRoomChat(@RequestBody RoomChatRequest request) {
        RoomChat result = roomChatService.findOrCreateRoomChat(request.getPenyewaId(), request.getPemilikKosId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result.getId());
    }
}
