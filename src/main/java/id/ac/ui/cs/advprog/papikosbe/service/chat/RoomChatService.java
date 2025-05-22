package id.ac.ui.cs.advprog.papikosbe.service.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;

import java.util.List;
import java.util.UUID;

public interface RoomChatService {

    /**
     * Membuat RoomChat baru jika belum ada relasi penyewa-pemilik sebelumnya.
     * @return true jika berhasil dibuat, false jika sudah ada
     */
    boolean createRoomChatIfNotExists(RoomChat roomChat);

    /**
     * Mengambil RoomChat berdasarkan ID-nya.
     */
    RoomChat getRoomChatById(UUID id);

    /**
     * Mengambil semua RoomChat yang melibatkan user (sebagai penyewa atau pemilik).
     */
    List<RoomChat> getRoomChatsByUser(UUID userId);
}
