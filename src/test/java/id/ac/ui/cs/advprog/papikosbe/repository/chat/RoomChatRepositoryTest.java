package id.ac.ui.cs.advprog.papikosbe.repository.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.RoomChat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RoomChatRepositoryTest {

    @Autowired
    private RoomChatRepository repository;

    @Test
    public void testFindAllByPenyewaId() {
        UUID penyewaId = UUID.randomUUID();
        repository.save(new RoomChat(penyewaId, UUID.randomUUID()));

        List<RoomChat> result = repository.findAllByPenyewaId(penyewaId);
        assertEquals(1, result.size());
        assertEquals(penyewaId, result.get(0).getPenyewaId());
    }

    @Test
    public void testFindAllByPemilikKosId() {
        UUID pemilikId = UUID.randomUUID();
        repository.save(new RoomChat(UUID.randomUUID(), pemilikId));

        List<RoomChat> result = repository.findAllByPemilikKosId(pemilikId);
        assertEquals(1, result.size());
        assertEquals(pemilikId, result.get(0).getPemilikKosId());
    }
}
