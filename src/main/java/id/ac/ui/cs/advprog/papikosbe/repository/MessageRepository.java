package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.Message;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MessageRepository {

    private final Map<UUID, Message> messageStore = new HashMap<>();

    public void createMessage(Message message) {
    }

    public Message getMessageById(UUID id) {
        return null;
    }

    public void editMessage(Message updatedMessage) {
    }

    public boolean deleteMessage(UUID id) {
        return false;
    }

    public List<Message> getMessagesByRoomId(UUID roomId) {
    }
}