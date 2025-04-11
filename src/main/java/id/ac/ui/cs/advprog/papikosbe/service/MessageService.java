package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void saveMessage(Message message);
    void editMessage(Message message);
    boolean deleteMessage(UUID id);
    List<Message> getMessagesByRoomId(UUID roomId);
}