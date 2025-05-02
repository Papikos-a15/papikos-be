package id.ac.ui.cs.advprog.papikosbe.controller;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.Notification;
import id.ac.ui.cs.advprog.papikosbe.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable(required = false) UUID userId) {
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) NotificationType type) {

        if (userId == null || title == null || message == null || type == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Notification notification = notificationService.createNotification(userId, title, message, type);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable(required = false) UUID notificationId) {
        Map<String, String> response = new HashMap<>();

        if (notificationId == null) {
            response.put("message", "Notification ID cannot be null");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        notificationService.markAsRead(notificationId);
        response.put("message", "Notification marked as read");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable(required = false) UUID notificationId) {
        Map<String, String> response = new HashMap<>();

        if (notificationId == null) {
            response.put("message", "Notification ID cannot be null");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        notificationService.deleteNotification(notificationId);
        response.put("message", "Notification deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}