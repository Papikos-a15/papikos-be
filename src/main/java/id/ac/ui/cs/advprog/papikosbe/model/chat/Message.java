package id.ac.ui.cs.advprog.papikosbe.model.chat;

import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_chat_id", nullable = false)
    private RoomChat roomChat;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "is_edited", nullable = false)
    private boolean isEdited;

    @Enumerated(EnumType.STRING)
    @Column(name = "send_type", nullable = false)
    private SendType sendType;

    // Convenience constructor (non-JPA)
    public Message(RoomChat roomChat, UUID senderId, String content, SendType sendType) {
        this.id = UUID.randomUUID();
        this.roomChat = roomChat;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isEdited = false;
        this.sendType = sendType;
    }
}