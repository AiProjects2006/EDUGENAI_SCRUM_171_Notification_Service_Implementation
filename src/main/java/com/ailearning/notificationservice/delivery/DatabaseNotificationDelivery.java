package com.ailearning.notificationservice.delivery;

import com.ailearning.notificationservice.model.Notification;
import com.ailearning.notificationservice.model.NotificationEntity;
import com.ailearning.notificationservice.repository.NotificationRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "notification.delivery.mode",
        havingValue = "database"
)
public class DatabaseNotificationDelivery implements NotificationDelivery {

    private final NotificationRepository notificationRepository;

    public DatabaseNotificationDelivery(
            NotificationRepository notificationRepository
    ) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void deliver(Notification notification) {
        NotificationEntity entity = new NotificationEntity(
                notification.eventId(),
                notification.eventType(),
                notification.recipientId(),
                notification.recipientEmail(),
                notification.subject(),
                notification.message(),
                notification.createdAt()
        );

        notificationRepository.save(entity);
    }
}