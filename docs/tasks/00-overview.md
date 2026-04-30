# Cloud Bridge - Task Overview

## Goal

Build a cloud-agnostic Spring Boot starter that supports messaging and storage across AWS, Azure, GCP, and OCI.

## Progress Tracker

| Module | Status | Owner | Notes |
| --- | --- | --- | --- |
| Core Abstractions | ⬜ |  |  |
| AWS Adapter | ⬜ |  |  |
| Listener Engine | ⬜ |  |  |
| Retry + DLQ | ⬜ |  |  |
| Config System | ⬜ |  |  |
| Azure/GCP Adapter | ⬜ |  |  |
| Storage Layer | ⬜ |  |  |
| Examples | ⬜ |  |  |
| Docs & SEO | ⬜ |  |  |

## Status Legend

- ⬜ Not Started
- In Progress
- ✅ Completed

## Contribution Flow

1. Pick a task file
2. Move status -> In Progress
3. Create PR
4. Add tests
5. Mark ✅ after merge

## For AI Contributors

When working on a task:

1. Read the task file fully
2. Follow acceptance criteria strictly
3. Do not introduce provider-specific logic in core
4. Keep code minimal and extensible

## Why This Works

This structure keeps work separated by concern, makes ownership visible, and gives contributors a clear acceptance target for each milestone.
