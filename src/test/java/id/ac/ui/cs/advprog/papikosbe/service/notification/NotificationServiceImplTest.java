package id.ac.ui.cs.advprog.papikosbe.service.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import id.ac.ui.cs.advprog.papikosbe.repository.notification.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNotification_Success() {
        UUID userId = UUID.randomUUID();
        String title = "Test Title";
        String message = "Test Message";
        NotificationType type = NotificationType.SYSTEM;

        // We simulate repository.save() returning the same notification
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        Notification result = notificationService.createNotification(userId, title, message, type);

        // Basic assertions to check that the returned Notification is correct
        assertNotNull(result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(title, result.getTitle());
        assertEquals(message, result.getMessage());
        assertEquals(type, result.getType());
        assertFalse(result.isRead());
        assertNotNull(result.getCreatedAt());

        // Verify that repository.save was called
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }


    @Test
    void testGetNotificationsForUser_FiltersCorrectly() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        Notification userNotif = new Notification.Builder(UUID.randomUUID(), userId)
                .setTitle("Title 1")
                .setMessage("Message 1")
                .setType(NotificationType.SYSTEM)
                .setIsRead(false)
                .build();

        Notification otherNotif = new Notification.Builder(UUID.randomUUID(), otherUserId)
                .setTitle("Title 2")
                .setMessage("Message 2")
                .setType(NotificationType.BOOKING)
                .setIsRead(false)
                .build();

        when(notificationRepository.findAll()).thenReturn(List.of(userNotif, otherNotif));

        List<Notification> result = notificationService.getNotificationsForUser(userId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
    }

    @Test
    void testMarkAsRead_NotificationExists() {
        UUID notifId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Notification notif = new Notification.Builder(notifId, userId)
                .setTitle("Unread")
                .setMessage("Message")
                .setType(NotificationType.PAYMENT)
                .setIsRead(false)
                .build();

        when(notificationRepository.findById(notifId)).thenReturn(notif);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        notificationService.markAsRead(notifId);

        assertTrue(notif.isRead());
        verify(notificationRepository).save(notif);
    }

    @Test
    void testMarkAsRead_NotificationDoesNotExist() {
        UUID notifId = UUID.randomUUID();

        when(notificationRepository.findById(notifId)).thenReturn(null);

        notificationService.markAsRead(notifId);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testDeleteNotification_CallsRepository() {
        UUID notifId = UUID.randomUUID();

        notificationService.deleteNotification(notifId);

        verify(notificationRepository).deleteById(notifId);
    }
}
