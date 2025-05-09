package id.ac.ui.cs.advprog.papikosbe.controller.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import id.ac.ui.cs.advprog.papikosbe.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/api/v1/notifications/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable UUID userId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsForUser(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.out.println("Error in getting notifications!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/api/v1/notifications", method = RequestMethod.POST)
    public ResponseEntity<Notification> createNotification(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) NotificationType type) {

        if (userId == null || title == null || message == null || type == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Notification notification = notificationService.createNotification(userId, title, message, type);
            return new ResponseEntity<>(notification, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("Error in creating notification!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/api/v1/notifications/{notificationId}/read", method = RequestMethod.PATCH)
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable UUID notificationId) {
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
            System.out.println("Error in marking notification as read!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/api/v1/notifications/{notificationId}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable UUID notificationId) {
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
            System.out.println("Error in deleting notification!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
