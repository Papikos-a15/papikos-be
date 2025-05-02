package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.RoomChat;

import java.util.List;
import java.util.UUID;

public interface RoomChatService {
    boolean createRoomChatIfNotExists(RoomChat roomChat);
    RoomChat getRoomChatById(UUID id);
    List<RoomChat> getRoomChatsByUser(UUID userId);
}