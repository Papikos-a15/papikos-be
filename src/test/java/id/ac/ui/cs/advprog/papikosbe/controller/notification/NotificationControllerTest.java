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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

        // Create the test Notification using the Builder
        testNotification = Notification.builder()
                .id(notificationId)
                .userId((userId))
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

        // Create the request body map
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId.toString());
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("type", type.name());

        CompletableFuture<Notification> futureNotification = CompletableFuture.completedFuture(testNotification);

        // Simulating the service returning the created notification
        when(notificationService.createNotification(eq(userId), eq(title), eq(message), eq(type)))
                .thenReturn(futureNotification);

        // Send the request with the body map
        ResponseEntity<Notification> response = notificationController.createNotification(notificationData);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testNotification, response.getBody());
        verify(notificationService, times(1)).createNotification(eq(userId), eq(title), eq(message), eq(type));
        verify(notificationPublisher, times(1)).publish(testNotification);
    }

    @Test
    void testCreateNotification_NullParameters() {
        // Test when userId is null
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", null); // userId is null
        notificationData.put("title", "Title");
        notificationData.put("message", "Message");
        notificationData.put("type", NotificationType.SYSTEM.name());

        ResponseEntity<Notification> response1 = notificationController.createNotification(notificationData);
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertNull(response1.getBody());

        // Test when title is null
        notificationData.put("userId", userId.toString()); // valid userId
        notificationData.put("title", null); // title is null
        ResponseEntity<Notification> response2 = notificationController.createNotification(notificationData);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertNull(response2.getBody());

        // Test when message is null
        notificationData.put("title", "Title");
        notificationData.put("message", null); // message is null
        ResponseEntity<Notification> response3 = notificationController.createNotification(notificationData);
        assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        assertNull(response3.getBody());

        // Test when type is null
        notificationData.put("message", "Message");
        notificationData.put("type", null); // type is null
        ResponseEntity<Notification> response4 = notificationController.createNotification(notificationData);
        assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        assertNull(response4.getBody());

        // Verify the service method was never called due to invalid parameters
        verify(notificationService, never()).createNotification(any(), any(), any(), any());
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
}
