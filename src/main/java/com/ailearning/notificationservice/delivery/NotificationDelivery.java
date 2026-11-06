package com.ailearning.notificationservice.delivery;

import com.ailearning.notificationservice.model.Notification;

public interface NotificationDelivery {
    void deliver(Notification notification);
}
