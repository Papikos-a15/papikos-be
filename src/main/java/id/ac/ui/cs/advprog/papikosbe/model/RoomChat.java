package id.ac.ui.cs.advprog.papikosbe.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RoomChat {

    private String id;
    private String penyewaId;
    private String pemilikKosId;
    private LocalDateTime createdAt;

    public RoomChat(String penyewaId, String pemilikKosId) {
    }

    public RoomChat() {}
}