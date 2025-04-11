package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.Message;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MessageRepository {

    private final Map<UUID, Message> messageStore = new HashMap<>();

    public void createMessage(Message message) {
        messageStore.put(message.getId(), message);
    }

    public Message getMessageById(UUID id) {
        return messageStore.get(id);
    }

    public void editMessage(Message updatedMessage) {
        messageStore.put(updatedMessage.getId(), updatedMessage);
    }

    public boolean deleteMessage(UUID id) {
        return messageStore.remove(id) != null;
    }

    public List<Message> getMessagesByRoomId(UUID roomId) {
        return messageStore.values().stream()
                .filter(message -> message.getRoomChatId().equals(roomId))
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }
}