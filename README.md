# Notification Service

Notification Service for the EDUGENAI project. This service consumes notification events, validates them, processes them, and passes them to a delivery adapter.

Sprint tasks:
- SCRUM-170: Set Up and Configure Kafka Integration
- SCRUM-171: Implement Notification Event Consumer
- SCRUM-172: Implement Notification Processing Logic and Delivery Mechanism
- SCRUM-173: Test and Document Notification Service

## Current implementation status

- Kafka event consumption has been tested locally.
- Notification processing and console delivery are implemented.
- SQS consumer code and unit tests are included.
- The automated test suite currently has 15 passing tests.
- Real AWS SQS consumption and real email delivery have not yet been verified.

Console delivery is for local development only. It logs notification details but does not send emails or persist in-app notifications.

## Technology stack

- Java 17
- Spring Boot
- Maven
- Spring Kafka
- AWS SDK for Java 2.x (SQS)
- Spring Web
- JUnit 5 and Mockito

## Project structure

- `consumer/NotificationEventConsumer.java`: Kafka event consumer.
- `consumer/SqsNotificationEventConsumer.java`: SQS polling and message processing.
- `config/AwsSqsConfig.java`: AWS SQS client configuration.
- `event/NotificationEvent.java`: notification event model.
- `service/NotificationProcessingService.java`: event validation and processing.
- `delivery/NotificationDelivery.java`: delivery abstraction.
- `delivery/ConsoleNotificationDelivery.java`: development-only delivery adapter.
- `api/HealthController.java`: health endpoint.
- `src/test`: automated tests.

## Prerequisites

- Java 17
- Maven
- Docker Desktop for the local Kafka setup
- AWS CLI only if testing real AWS SQS

## Configuration

The application uses these environment variables:

| Variable | Default | Purpose |
|---|---|---|
| `SERVER_PORT` | `8085` | Application port |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker |
| `KAFKA_NOTIFICATION_TOPIC` | `notification-events` | Kafka topic |
| `KAFKA_CONSUMER_GROUP` | `notification-service` | Kafka consumer group |
| `NOTIFICATION_DELIVERY_MODE` | `console` | Development delivery adapter |
| `AWS_REGION` | `ap-south-1` in the SQS client configuration | AWS region |
| `NOTIFICATION_SQS_QUEUE_URL` | Not configured | Intended environment variable for the SQS queue URL; application property mapping must be configured before using it |
| `NOTIFICATION_SQS_POLL_INTERVAL_MS` | `5000` | SQS polling interval |

Do not commit AWS credentials, passwords, tokens, or other secrets. Use an approved AWS credential provider or IAM role.

## Start Kafka locally

The project has an example Docker Compose file named `compose.kafka.example.yml`.

Use the team's approved local Kafka setup. If the `notification-kafka` container is already running, do not start another broker unnecessarily.

Check the container:

```powershell
docker ps
```

List Kafka topics:

```powershell
docker exec notification-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```

The `notification-events` topic should exist for the documented Kafka test.

## Run automated tests

From the project root:

```powershell
mvn clean test
```

Latest recorded result:

- Tests run: 15
- Failures: 0
- Errors: 0
- Result: BUILD SUCCESS

Run the tests again after making changes and update these figures if the results change.

## Run the application

```powershell
mvn spring-boot:run
```

Health endpoint:

`http://localhost:8085/health`

Expected response:

```json
{
  "status": "UP",
  "service": "notification-service"
}
```

## Test a Kafka notification event

Keep the application running in one terminal. Open a second PowerShell window and run:

```powershell
docker exec -i notification-kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic notification-events
```

Paste this example as one line and press Enter:

```json
{"eventId":"sprint-test-001","eventType":"COURSE_CREATED","recipientId":"student-123","subject":"Test notification","message":"Testing notification event consumption","data":{}}
```

Press `Ctrl+C` to exit the producer. Check the application logs for the event ID `sprint-test-001` and messages indicating notification processing.

This verifies the local Kafka-to-processing flow. Console output is not proof that an email was sent.

## AWS SQS status and testing

The SQS consumer receives messages, passes valid events to the processing service, and deletes messages after successful processing. Failed messages are not deleted by this consumer and may be retried according to the queue's visibility and redrive configuration.

Real AWS SQS integration is not yet verified. Before testing it:

1. Confirm the AWS region and approved test queue.
2. Configure the SQS queue URL through application configuration.
3. Use valid AWS credentials and appropriate IAM permissions.
4. Send a test message and verify successful processing and deletion.
5. Configure and verify retry and dead-letter queue behavior.

The SQS consumer is conditional on `notification.sqs.queue-url`. The current application configuration does not map `NOTIFICATION_SQS_QUEUE_URL` to that property yet.

## Notification delivery status

The current console adapter is intended for development. The real email provider and its API credentials have not been confirmed in this repository's current configuration.

Before enabling real email delivery, agree on the provider and sender identity with the team, store secrets securely, and test success and failure scenarios.

## Known limitations and follow-up work

- Verify real AWS SQS consumption.
- Confirm the final queue architecture with the team.
- Implement and test the agreed email delivery provider.
- Confirm the producer's event schema and supported notification types.
- Configure retry and dead-letter queue behavior.
- Consider duplicate-event prevention and idempotency.
- Add production monitoring and deployment configuration as required.

## Branch workflow

Current development branch: `feature/notification-delivery`.

Run tests before committing changes. Push tested changes to the feature branch. Follow the team's review and pull-request process when requested.