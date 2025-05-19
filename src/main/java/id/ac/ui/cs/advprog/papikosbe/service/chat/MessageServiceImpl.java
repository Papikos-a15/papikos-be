package id.ac.ui.cs.advprog.papikosbe.service.chat;

import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.MessageRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.RoomChatRepository;
import id.ac.ui.cs.advprog.papikosbe.strategy.chat.SendStrategy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final RoomChatRepository roomChatRepository;
    private final Map<SendType, SendStrategy> strategies;

    public MessageServiceImpl(MessageRepository messageRepository,
                              RoomChatRepository roomChatRepository,
                              List<SendStrategy> strategyList) {
        this.messageRepository = messageRepository;
        this.roomChatRepository = roomChatRepository;
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(SendStrategy::getType, Function.identity()));
    }

    @Override
    public void saveMessage(Message message) {
        SendStrategy strategy = strategies.get(message.getSendType());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported send type: " + message.getSendType());
        }
        strategy.send(message);
    }

    @Override
    public void editMessage(Message message) {
        message.setEdited(true);
        messageRepository.save(message);
    }

    @Override
    public boolean deleteMessage(UUID id) {
        if (!messageRepository.existsById(id)) return false;
        messageRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Message> getMessagesByRoomId(UUID roomId) {
        RoomChat room = roomChatRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("Room not found"));
        return messageRepository.findByRoomChatOrderByTimestampAsc(room);
    }

    @Override
    public Message getMessageById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));
    }
}