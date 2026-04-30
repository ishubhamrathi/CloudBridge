# Cloud Bridge - Task Overview

## Goal

Build a cloud-agnostic Spring Boot starter that supports messaging and storage across AWS, Azure, GCP, and OCI.

## Progress Tracker

| Module | Status | Owner | Notes |
| --- | --- | --- | --- |
| Core Abstractions | Completed |  | Core API, listener annotation, and contract tests added |
| AWS Adapter | Completed |  | SQS queue client, polling consumer, and DynamoDB key-value adapter added |
| Listener Engine | Completed |  | Annotation scanner, registry, invoker, executor-backed container manager added |
| Retry + DLQ | Completed |  | Retry executor, backoff sleeper, and DLQ publisher added |
| Config System | Completed |  | Configuration properties, provider resolver, and auto-configuration added |
| Azure/GCP Adapter | Completed |  | Azure Service Bus and GCP Pub/Sub adapter implementations added |
| Storage Layer | Completed |  | Provider-neutral key-value abstraction plus AWS DynamoDB implementation added |
| Examples | Completed |  | Docs created under `docs/examples/` |
| Docs & SEO | Completed |  | README, blog draft, and Mermaid diagrams created |

## Status Legend

- Not Started
- Completed

## Contribution Flow

1. Pick a task file
2. Move status -> In Progress
3. Create PR
4. Add tests
5. Mark Completed after merge

## For AI Contributors

When working on a task:

1. Read the task file fully
2. Follow acceptance criteria strictly
3. Do not introduce provider-specific logic in core
4. Keep code minimal and extensible

## Why This Works

This structure keeps work separated by concern, makes ownership visible, and gives contributors a clear acceptance target for each milestone.
