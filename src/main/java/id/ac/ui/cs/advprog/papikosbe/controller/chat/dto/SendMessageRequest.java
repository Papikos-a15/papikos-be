package id.ac.ui.cs.advprog.papikosbe.controller.chat.dto;

import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SendMessageRequest {
    private UUID roomChatId;
    private UUID senderId;
    private String content;
    private SendType sendType;
}