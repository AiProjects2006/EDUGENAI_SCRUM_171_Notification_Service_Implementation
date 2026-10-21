package com.ailearning.notificationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092",
        "notification.kafka.topic=test-notifications"
})
class NotificationServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
