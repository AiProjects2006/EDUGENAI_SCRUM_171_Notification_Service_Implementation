package com.ailearning.notificationservice.service;

import com.ailearning.notificationservice.delivery.NotificationDelivery;
import com.ailearning.notificationservice.event.NotificationEvent;
import com.ailearning.notificationservice.model.Notification;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
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
                        && n.message().equals("A course is available")));
    }

    @Test
    void rejectsEventWithoutRequiredFields() {
        NotificationDelivery delivery = mock(NotificationDelivery.class);
        NotificationProcessingService service = new NotificationProcessingService(delivery);
        NotificationEvent event = new NotificationEvent(
                "evt-2", "", "student-1", null, null, "", null, Map.of()
        );

        assertThrows(IllegalArgumentException.class, () -> service.process(event));
        verifyNoInteractions(delivery);
    }
}
