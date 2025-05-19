package id.ac.ui.cs.advprog.papikosbe.controller.chat.dto;

import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private UUID roomChatId;
    private UUID senderId;
    private String senderEmail;
    private String content;
    private String timestamp;
    private boolean isEdited;
    private SendType sendType;
}