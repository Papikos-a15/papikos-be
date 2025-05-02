package id.ac.ui.cs.advprog.papikosbe.controller;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.Notification;
import id.ac.ui.cs.advprog.papikosbe.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable UUID userId) {
        // TODO
        return null;
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestParam UUID userId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam NotificationType type) {
        // TODO
        return null;
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable UUID notificationId) {
        // TODO
        return null;
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable UUID notificationId) {
        // TODO
        return null;
    }
}