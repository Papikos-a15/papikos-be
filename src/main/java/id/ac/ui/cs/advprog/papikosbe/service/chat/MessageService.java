package id.ac.ui.cs.advprog.papikosbe.service.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void saveMessage(Message message);
    void editMessage(Message message);
    boolean deleteMessage(UUID id);
    List<Message> getMessagesByRoomId(UUID roomId);
    Message getMessageById(UUID id);
}