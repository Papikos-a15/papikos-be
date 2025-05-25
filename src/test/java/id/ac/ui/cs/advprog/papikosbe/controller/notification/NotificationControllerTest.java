package id.ac.ui.cs.advprog.papikosbe.controller.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import id.ac.ui.cs.advprog.papikosbe.observer.NotificationPublisher;
import id.ac.ui.cs.advprog.papikosbe.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private NotificationController notificationController;

    private UUID userId;
    private UUID notificationId;
    private Notification testNotification;
    private List<Notification> testNotifications;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        notificationId = UUID.randomUUID();

        testNotification = Notification.builder()
                .id(notificationId)
                .userId(userId)
                .title("Test Notification")
                .message("This is a test notification message")
                .createdAt(LocalDateTime.now())
                .type(NotificationType.SYSTEM)
                .isRead(false)
                .build();

        testNotifications = new ArrayList<>();
        testNotifications.add(testNotification);
    }

    @Test
    void testCreateNotification() {
        String title = "New Notification";
        String message = "This is a new notification";
        NotificationType type = NotificationType.SYSTEM;

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId.toString());
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("type", type.name());

        CompletableFuture<Notification> futureNotification = CompletableFuture.completedFuture(testNotification);

        when(notificationService.createNotification(eq(userId), eq(title), eq(message), eq(type)))
                .thenReturn(futureNotification);

        ResponseEntity<Notification> response = notificationController.createNotification(notificationData);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testNotification, response.getBody());
        verify(notificationService, times(1)).createNotification(eq(userId), eq(title), eq(message), eq(type));
        verify(notificationPublisher, times(1)).publish(testNotification);
    }

    @Test
    void testCreateNotification_NullParameters() {
        // Case 1: userId is null
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", null); // userId is null
        notificationData.put("title", "Title");
        notificationData.put("message", "Message");
        notificationData.put("type", NotificationType.SYSTEM.name());

        ResponseEntity<Notification> response1 = notificationController.createNotification(notificationData);
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());

        // Case 2: title is null
        notificationData.put("userId", userId.toString()); // valid userId
        notificationData.put("title", null); // title is null
        ResponseEntity<Notification> response2 = notificationController.createNotification(notificationData);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());

        // Case 3: message is null
        notificationData.put("title", "Title");
        notificationData.put("message", null); // message is null
        ResponseEntity<Notification> response3 = notificationController.createNotification(notificationData);
        assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());

        // Case 4: type is null
        notificationData.put("message", "Message");
        notificationData.put("type", null); // type is null
        ResponseEntity<Notification> response4 = notificationController.createNotification(notificationData);
        assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());

        // Case 5: all valid parameters
        notificationData.put("userId", userId.toString());
        notificationData.put("title", "Title");
        notificationData.put("message", "Message");
        notificationData.put("type", NotificationType.SYSTEM.name());

        CompletableFuture<Notification> futureNotification = CompletableFuture.completedFuture(testNotification);
        when(notificationService.createNotification(eq(userId), eq("Title"), eq("Message"), eq(NotificationType.SYSTEM)))
                .thenReturn(futureNotification);

        ResponseEntity<Notification> response5 = notificationController.createNotification(notificationData);
        assertEquals(HttpStatus.CREATED, response5.getStatusCode());

        // Verify no unexpected calls
        verify(notificationService, times(1)).createNotification(eq(userId), eq("Title"), eq("Message"), eq(NotificationType.SYSTEM));
    }


    @Test
    void testCreateNotificationForAllUser() {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", "Broadcast Title");
        notificationData.put("message", "Broadcast Message");
        notificationData.put("type", NotificationType.SYSTEM.name());

        doNothing().when(notificationService).createNotificationForAllUser(
                eq("Broadcast Title"),
                eq("Broadcast Message"),
                eq(NotificationType.SYSTEM)
        );

        ResponseEntity<Map<String, String>> response = notificationController.createNotificationForAllUser(notificationData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Notification broadcasted to all users", response.getBody().get("message"));
        verify(notificationService, times(1)).createNotificationForAllUser("Broadcast Title", "Broadcast Message", NotificationType.SYSTEM);
    }

    @Test
    void testCreateNotificationForAllUser_InvalidData() {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", null);

        ResponseEntity<Map<String, String>> response = notificationController.createNotificationForAllUser(notificationData);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(notificationService, never()).createNotificationForAllUser(any(), any(), any());
    }

    @Test
    void testMarkAsRead() {
        doNothing().when(notificationService).markAsRead(notificationId);

        ResponseEntity<Map<String, String>> response = notificationController.markAsRead(notificationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Notification marked as read", response.getBody().get("message"));
        verify(notificationService, times(1)).markAsRead(notificationId);
    }

    @Test
    void testMarkAsRead_NullId() {
        ResponseEntity<Map<String, String>> response = notificationController.markAsRead(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Notification ID cannot be null", response.getBody().get("message"));
        verify(notificationService, never()).markAsRead(any());
    }

    @Test
    void testDeleteNotification() {
        doNothing().when(notificationService).deleteNotification(notificationId);

        ResponseEntity<Map<String, String>> response = notificationController.deleteNotification(notificationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Notification deleted successfully", response.getBody().get("message"));
        verify(notificationService, times(1)).deleteNotification(notificationId);
    }

    @Test
    void testDeleteNotification_NullId() {
        ResponseEntity<Map<String, String>> response = notificationController.deleteNotification(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Notification ID cannot be null", response.getBody().get("message"));
        verify(notificationService, never()).deleteNotification(any());
    }

    @Test
    void testGetNotification_Success() {
        Notification expectedNotification = Notification.builder()
                .id(notificationId)
                .userId(userId)
                .title("Test Notification")
                .message("Test message")
                .createdAt(LocalDateTime.now())
                .type(NotificationType.SYSTEM)
                .isRead(false)
                .build();

        when(notificationService.getNotificationById(notificationId)).thenReturn(expectedNotification);

        ResponseEntity<Notification> response = notificationController.getNotification(notificationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedNotification, response.getBody());

        verify(notificationService, times(1)).getNotificationById(notificationId);
    }

    @Test
    void testGetNotification_NotFound() {
        when(notificationService.getNotificationById(notificationId)).thenReturn(null);

        ResponseEntity<Notification> response = notificationController.getNotification(notificationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        verify(notificationService, times(1)).getNotificationById(notificationId);
    }

    @Test
    void testGetNotification_NullId() {
        ResponseEntity<Notification> response = notificationController.getNotification(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(notificationService, never()).getNotificationById(any());
    }

    @Test
    void testGetNotificationsForUser_Success() {
        Notification notification1 = Notification.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Test Notification 1")
                .message("This is a test notification message 1")
                .createdAt(LocalDateTime.now())
                .type(NotificationType.SYSTEM)
                .isRead(false)
                .build();

        Notification notification2 = Notification.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Test Notification 2")
                .message("This is a test notification message 2")
                .createdAt(LocalDateTime.now())
                .type(NotificationType.SYSTEM)
                .isRead(false)
                .build();

        List<Notification> notifications = List.of(notification1, notification2);

        when(notificationService.getNotificationsForUser(userId)).thenReturn(notifications);

        ResponseEntity<List<Notification>> response = notificationController.getNotificationsForUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(notification1, response.getBody().get(0));
        assertEquals(notification2, response.getBody().get(1));

        verify(notificationService, times(1)).getNotificationsForUser(userId);
    }

    @Test
    void testGetNotificationsForUser_NullId() {
        ResponseEntity<List<Notification>> response = notificationController.getNotificationsForUser(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(notificationService, never()).getNotificationsForUser(any());
    }

    @Test
    void testGetNotificationsForUser_NoNotifications() {
        when(notificationService.getNotificationsForUser(userId)).thenReturn(new ArrayList<>());

        ResponseEntity<List<Notification>> response = notificationController.getNotificationsForUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(notificationService, times(1)).getNotificationsForUser(userId);
    }

    @Test
    void testCreateNotification_InterruptedException() {
        String title = "New Notification";
        String message = "This is a new notification";
        NotificationType type = NotificationType.SYSTEM;

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId.toString());
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("type", type.name());

        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            throw new InterruptedException("Simulating InterruptedException");
        }).when(notificationService).createNotification(eq(userId), eq(title), eq(message), eq(type));

        ResponseEntity<Notification> response = notificationController.createNotification(notificationData);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(notificationService, times(1)).createNotification(eq(userId), eq(title), eq(message), eq(type));
        verify(notificationPublisher, never()).publish(any());  // No publish should happen in this case
    }

    @Test
    void testCreateNotification_GenericException() throws Exception {
        String title = "New Notification";
        String message = "This is a new notification";
        NotificationType type = NotificationType.SYSTEM;

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId.toString());
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("type", type.name());

        when(notificationService.createNotification(eq(userId), eq(title), eq(message), eq(type)))
                .thenThrow(new RuntimeException("Generic RuntimeException"));

        ResponseEntity<Notification> response = notificationController.createNotification(notificationData);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(notificationService, times(1)).createNotification(eq(userId), eq(title), eq(message), eq(type));
        verify(notificationPublisher, never()).publish(any());
    }

    @Test
    void testCreateNotificationForAllUser_NullParameters() {
        Map<String, Object> notificationData = new HashMap<>();

        // Case 1: Title is null
        notificationData.put("title", null);
        notificationData.put("message", "Broadcast Message");
        notificationData.put("type", NotificationType.SYSTEM.name());

        ResponseEntity<Map<String, String>> response1 = notificationController.createNotificationForAllUser(notificationData);
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());

        // Case 2: Message is null
        notificationData.put("title", "Broadcast Title");
        notificationData.put("message", null);
        notificationData.put("type", NotificationType.SYSTEM.name());

        ResponseEntity<Map<String, String>> response2 = notificationController.createNotificationForAllUser(notificationData);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());

        // Case 3: Type is null
        notificationData.put("title", "Broadcast Title");
        notificationData.put("message", "Broadcast Message");
        notificationData.put("type", null);

        ResponseEntity<Map<String, String>> response3 = notificationController.createNotificationForAllUser(notificationData);
        assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());

        // Case 4: All parameters are valid
        notificationData.put("title", "Broadcast Title");
        notificationData.put("message", "Broadcast Message");
        notificationData.put("type", NotificationType.SYSTEM.name());

        ResponseEntity<Map<String, String>> response4 = notificationController.createNotificationForAllUser(notificationData);
        assertEquals(HttpStatus.OK, response4.getStatusCode());
        assertEquals("Notification broadcasted to all users", response4.getBody().get("message"));
    }

    @Test
    void testCreateNotificationForAllUser_CatchBlock() {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", "Broadcast Title");
        notificationData.put("message", "Broadcast Message");
        notificationData.put("type", NotificationType.SYSTEM.name());

        doThrow(new RuntimeException("Simulating an error")).when(notificationService).createNotificationForAllUser(
                eq("Broadcast Title"), eq("Broadcast Message"), eq(NotificationType.SYSTEM)
        );

        ResponseEntity<Map<String, String>> response = notificationController.createNotificationForAllUser(notificationData);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(notificationService, times(1)).createNotificationForAllUser("Broadcast Title", "Broadcast Message", NotificationType.SYSTEM);
    }

    @Test
    void testMarkAsRead_ServiceException() {
        UUID notificationId = UUID.randomUUID();

        doThrow(new RuntimeException("Error marking notification as read")).when(notificationService).markAsRead(eq(notificationId));

        ResponseEntity<Map<String, String>> response = notificationController.markAsRead(notificationId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error in marking notification as read", response.getBody().get("message"));
    }

    @Test
    void testDeleteNotification_ServiceException() {
        UUID notificationId = UUID.randomUUID();

        doThrow(new RuntimeException("Error deleting notification")).when(notificationService).deleteNotification(eq(notificationId));

        ResponseEntity<Map<String, String>> response = notificationController.deleteNotification(notificationId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error in deleting notification", response.getBody().get("message"));
    }


}
