package id.ac.ui.cs.advprog.papikosbe.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Message {
    private UUID id;
    private UUID roomChatId;
    private UUID senderId;
    private String content;
    private LocalDateTime timestamp;
    private boolean isEdited;

    public Message(UUID roomChatId, UUID senderId, String content) {
        this.id = UUID.randomUUID();
        this.roomChatId = roomChatId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isEdited = false;
    }
}