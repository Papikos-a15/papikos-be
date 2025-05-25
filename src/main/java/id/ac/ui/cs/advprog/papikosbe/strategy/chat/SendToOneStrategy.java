package id.ac.ui.cs.advprog.papikosbe.strategy.chat;

import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.MessageRepository;
import org.springframework.stereotype.Component;

@Component
public class SendToOneStrategy implements SendStrategy {

    private final MessageRepository messageRepository;

    public SendToOneStrategy(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void send(Message message) {
        messageRepository.save(message);
    }

    @Override
    public SendType getType() {
        return SendType.TO_ONE;
    }
}
