package id.ac.ui.cs.advprog.papikosbe.repository.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository repository;

    private UUID dummyUserId;
    private Notification sampleNotification1;
    private Notification sampleNotification2;

    @BeforeEach
    void setUp() {
        dummyUserId = UUID.randomUUID();

        sampleNotification1 = Notification.builder()
                .id(UUID.randomUUID())
                .userId(dummyUserId)
                .title("Welcome")
                .message("Thanks for joining!")
                .createdAt(LocalDateTime.now())
                .type(NotificationType.OTHER)
                .isRead(false)
                .build();

        sampleNotification2 = Notification.builder()
                .id(UUID.randomUUID())
                .userId(dummyUserId)
                .title("Reminder")
                .message("Don't forget to check the system.")
                .createdAt(LocalDateTime.now().plusDays(1))
                .type(NotificationType.OTHER)
                .isRead(true)
                .build();
    }

    @Test
    void testSaveAndFindById() {
        repository.save(sampleNotification1);
        Optional<Notification> retrieved = repository.findById(sampleNotification1.getId());

        assertTrue(retrieved.isPresent());
        assertEquals(sampleNotification1.getId(), retrieved.get().getId());
        assertEquals("Welcome", retrieved.get().getTitle());
        assertEquals("Thanks for joining!", retrieved.get().getMessage());
    }

    @Test
    void testFindByIdReturnsEmptyIfNotFound() {
        Optional<Notification> retrieved = repository.findById(UUID.randomUUID());
        assertFalse(retrieved.isPresent());
    }

    @Test
    void testFindByUserId() {
        repository.save(sampleNotification1);
        repository.save(sampleNotification2);

        List<Notification> notifications = repository.findByUserId(dummyUserId);

        assertEquals(2, notifications.size());
        notifications.forEach(notification -> assertEquals(dummyUserId, notification.getUserId()));
    }

    @Test
    void testDeleteById() {
        repository.save(sampleNotification1);
        assertTrue(repository.findById(sampleNotification1.getId()).isPresent());

        repository.deleteById(sampleNotification1.getId());
        assertFalse(repository.findById(sampleNotification1.getId()).isPresent());
    }

    @Test
    void testFindAllNotifications() {
        repository.save(sampleNotification1);
        repository.save(sampleNotification2);

        List<Notification> allNotifications = repository.findAll();

        assertEquals(2, allNotifications.size());
        assertTrue(allNotifications.contains(sampleNotification1));
        assertTrue(allNotifications.contains(sampleNotification2));
    }
}
