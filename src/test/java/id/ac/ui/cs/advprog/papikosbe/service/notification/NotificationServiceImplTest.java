package id.ac.ui.cs.advprog.papikosbe.service.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.observer.NotificationPublisher;
import id.ac.ui.cs.advprog.papikosbe.repository.notification.NotificationRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNotification_Success() throws ExecutionException, InterruptedException {
        UUID userId = UUID.randomUUID();
        String title = "Test Title";
        String message = "Test Message";
        NotificationType type = NotificationType.SYSTEM;

        // We simulate repository.save() returning the same notification
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        CompletableFuture<Notification> future = notificationService.createNotification(userId, title, message, type);
        Notification result = future.get();

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
    void testCreateNotificationForAllUser_Success() throws InterruptedException {
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        NotificationPublisher notificationPublisher = mock(NotificationPublisher.class);

        User user1 = new Tenant();
        user1.setId(UUID.randomUUID());
        User user2 = new Owner();
        user2.setId(UUID.randomUUID());

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationServiceImpl notificationService = new NotificationServiceImpl(
                notificationRepository, notificationPublisher, userRepository
        );

        notificationService.createNotificationForAllUser("Title", "Message", NotificationType.SYSTEM);

        Thread.sleep(500);

        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(notificationPublisher, times(2)).publish(any(Notification.class));
    }


    @Test
    void testGetNotificationsForUser_FiltersCorrectly() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        Notification userNotif = Notification.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Title 1")
                .message("Message 1")
                .type(NotificationType.SYSTEM)
                .isRead(false)
                .build();

        Notification otherNotif = Notification.builder()
                .id(UUID.randomUUID())
                .userId(otherUserId)
                .title("Title 2")
                .message("Message 2")
                .type(NotificationType.BOOKING)
                .isRead(false)
                .build();

        when(notificationRepository.findByUserId(userId)).thenReturn(List.of(userNotif));

        List<Notification> result = notificationService.getNotificationsForUser(userId);

        assertEquals(1, result.size());
        assertEquals(userId, result.getFirst().getUserId());
    }

    @Test
    void testGetNotificationById_Success() {
        UUID notifId = UUID.randomUUID();
        Notification expectedNotification = Notification.builder()
                .id(notifId)
                .userId(UUID.randomUUID())
                .title("Test Title")
                .message("Test Message")
                .type(NotificationType.SYSTEM)
                .isRead(false)
                .build();

        // Mock the repository to return the notification when findById is called
        when(notificationRepository.findById(notifId)).thenReturn(Optional.of(expectedNotification));

        // Call the service method
        Notification result = notificationService.getNotificationById(notifId);

        // Assert that the returned notification is the one we mocked
        assertNotNull(result);
        assertEquals(notifId, result.getId());
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Message", result.getMessage());
        assertEquals(NotificationType.SYSTEM, result.getType());
        assertFalse(result.isRead());

        // Verify that findById was called once
        verify(notificationRepository, times(1)).findById(notifId);
    }

    @Test
    void testGetNotificationById_NotFound() {
        UUID notifId = UUID.randomUUID();

        // Mock the repository to return an empty Optional when the notification is not found
        when(notificationRepository.findById(notifId)).thenReturn(Optional.empty());

        // Call the service method
        Notification result = notificationService.getNotificationById(notifId);

        // Assert that the result is null when not found
        assertNull(result);

        // Verify that findById was called once
        verify(notificationRepository, times(1)).findById(notifId);
    }


    @Test
    void testMarkAsRead_NotificationExists() {
        UUID notifId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Notification notif = Notification.builder()
                .id(notifId)
                .userId(userId)
                .title("Unread")
                .message("Message")
                .type(NotificationType.PAYMENT)
                .isRead(false)
                .build();


        when(notificationRepository.findById(notifId)).thenReturn(Optional.ofNullable(notif));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        notificationService.markAsRead(notifId);

        assert notif != null;
        assertTrue(notif.isRead());
        verify(notificationRepository).save(notif);
    }

    @Test
    void testMarkAsRead_NotificationDoesNotExist() {
        UUID notifId = UUID.randomUUID();

        when(notificationRepository.findById(notifId)).thenReturn(Optional.empty());

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
