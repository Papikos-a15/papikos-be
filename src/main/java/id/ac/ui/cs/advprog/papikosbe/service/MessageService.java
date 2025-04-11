package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Message;

import java.util.UUID;

public interface MessageService {
    void saveMessage(Message message);
    boolean editMessage(Message message);
    boolean deleteMessage(UUID id);
    Message[] getMessagesByRoomId(UUID roomId);
}