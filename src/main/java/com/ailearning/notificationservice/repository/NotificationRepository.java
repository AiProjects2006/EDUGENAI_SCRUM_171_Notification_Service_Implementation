package com.ailearning.notificationservice.repository;

import com.ailearning.notificationservice.model.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByRecipientIdOrderByCreatedAtDesc(
            String recipientId
    );

    Optional<NotificationEntity> findByIdAndRecipientId(
            Long id,
            String recipientId
    );
}
