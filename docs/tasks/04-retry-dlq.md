# Task: Retry + DLQ

## Goal

Provide unified retry + dead-letter handling

## Deliverables

- Retry policy engine
- Backoff support
- DLQ routing logic

## Config Example

```yaml
cloud:
  messaging:
    retry:
      maxAttempts: 3
      backoffMs: 2000
```

## Requirements

- Work even if provider doesn't support native DLQ
- Idempotency-safe design

## Acceptance Criteria

- Failed messages retry
- After max attempts -> DLQ
