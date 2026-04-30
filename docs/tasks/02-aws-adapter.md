# Task: AWS Adapter

## Goal

Implement AWS support using SQS, SNS, DynamoDB

## Deliverables

- `AwsQueueClient`
- SQS send implementation
- SQS receive polling
- Basic DLQ support
- Config mapping

## Dependencies

- AWS SDK v2

## Requirements

- Map `CloudMessage` -> SQS message
- Support `SendOptions` (delay if possible)
- Graceful failure handling

## Acceptance Criteria

- Can send/receive messages locally (LocalStack optional)
- Works with core interfaces

## Delivery Notes

- `AwsQueueClient` maps `CloudMessage` headers to SQS message attributes
- `AwsQueueConsumer` performs SQS polling and hands messages to the listener engine
- `AwsSqsAcknowledgement` deletes messages on ack and leaves them for redelivery on nack
- `AwsKeyValueStore` provides the DynamoDB-backed storage implementation used by Task 7
