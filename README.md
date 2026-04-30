# CloudBridge

CloudBridge is a cloud-agnostic Spring Boot starter for messaging and basic storage.
It reduces lock-in by giving application code one portable API for queues, listeners, retry handling, and key-value storage across AWS, Azure, GCP, and OCI.

## What It Solves

- Provider-specific listener annotations and SDK calls
- Rewriting queue and pub/sub code during migrations
- Inconsistent retry, acknowledgement, and DLQ behavior
- Duplicated integration code across teams and services

## Supported Versions

- Java 21
- Spring Boot starter baseline targeted at Java 21 projects

## Quick Start

1. Add the CloudBridge starter to your Spring Boot project.
2. Select a provider in configuration.
3. Use the portable messaging API and `@QueueListener` in application code.

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

## Architecture

```mermaid
flowchart LR
  App[Spring Boot App] --> CB[CloudBridge Starter]
  CB --> MQ[Messaging Core]
  CB --> KV[KeyValueStore]
  MQ --> Listener[@QueueListener]
  MQ --> Client[QueueClient]
  CB --> AWS[AWS Adapter]
  CB --> AZ[Azure Adapter]
  CB --> GCP[GCP Adapter]
  CB --> OCI[OCI Adapter]
  AWS --> SQS[SQS / SNS / DynamoDB]
  AZ --> SB[Service Bus]
  GCP --> PS[Pub/Sub]
  OCI --> OQS[OCI Queue]
```

## Example Docs

- [Examples hub](docs/examples/README.md)
- [AWS example](docs/examples/aws-example.md)
- [Multi-cloud example](docs/examples/multi-cloud-example.md)
- [Blog draft](docs/blog/cloud-bridge-cloud-agnostic-spring-boot-starter-draft.md)

## Keywords

- spring boot multi cloud
- cloud agnostic java
- aws sqs alternative
- multi-cloud messaging
- portable queue listener
- Spring Boot starter
