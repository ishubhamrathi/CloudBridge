# AWS Example

This example shows the AWS-focused setup for CloudBridge.
It maps the portable messaging API to AWS services such as SQS, SNS, and DynamoDB.

## Scenario

An order service publishes events, consumes queue messages, and stores simple state in a key-value store.

## Flow

```mermaid
flowchart LR
  API[Order Service] --> CB[CloudBridge Core]
  CB --> SQS[SQS Queue]
  CB --> SNS[SNS Topic]
  CB --> DDB[DynamoDB]
  SQS --> Worker[@QueueListener]
  Worker --> Ack[Acknowledgement]
  Worker --> Retry[Retry + DLQ]
```

## Intended Setup

- Provider: `aws`
- Queue backend: `SQS`
- Optional fanout: `SNS`
- Storage backend: `DynamoDB`

## Sample Usage

```yaml
cloud:
  provider: aws
  messaging:
    retry:
      maxAttempts: 3
      backoffMs: 2000
```

```java
@QueueListener(value = "order-events", concurrency = 4)
public void handle(CloudMessage message, Acknowledgement ack) {
    process(message);
    ack.ack();
}
```

## Notes

- Keep the message contract provider-agnostic in application code.
- Use AWS-specific behavior only in the adapter layer.
- Preserve the same listener and client shape for other cloud providers.
