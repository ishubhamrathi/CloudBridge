# Task: Configuration System

## Goal

Centralize config and provider selection

## Deliverables

- Config classes
- Provider resolver
- Auto-configuration

## Example

```yaml
cloud:
  provider: aws
```

## Requirements

- Spring Boot compatible
- Clean override system

## Acceptance Criteria

- Switching provider via config works

## Delivery Notes

- `CloudBridgeProperties` binds `cloud.*` configuration for provider, messaging, and storage
- `ProviderResolver` exposes the selected provider to the runtime
- `CloudBridgeAutoConfiguration` registers auto-configured beans for AWS, Azure, and GCP
