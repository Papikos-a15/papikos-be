package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.RoomChat;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class RoomChatRepository {

    private List<RoomChat> roomChatData = new ArrayList<>();

    public void createRoomChat(RoomChat roomChat) {
    }

    public RoomChat getRoomChatById(UUID id) {
        return null;
    }

    public List<RoomChat> getRoomChatsByUser(UUID userId) {
        return new ArrayList<>();
    }
}