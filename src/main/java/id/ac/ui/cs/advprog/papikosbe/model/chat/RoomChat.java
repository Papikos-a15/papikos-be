package id.ac.ui.cs.advprog.papikosbe.model.chat;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "room_chats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomChat {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "penyewa_id", nullable = false, updatable = false)
    private UUID penyewaId;

    @Column(name = "pemilik_kos_id", nullable = false, updatable = false)
    private UUID pemilikKosId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RoomChat(UUID penyewaId, UUID pemilikKosId) {
        this.id = UUID.randomUUID();
        this.penyewaId = penyewaId;
        this.pemilikKosId = pemilikKosId;
        this.createdAt = LocalDateTime.now();
    }
}