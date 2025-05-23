package id.ac.ui.cs.advprog.papikosbe.controller.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RoomChatRequest {
    private UUID penyewaId;
    private UUID pemilikKosId;
}
