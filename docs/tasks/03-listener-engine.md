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
