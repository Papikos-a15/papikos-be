package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.repository.RoomChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoomChatServiceImpl implements RoomChatService {

    private final RoomChatRepository roomChatRepository;

    public RoomChatServiceImpl(RoomChatRepository roomChatRepository) {
        this.roomChatRepository = roomChatRepository;
    }

    @Override
    public boolean createRoomChatIfNotExists(RoomChat roomChat) {
        return false;
    }

    @Override
    public RoomChat getRoomChatById(UUID id) {
        return null;
    }

    @Override
    public List<RoomChat> getRoomChatsByUser(UUID userId) {
        return null;
    }
}