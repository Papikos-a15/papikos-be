package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Message;
import id.ac.ui.cs.advprog.papikosbe.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void saveMessage(Message message) {
        messageRepository.createMessage(message);
    }

    @Override
    public void editMessage(Message message) {
        messageRepository.editMessage(message);
    }

    @Override
    public boolean deleteMessage(UUID id) {
        return messageRepository.deleteMessage(id);
    }

    @Override
    public List<Message> getMessagesByRoomId(UUID roomId) {
        return messageRepository.getMessagesByRoomId(roomId);
    }
}