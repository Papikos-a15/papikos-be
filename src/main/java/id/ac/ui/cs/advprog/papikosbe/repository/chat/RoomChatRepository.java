package id.ac.ui.cs.advprog.papikosbe.repository.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class RoomChatRepository {

    private List<RoomChat> roomChatData = new ArrayList<>();

    public void createRoomChat(RoomChat roomChat) {
        roomChatData.add(roomChat);
    }

    public RoomChat getRoomChatById(UUID id) {
        for (RoomChat chat : roomChatData) {
            if (chat.getId().equals(id)) {
                return chat;
            }
        }
        return null;
    }

    public List<RoomChat> getRoomChatsByUser(UUID userId) {
        List<RoomChat> result = new ArrayList<>();
        for (RoomChat chat : roomChatData) {
            if (chat.getPenyewaId().equals(userId) || chat.getPemilikKosId().equals(userId)) {
                result.add(chat);
            }
        }
        return result;
    }
}