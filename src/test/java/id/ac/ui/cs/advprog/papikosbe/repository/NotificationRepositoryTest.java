package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import id.ac.ui.cs.advprog.papikosbe.repository.notification.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationRepositoryTest {

    private NotificationRepository repository;
    private Notification sampleNotif;

    @BeforeEach
    public void setUp() {
        repository = new NotificationRepository();
        sampleNotif = new Notification(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Welcome",
                "Thanks for joining!",
                LocalDateTime.now(),
                NotificationType.OTHER,
                false
        );
    }

    @Test
    public void testSaveNotification() {
        Notification result = repository.save(sampleNotif);
        assertEquals(sampleNotif, result);
    }

    @Test
    public void testFindById() {
        repository.save(sampleNotif);
        Notification found = repository.findById(sampleNotif.getId());
        assertNotNull(found);
        assertEquals(sampleNotif.getId(), found.getId());
    }

    @Test
    public void testFindAll() {
        repository.save(sampleNotif);
        List<Notification> all = repository.findAll();
        assertEquals(1, all.size());
        assertTrue(all.contains(sampleNotif));
    }

    @Test
    public void testDeleteById() {
        repository.save(sampleNotif);
        repository.deleteById(sampleNotif.getId());
        assertNull(repository.findById(sampleNotif.getId()));
    }

    @Test
    public void testFindByUserId() {
        UUID userId = sampleNotif.getUserId();
        repository.save(sampleNotif);
        List<Notification> userNotifs = repository.findByUserId(userId);
        assertEquals(1, userNotifs.size());
        assertEquals(userId, userNotifs.get(0).getUserId());
    }
}