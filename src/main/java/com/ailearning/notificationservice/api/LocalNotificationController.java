
package com.ailearning.notificationservice.api;

import com.ailearning.notificationservice.event.NotificationEvent;
import com.ailearning.notificationservice.service.NotificationProcessingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@ConditionalOnProperty(
        name = "notification.local.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class LocalNotificationController {

    private final NotificationProcessingService processingService;

    public LocalNotificationController(
            NotificationProcessingService processingService) {
        this.processingService = processingService;
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTestNotification(
            @RequestBody NotificationEvent event) {

        if (event == null || !event.hasMinimumRequiredFields()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "ERROR",
                            "message", "eventType and message are required"
                    ));
        }

        processingService.process(event);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "status", "ACCEPTED",
                        "eventId", event.eventId() == null ? "" : event.eventId(),
                        "message", "Notification processed locally"
                ));
    }
}
