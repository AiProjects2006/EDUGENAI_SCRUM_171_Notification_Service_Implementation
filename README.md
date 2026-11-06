# Notification Service

Notification Service for the EDUGENAI project. This service receives notification events, validates and processes them, and passes them to a delivery adapter.

## Sprint Tasks

- **SCRUM-170 — Configure Amazon SQS** (3 points): Configure the AWS SDK, queue settings, and connection.
- **SCRUM-171 — Consume notification events** (2 points): Receive, deserialize, validate, and process messages from SQS.
- **SCRUM-172 — Process and deliver notifications** (2 points): Prepare notification content and implement the agreed delivery channel.
- **SCRUM-173 — Test and document** (1 point): Test SQS processing and error handling, and update documentation.

## Current Implementation Status

- SQS client configuration is implemented.
- SQS event consumer code and tests are present.
- Notification event validation and processing are implemented.
- Console notification delivery is available for local development.
- A local REST endpoint allows notification processing to be tested without sending an SQS message.
- The health endpoint is available at `/health`.
- The latest recorded automated test run passed 10 tests with no failures or errors.
- Real AWS SQS operation and real email delivery still require final verification.

Console delivery logs notification details. It does not send real emails or persist in-app notifications.

## Technology Stack

- Java 17
- Spring Boot
- Maven
- AWS SDK for Java 2.x (Amazon SQS)
- Spring Web
- Jackson JSON processing
- Springdoc OpenAPI / Swagger UI
- JUnit 5 and Mockito

## Project Structure

- `NotificationServiceApplication.java`: Spring Boot application entry point and scheduling configuration.
- `api/HealthController.java`: Health endpoint.
- `api/LocalNotificationController.java`: Local notification test endpoint.
- `config/AwsSqsConfig.java`: AWS SQS client configuration.
- `consumer/SqsNotificationEventConsumer.java`: SQS polling, event processing, and message deletion after successful processing.
- `event/NotificationEvent.java`: Notification event model.
- `service/NotificationProcessingService.java`: Event validation and processing.
- `delivery/NotificationDelivery.java`: Delivery abstraction.
- `delivery/ConsoleNotificationDelivery.java`: Development-only console delivery adapter.
- `src/test`: Automated tests.

## Prerequisites

- Java 17
- Maven
- AWS credentials and appropriate IAM permissions only when testing real AWS SQS

## Configuration

| Variable | Default | Purpose |
|---|---|---|
| `SERVER_PORT` | `8085` | Application port |
| `NOTIFICATION_DELIVERY_MODE` | `console` | Selects the console delivery adapter |
| `NOTIFICATION_LOCAL_ENABLED` | `true` in the local shell setup | Enables the local notification endpoint through Spring property binding |
| `NOTIFICATION_SQS_ENABLED` | `false` | Enables SQS polling when set to `true` |
| `NOTIFICATION_SQS_QUEUE_URL` | Not configured | SQS queue URL; required when SQS polling is enabled |
| `NOTIFICATION_SQS_POLL_INTERVAL_MS` | `5000` | Delay between SQS polling operations |
| `AWS_REGION` | `ap-south-1` | AWS region used by the SQS client |

Spring Boot maps environment variables to matching configuration properties. When enabling SQS, ensure the queue URL and region match the intended AWS queue.

Do not commit AWS credentials, passwords, tokens, or other secrets. Use an approved AWS credential provider or IAM role.

## Run Locally

From the project root:

```powershell
mvn clean test
mvn spring-boot:run
```

For local console delivery, set the mode explicitly if needed:

```powershell
$env:NOTIFICATION_DELIVERY_MODE = "console"
$env:NOTIFICATION_LOCAL_ENABLED = "true"
Remove-Item Env:NOTIFICATION_SQS_ENABLED -ErrorAction SilentlyContinue
mvn spring-boot:run
```

Keep the application terminal open while testing from another PowerShell terminal.

### Health Check

Request:

```powershell
Invoke-RestMethod -Uri "http://localhost:8085/health"
```

Expected response:

```json
{
  "status": "UP",
  "service": "notification-service"
}
```

### Test Local Notification Processing

The local endpoint is:

`POST http://localhost:8085/api/notifications/test`

Example PowerShell request:

```powershell
$body = @{
    eventId = "local-demo-001"
    eventType = "COURSE_CREATED"
    recipientId = "student-001"
    recipientEmail = "student@example.com"
    subject = "Welcome to the course"
    message = "Your course notification was processed locally."
    occurredAt = "2026-10-23T12:00:00Z"
    data = @{}
} | ConvertTo-Json -Depth 5

Invoke-RestMethod `
    -Uri "http://localhost:8085/api/notifications/test" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

Expected response:

```json
{
  "status": "ACCEPTED",
  "eventId": "local-demo-001",
  "message": "Notification processed locally"
}
```

The application logs should show the notification output and processing messages. This verifies the local processing path, not real email delivery.

## Amazon SQS Configuration and Testing

The SQS consumer is controlled by `notification.sqs.enabled`. SQS polling is disabled by default.

Before testing real AWS SQS:

1. Confirm the approved AWS account, region, and test queue.
2. Configure `NOTIFICATION_SQS_ENABLED=true`.
3. Configure `NOTIFICATION_SQS_QUEUE_URL` with the intended queue URL.
4. Verify AWS credentials and IAM permissions.
5. Send a valid notification event to the queue.
6. Confirm that the event is processed and the message is deleted after successful processing.
7. Test invalid messages, retries, and dead-letter queue behavior.

The consumer does not delete a message when processing fails. Depending on the queue visibility timeout and redrive configuration, failed messages may be delivered again. Verify the retry and dead-letter queue strategy before production use.

AWS SQS testing should be performed only when the correct account, queue, and credentials are available.

## Notification Delivery

The current console delivery adapter is for development and testing. The project still needs confirmation of the agreed real delivery channel and, if email is required, the provider and sender identity.

Before enabling real email delivery:

- Agree on the provider and sender identity with the team.
- Store credentials securely.
- Test successful delivery and provider failures.
- Document the supported notification types and event schema.

## Testing

Run the automated tests from the project root:

```powershell
mvn clean test
```

Latest recorded result:

- Tests run: 10
- Failures: 0
- Errors: 0
- Result: `BUILD SUCCESS`

Rerun the tests after code changes and update these figures to match the latest result.

## Remaining Work

- Verify real AWS SQS connection and message processing.
- Confirm queue URL and region configuration.
- Verify validation, retry, and dead-letter queue behavior.
- Confirm the producer's event schema and supported notification types.
- Implement the agreed real delivery provider if required.
- Confirm whether notification history or in-app notification APIs are in scope.
- Review Swagger/OpenAPI documentation for the available endpoints.
- Add monitoring and deployment configuration as required.

## Branch Workflow

Current development branch: `feature/local-notification-mode`.

Run tests before committing. Review the changes and commit them to the feature branch. Follow the team's review and pull-request process before merging.
