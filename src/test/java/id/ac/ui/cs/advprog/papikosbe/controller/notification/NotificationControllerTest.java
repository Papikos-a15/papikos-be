package id.ac.ui.cs.advprog.papikosbe.controller.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

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
        testNotification = new Notification.Builder(notificationId, userId)
                .setTitle("Test Notification")
                .setMessage("This is a test notification message")
                .setCreatedAt(LocalDateTime.now())
                .setType(NotificationType.SYSTEM)
                .setIsRead(false)
                .build();

        testNotifications = new ArrayList<>();
        testNotifications.add(testNotification);
    }

    @Test
    void testGetNotificationsForUser() {
        when(notificationService.getNotificationsForUser(userId)).thenReturn(testNotifications);

        ResponseEntity<List<Notification>> response = notificationController.getNotificationsForUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testNotification, response.getBody().get(0)); // Changed to index 0 as it is a List
        verify(notificationService, times(1)).getNotificationsForUser(userId);
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
    void testCreateNotification() {
        String title = "New Notification";
        String message = "This is a new notification";
        NotificationType type = NotificationType.SYSTEM;

        // Simulating the service returning the created notification
        when(notificationService.createNotification(eq(userId), eq(title), eq(message), eq(type)))
                .thenReturn(testNotification);

        ResponseEntity<Notification> response = notificationController.createNotification(
                userId, title, message, type);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testNotification, response.getBody());
        verify(notificationService, times(1)).createNotification(userId, title, message, type);
    }

    @Test
    void testCreateNotification_NullParameters() {
        ResponseEntity<Notification> response1 = notificationController.createNotification(
                null, "Title", "Message", NotificationType.SYSTEM);

        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertNull(response1.getBody());

        ResponseEntity<Notification> response2 = notificationController.createNotification(
                userId, null, "Message", NotificationType.SYSTEM);

        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertNull(response2.getBody());

        ResponseEntity<Notification> response3 = notificationController.createNotification(
                userId, "Title", null, NotificationType.SYSTEM);

        assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        assertNull(response3.getBody());

        ResponseEntity<Notification> response4 = notificationController.createNotification(
                userId, "Title", "Message", null);

        assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        assertNull(response4.getBody());

        verify(notificationService, never()).createNotification(any(), any(), any(), any());
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