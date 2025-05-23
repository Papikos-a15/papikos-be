package id.ac.ui.cs.advprog.papikosbe.controller.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import id.ac.ui.cs.advprog.papikosbe.service.notification.NotificationService;
import id.ac.ui.cs.advprog.papikosbe.observer.NotificationPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    private NotificationPublisher notificationPublisher;

    @Autowired
    public NotificationController(NotificationService notificationService, NotificationPublisher notificationPublisher) {
        this.notificationService = notificationService;
        this.notificationPublisher = notificationPublisher;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable(required = false) UUID userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Map<String, Object> notificationData) {
        UUID userId;
        String title;
        String message;
        NotificationType type;
        try{
            userId = UUID.fromString((String) notificationData.get("userId"));
            title = (String) notificationData.get("title");
            message = (String) notificationData.get("message");
            type = NotificationType.valueOf((String) notificationData.get("type"));

            if (userId == null || title == null || message == null || type == null) {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            CompletableFuture<Notification> notification = notificationService.createNotification(userId, title, message, type);

            notificationPublisher.publish(notification.get());

            return new ResponseEntity<>(notification.get(), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, String>> createNotificationForAllUser(@RequestBody Map<String, Object> notificationData) {
        String title;
        String message;
        NotificationType type;

        try {
            title = (String) notificationData.get("title");
            message = (String) notificationData.get("message");
            type = NotificationType.valueOf((String) notificationData.get("type"));

            if (title == null || message == null || type == null) {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            notificationService.createNotificationForAllUser(title, message, type);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification broadcasted to all users");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable(required = false) UUID notificationId) {
        Map<String, String> response = new HashMap<>();

        if (notificationId == null) {
            response.put("message", "Notification ID cannot be null");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            notificationService.markAsRead(notificationId);
            response.put("message", "Notification marked as read");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error in marking notification as read");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable(required = false) UUID notificationId) {
        Map<String, String> response = new HashMap<>();

        if (notificationId == null) {
            response.put("message", "Notification ID cannot be null");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            notificationService.deleteNotification(notificationId);
            response.put("message", "Notification deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error in deleting notification");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
