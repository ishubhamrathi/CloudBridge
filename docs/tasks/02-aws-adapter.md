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
