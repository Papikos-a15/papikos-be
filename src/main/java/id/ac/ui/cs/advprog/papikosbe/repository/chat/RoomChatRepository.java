package id.ac.ui.cs.advprog.papikosbe.repository.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomChatRepository extends JpaRepository<RoomChat, UUID> {
    List<RoomChat> findAllByPenyewaId(UUID penyewaId);
    List<RoomChat> findAllByPemilikKosId(UUID pemilikKosId);
}
