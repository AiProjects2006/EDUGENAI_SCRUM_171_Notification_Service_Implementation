package com.ailearning.notificationservice.service;

import com.ailearning.notificationservice.delivery.NotificationDelivery;
import com.ailearning.notificationservice.event.NotificationEvent;
import com.ailearning.notificationservice.model.Notification;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationProcessingServiceTest {

    @Test
    void processesValidEventAndDeliversNotification() {
        NotificationDelivery delivery = mock(NotificationDelivery.class);
        NotificationProcessingService service = new NotificationProcessingService(delivery);
        NotificationEvent event = new NotificationEvent(
                "evt-1", "COURSE_CREATED", "student-1", null,
                "New course", "A course is available", null, Map.of()
        );

        service.process(event);

        verify(delivery).deliver(argThat((Notification n) ->
                n.eventType().equals("COURSE_CREATED")
                        && n.recipientId().equals("student-1")
                        && n.message().equals("A course is available")
                        && n.subject().equals("New course")));
    }

    @Test
    void rejectsEventWithoutEventType() {
        NotificationDelivery delivery = mock(NotificationDelivery.class);
        NotificationProcessingService service = new NotificationProcessingService(delivery);
        NotificationEvent event = new NotificationEvent(
                "evt-2", " ", "student-1", null, null,
                "A message", null, Map.of()
        );

        assertThrows(IllegalArgumentException.class, () -> service.process(event));
        verifyNoInteractions(delivery);
    }

    @Test
    void rejectsEventWithoutMessage() {
        NotificationDelivery delivery = mock(NotificationDelivery.class);
        NotificationProcessingService service = new NotificationProcessingService(delivery);
        NotificationEvent event = new NotificationEvent(
                "evt-3", "COURSE_CREATED", "student-1", null,
                "New course", " ", null, Map.of()
        );

        assertThrows(IllegalArgumentException.class, () -> service.process(event));
        verifyNoInteractions(delivery);
    }

    @Test
    void usesDefaultSubjectWhenSubjectIsMissing() {
        NotificationDelivery delivery = mock(NotificationDelivery.class);
        NotificationProcessingService service = new NotificationProcessingService(delivery);
        NotificationEvent event = new NotificationEvent(
                "evt-4", "COURSE_CREATED", "student-1", null,
                null, "A course is available", null, Map.of()
        );

        service.process(event);

        verify(delivery).deliver(argThat((Notification n) ->
                n.subject().equals("Notification: COURSE_CREATED")));
    }

    @Test
    void usesDefaultSubjectWhenSubjectIsBlank() {
        NotificationDelivery delivery = mock(NotificationDelivery.class);
        NotificationProcessingService service = new NotificationProcessingService(delivery);
        NotificationEvent event = new NotificationEvent(
                "evt-5", "PASSWORD_RESET", "student-1", null,
                "   ", "Reset your password", null, Map.of()
        );

        service.process(event);

        verify(delivery).deliver(argThat((Notification n) ->
                n.eventType().equals("PASSWORD_RESET")
                        && n.subject().equals("Notification: PASSWORD_RESET")));
    }

    @Test
    void propagatesDeliveryFailure() {
        NotificationDelivery delivery = mock(NotificationDelivery.class);
        NotificationProcessingService service = new NotificationProcessingService(delivery);
        NotificationEvent event = new NotificationEvent(
                "evt-6", "COURSE_CREATED", "student-1", null,
                "New course", "A course is available", null, Map.of()
        );

        doThrow(new IllegalStateException("Delivery failed"))
                .when(delivery).deliver(any(Notification.class));

        assertThrows(IllegalStateException.class, () -> service.process(event));
    }

    @Test
    void processesDifferentNotificationEventTypes() {
        NotificationDelivery delivery = mock(NotificationDelivery.class);
        NotificationProcessingService service = new NotificationProcessingService(delivery);

        NotificationEvent courseEvent = new NotificationEvent(
                "evt-7", "COURSE_CREATED", "student-1", null,
                "New course", "A course is available", null, Map.of()
        );
        NotificationEvent reminderEvent = new NotificationEvent(
                "evt-8", "LEARNING_REMINDER", "student-1", null,
                "Learning reminder", "Continue your learning", null, Map.of()
        );

        service.process(courseEvent);
        service.process(reminderEvent);

        verify(delivery, times(2)).deliver(any(Notification.class));
    }
}