package id.ac.ui.cs.advprog.papikosbe.model.chat;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RoomChat {

    private UUID id;
    private UUID penyewaId;
    private UUID pemilikKosId;
    private LocalDateTime createdAt;

    public RoomChat(UUID penyewaId, UUID pemilikKosId) {
        this.id = UUID.randomUUID();
        this.penyewaId = penyewaId;
        this.pemilikKosId = pemilikKosId;
        this.createdAt = LocalDateTime.now();
    }
}