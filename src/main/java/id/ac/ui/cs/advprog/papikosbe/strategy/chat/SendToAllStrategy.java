package id.ac.ui.cs.advprog.papikosbe.strategy.chat;

import id.ac.ui.cs.advprog.papikosbe.enums.SendType;
import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.MessageRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.RoomChatRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SendToAllStrategy implements SendStrategy {

    private final MessageRepository messageRepository;
    private final RoomChatRepository roomChatRepository;

    public SendToAllStrategy(MessageRepository messageRepository, RoomChatRepository roomChatRepository) {
        this.messageRepository = messageRepository;
        this.roomChatRepository = roomChatRepository;
    }

    @Async("chatTaskExecutor")
    @Override
    public void send(Message message) {
        System.out.println("[Async Check] Running in thread: " + Thread.currentThread().getName());
        UUID pemilikId = message.getSenderId();
        List<RoomChat> rooms = roomChatRepository.findAllByPemilikKosId(pemilikId);

        for (RoomChat room : rooms) {
            Message broadcast = new Message(
                    room,
                    pemilikId,
                    message.getContent(),
                    SendType.TO_ALL
            );
            messageRepository.save(broadcast);
        }
    }

    @Override
    public SendType getType() {
        return SendType.TO_ALL;
    }
}