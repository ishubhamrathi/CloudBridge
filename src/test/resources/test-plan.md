# Cloud Bridge Test Plan

## Covered now

- `CloudMessage` validation, immutability, and snapshot semantics
- `SendOptions` validation
- `QueueClient` default overload behavior
- `QueueListener` runtime retention and method targeting
- `Acknowledgement` and `CloudCapabilities` interface contracts
- `QueueListenerMethodInvoker` argument binding and unsupported-parameter rejection
- `RetryExecutor` retry, backoff, ack, nack, and DLQ flow

## Next when external-provider verification is possible

- AWS adapter: SQS mapping, delay handling, receive polling, graceful failure behavior
- Listener engine: annotation scanning, registry wiring, concurrency, dispatch, ack/nack flow
- Retry + DLQ: retry policy, backoff, dead-letter routing
- Configuration system: provider resolution, override behavior, auto-configuration
- Azure/GCP adapter: parity tests for send/receive contracts
- Storage layer: key-value CRUD against provider adapter
- Examples: smoke tests for runnable demos

## Notes

- No production APIs were invented for this plan file.
- Future task tests should be added only after the matching production types exist.
