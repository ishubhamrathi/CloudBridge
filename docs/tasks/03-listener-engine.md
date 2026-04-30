# Task: Listener Engine

## Goal

Process `@QueueListener` and connect to adapters

## Deliverables

- Annotation scanner
- Listener registry
- Thread pool execution
- Message dispatch logic

## Responsibilities

- Detect annotated methods
- Subscribe to queue
- Call method with message + ack

## Challenges

- Threading model
- Error handling
- Backpressure

## Acceptance Criteria

- Listener works end-to-end
- Supports concurrency config

## Delivery Notes

- `QueueListenerAnnotationBeanPostProcessor` scans beans for `@QueueListener`
- `QueueListenerRegistry` and `QueueListenerEndpoint` store discovered listeners
- `QueueListenerMethodInvoker` dispatches `CloudMessage`, payload `String`, and `Acknowledgement`
- `QueueListenerContainerManager` wires provider consumers to a thread-pool-backed retry flow
