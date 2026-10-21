# Notification Service

Starter implementation for EDUGENAI Notification Service sprint tasks SCRUM-170–SCRUM-173.

## Important integration decisions to confirm

Before treating this as complete or deploying it, confirm with the team:
1. Repository naming/number and branch protection rules.
2. Kafka broker address, topic name, authentication and topic ownership.
3. Producer event schema and event types.
4. Delivery channel: email, in-app, or both.
5. Required database, Keycloak integration and deployment target.

This starter uses a **console delivery adapter for development only**. It logs that an event was handled; it does not send an email or persist an in-app notification. Replace/add a delivery adapter once the team agrees on the channel.

## Stack

- Java 17
- Spring Boot 3.5.16 (align with team-approved version)
- Maven
- Spring Kafka
- Spring Web
- JUnit 5 / Mockito

## Project layout

- `consumer/NotificationEventConsumer.java`: Kafka listener and JSON parsing.
- `event/NotificationEvent.java`: example event model; align with producer contract.
- `service/NotificationProcessingService.java`: validation and processing.
- `delivery/NotificationDelivery.java`: delivery abstraction.
- `delivery/ConsoleNotificationDelivery.java`: development-only adapter.
- `api/HealthController.java`: `GET /health`.
- `src/test`: unit and Spring context tests.

## Configure

Environment variables:
- `KAFKA_BOOTSTRAP_SERVERS` (default `localhost:9092`)
- `KAFKA_NOTIFICATION_TOPIC` (default `notification-events`)
- `KAFKA_CONSUMER_GROUP` (default `notification-service`)
- `SERVER_PORT` (default `8085`)
- `NOTIFICATION_DELIVERY_MODE` (default `console`)

Do not commit credentials. If the team Kafka uses authentication/TLS, add the required configuration using the team's approved secret handling.

## Run locally

Requires Java 17 and Maven, or use the Maven wrapper if provided by the repository.

```powershell
mvn clean test
mvn spring-boot:run
```

The app's health endpoint is `http://localhost:8085/health`.

Kafka must be running and the configured topic must be available for the consumer to receive events. If your team already provides Kafka, use that environment. `compose.kafka.example.yml` is an optional local example only; do not start a separate broker without coordinating with the team.

## Example event (illustrative only)

```json
{
  "eventId": "evt-123",
  "eventType": "COURSE_CREATED",
  "recipientId": "student-123",
  "recipientEmail": null,
  "subject": "New course",
  "message": "A new course is available",
  "occurredAt": "2026-10-21T10:00:00Z",
  "data": {}
}
```

The producer's actual schema is authoritative. Update the model and tests to match it.

## Suggested branch workflow

Create `main`, `dev`, then feature branches from `dev`:
- `feature/kafka-setup` — SCRUM-170
- `feature/event-consumer` — SCRUM-171
- `feature/notification-delivery` — SCRUM-172
- `feature/notification-tests` — SCRUM-173

Use pull requests into `dev`; merge `dev` into `main` only according to your team's approval process.

## Production-readiness items not included

- Email provider or in-app persistence implementation.
- Team-specific event contract and topic security.
- Dead-letter topic / configured retry policy for malformed events.
- Durable idempotency / duplicate notification prevention.
- Keycloak security for any user-facing endpoints, if required.
- Production observability and deployment manifests.
