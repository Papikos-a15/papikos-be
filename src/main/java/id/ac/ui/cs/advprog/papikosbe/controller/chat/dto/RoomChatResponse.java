package id.ac.ui.cs.advprog.papikosbe.controller.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RoomChatResponse {
    private UUID roomChatId;
    private UUID lawanUserId;
    private String lawanUserEmail;
    private LocalDateTime createdAt;
}