
package com.ailearning.notificationservice.service;

import com.ailearning.notificationservice.delivery.NotificationDelivery;
import com.ailearning.notificationservice.event.NotificationEvent;
import com.ailearning.notificationservice.model.Notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationProcessingServiceTest {

    @Mock
    private NotificationDelivery notificationDelivery;

    @Mock
    private NotificationEmailDeliveryService emailDeliveryService;

    @InjectMocks
    private NotificationProcessingService processingService;

    @Test
    void shouldProcessNotificationAndSendEmail() {

        NotificationEvent event = new NotificationEvent(
                "EVENT-001",
                "COURSE_ENROLLMENT",
                "STUDENT-001",
                "student@gmail.com",
                "Course Enrollment Successful",
                "You have successfully enrolled.",
                Instant.now(),
                Map.of()
        );

        processingService.process(event);

        verify(notificationDelivery, times(1))
                .deliver(any(Notification.class));

        verify(emailDeliveryService, times(1))
                .sendNotificationEmail(
                        "student@gmail.com",
                        "Course Enrollment Successful",
                        "You have successfully enrolled."
                );
    }

    @Test
    void shouldSkipEmailWhenRecipientEmailIsMissing() {

        NotificationEvent event = new NotificationEvent(
                "EVENT-002",
                "COURSE_ENROLLMENT",
                "STUDENT-002",
                null,
                "Course Enrollment",
                "Enrollment successful.",
                Instant.now(),
                Map.of()
        );

        processingService.process(event);

        verify(notificationDelivery, times(1))
                .deliver(any(Notification.class));

        verify(emailDeliveryService, never())
                .sendNotificationEmail(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }

    @Test
    void shouldThrowExceptionWhenEventIsInvalid() {

        NotificationEvent event = new NotificationEvent(
                "EVENT-003",
                null,
                "STUDENT-003",
                "student@gmail.com",
                "Test",
                "Test message",
                Instant.now(),
                Map.of()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> processingService.process(event)
        );

        verifyNoInteractions(
                notificationDelivery,
                emailDeliveryService
        );
    }

    @Test
    void shouldThrowExceptionWhenEventIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> processingService.process(null)
        );

        verifyNoInteractions(
                notificationDelivery,
                emailDeliveryService
        );
    }
}
