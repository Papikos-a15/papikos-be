package id.ac.ui.cs.advprog.papikosbe.model.notification;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;

public class NotificationTest {

    private Notification notification;
    private UUID dummyUserId;
    private String dummyTitle;
    private String dummyMessage;
    private LocalDateTime createdAt;

    @BeforeEach
    public void setUp() {
        dummyUserId = UUID.randomUUID();
        dummyTitle = "Promo Kamar Kos!";
        dummyMessage = "Ada kamar kosong di kos yang kamu favoritkan!";
        createdAt = LocalDateTime.now();

        // Using the Builder pattern to create the Notification
        notification = new Notification.Builder(UUID.randomUUID(), dummyUserId)
                .setTitle(dummyTitle)
                .setMessage(dummyMessage)
                .setCreatedAt(createdAt)
                .setType(NotificationType.SYSTEM)
                .setIsRead(false)
                .build();
    }

    @Test
    public void testNotificationInitialization() {
        assertNotNull(notification.getId());
        assertEquals(dummyUserId, notification.getUserId());
        assertEquals(dummyTitle, notification.getTitle());
        assertEquals(dummyMessage, notification.getMessage());
        assertEquals(createdAt, notification.getCreatedAt());
        assertEquals(NotificationType.SYSTEM, notification.getType());
        assertFalse(notification.isRead());
    }

    @Test
    public void testSetNotificationAsRead() {
        notification.setRead(true);
        assertTrue(notification.isRead());
    }

    @Test
    public void testChangeTitle() {
        String newTitle = "Judul Baru";
        notification.setTitle(newTitle);
        assertEquals(newTitle, notification.getTitle());
    }

    @Test
    public void testChangeMessage() {
        String newMessage = "Isi pesan diubah";
        notification.setMessage(newMessage);
        assertEquals(newMessage, notification.getMessage());
    }
}
