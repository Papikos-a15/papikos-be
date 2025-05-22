package id.ac.ui.cs.advprog.papikosbe.service.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    /**
     * Menyimpan pesan menggunakan strategi pengiriman (TO_ONE / TO_ALL).
     */
    void saveMessage(Message message);

    /**
     * Mengedit isi pesan dan menandai sebagai edited.
     */
    void editMessage(Message message);

    /**
     * Menghapus pesan berdasarkan ID.
     * @return true jika berhasil dihapus, false jika tidak ditemukan
     */
    boolean deleteMessage(UUID id);

    /**
     * Mengambil semua pesan untuk suatu RoomChat (terurut timestamp ASC).
     */
    List<Message> getMessagesByRoomId(UUID roomId);

    /**
     * Mengambil satu pesan berdasarkan ID-nya.
     */
    Message getMessageById(UUID id);
}