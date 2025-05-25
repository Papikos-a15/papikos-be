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
        notification = Notification.builder()
                .id(UUID.randomUUID())
                .userId(dummyUserId)
                .title(dummyTitle)
                .message(dummyMessage)
                .createdAt(createdAt)
                .type(NotificationType.SYSTEM)
                .isRead(false)
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

    @Test
    public void testNotificationTitleCannotBeNull() {
        notification.setTitle(null);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notification.validate();
        });
        assertEquals("Title cannot be null or empty", thrown.getMessage());
    }

    @Test
    public void testNotificationMessageCannotBeNull() {
        notification.setMessage(null);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notification.validate();
        });
        assertEquals("Message cannot be null or empty", thrown.getMessage());
    }

    @Test
    public void testNotificationTitleCannotBeEmpty() {
        notification.setTitle(" ");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notification.validate();
        });
        assertEquals("Title cannot be null or empty", thrown.getMessage());
    }

    @Test
    public void testNotificationMessageCannotBeEmpty() {
        notification.setMessage(" ");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notification.validate();
        });
        assertEquals("Message cannot be null or empty", thrown.getMessage());
    }

    @Test
    public void testDeprecatedConstructor() {
        LocalDateTime customCreatedAt = LocalDateTime.now().minusDays(1);
        Notification deprecatedNotification = new Notification(UUID.randomUUID(), dummyUserId, "Old Notification", "Old Message", customCreatedAt, NotificationType.SYSTEM, false);
        assertNotNull(deprecatedNotification.getId());
        assertEquals(dummyUserId, deprecatedNotification.getUserId());
        assertEquals("Old Notification", deprecatedNotification.getTitle());
        assertEquals("Old Message", deprecatedNotification.getMessage());
        assertEquals(customCreatedAt, deprecatedNotification.getCreatedAt());
        assertEquals(NotificationType.SYSTEM, deprecatedNotification.getType());
        assertFalse(deprecatedNotification.isRead());
    }

    @Test
    public void testNotificationWithNonNullCreatedAt() {
        notification = Notification.builder()
                .id(UUID.randomUUID())
                .userId(dummyUserId)
                .title(dummyTitle)
                .message(dummyMessage)
                .createdAt(createdAt)  // Provided createdAt
                .type(NotificationType.SYSTEM)
                .isRead(false)
                .build();

        assertEquals(createdAt, notification.getCreatedAt(), "The createdAt should be the same as the provided value.");
    }

    @Test
    public void testNotificationWithNullCreatedAt() {
        notification = Notification.builder()
                .id(UUID.randomUUID())
                .userId(dummyUserId)
                .title(dummyTitle)
                .message(dummyMessage)
                .createdAt(null)  // Null createdAt
                .type(NotificationType.SYSTEM)
                .isRead(false)
                .build();

        assertNotNull(notification.getCreatedAt(), "The createdAt should not be null.");
        assertTrue(notification.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)),
                "The createdAt should be close to the current time.");
    }

}
