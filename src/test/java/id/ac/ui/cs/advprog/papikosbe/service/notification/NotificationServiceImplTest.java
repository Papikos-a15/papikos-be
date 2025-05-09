package id.ac.ui.cs.advprog.papikosbe.service.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServiceImplTest {

    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        notificationService = mock(NotificationServiceImpl.class);
    }

    @Test
    void testCreateNotification() {
        UUID userId = UUID.randomUUID();
        String title = "Welcome";
        String message = "Welcome to Papikos!";
        NotificationType type = NotificationType.SYSTEM;

        Notification dummyNotification = new Notification(
                UUID.randomUUID(),
                userId,
                title,
                message,
                LocalDateTime.now(),
                type,
                false
        );

        when(notificationService.createNotification(userId, title, message, type)).thenReturn(dummyNotification);

        Notification result = notificationService.createNotification(userId, title, message, type);

        assertNotNull(result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(title, result.getTitle());
        assertEquals(message, result.getMessage());
        assertEquals(type, result.getType());
        assertFalse(result.isRead());
    }

    @Test
    void testGetAllNotificationsForUser() {
        UUID userId = UUID.randomUUID();
        Notification notif1 = new Notification(UUID.randomUUID(), userId, "Title 1", "Message 1", LocalDateTime.now(), NotificationType.OTHER, false);
        Notification notif2 = new Notification(UUID.randomUUID(), userId, "Title 2", "Message 2", LocalDateTime.now(), NotificationType.SYSTEM, true);

        when(notificationService.getNotificationsForUser(userId)).thenReturn(List.of(notif1, notif2));

        List<Notification> result = notificationService.getNotificationsForUser(userId);

        assertEquals(2, result.size());
        assertTrue(result.contains(notif1));
        assertTrue(result.contains(notif2));
    }

    @Test
    void testMarkAsRead() {
        UUID notificationId = UUID.randomUUID();
        doNothing().when(notificationService).markAsRead(notificationId);

        assertDoesNotThrow(() -> notificationService.markAsRead(notificationId));
        verify(notificationService, times(1)).markAsRead(notificationId);
    }

    @Test
    void testDeleteNotification() {
        UUID notificationId = UUID.randomUUID();
        doNothing().when(notificationService).deleteNotification(notificationId);

        assertDoesNotThrow(() -> notificationService.deleteNotification(notificationId));
        verify(notificationService, times(1)).deleteNotification(notificationId);
    }
}
