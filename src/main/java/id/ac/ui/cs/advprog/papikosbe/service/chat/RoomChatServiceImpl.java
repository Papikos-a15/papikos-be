package id.ac.ui.cs.advprog.papikosbe.service.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import id.ac.ui.cs.advprog.papikosbe.repository.chat.RoomChatRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoomChatServiceImpl implements RoomChatService {

    private final RoomChatRepository roomChatRepository;

    public RoomChatServiceImpl(RoomChatRepository roomChatRepository) {
        this.roomChatRepository = roomChatRepository;
    }

    @Override
    public boolean createRoomChatIfNotExists(RoomChat roomChat) {
        List<RoomChat> existing = roomChatRepository.findAllByPenyewaId(roomChat.getPenyewaId());

        boolean alreadyExists = existing.stream().anyMatch(room ->
                room.getPemilikKosId().equals(roomChat.getPemilikKosId())
        );

        if (alreadyExists) return false;

        roomChatRepository.save(roomChat);
        return true;
    }

    @Override
    public RoomChat getRoomChatById(UUID id) {
        return roomChatRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Room not found"));
    }

    @Override
    public List<RoomChat> getRoomChatsByUser(UUID userId) {
        List<RoomChat> asPenyewa = roomChatRepository.findAllByPenyewaId(userId);
        List<RoomChat> asPemilik = roomChatRepository.findAllByPemilikKosId(userId);

        Set<RoomChat> merged = new HashSet<>();
        merged.addAll(asPenyewa);
        merged.addAll(asPemilik);

        return new ArrayList<>(merged);
    }
}