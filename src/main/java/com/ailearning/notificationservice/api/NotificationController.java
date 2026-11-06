package com.ailearning.notificationservice.api;

import com.ailearning.notificationservice.model.NotificationEntity;
import com.ailearning.notificationservice.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(
            NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/{recipientId}")
    public List<NotificationEntity> getNotifications(
            @PathVariable String recipientId) {

        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    @PatchMapping("/{id}/read")
    public NotificationEntity markAsRead(
            @PathVariable Long id,
            @RequestParam String recipientId) {

        NotificationEntity notification = notificationRepository
                .findByIdAndRecipientId(id, recipientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification not found"));

        notification.markAsRead();

        return notificationRepository.save(notification);
    }
}
