# Task: Core Foundation

## Goal

Define all core interfaces and annotations (NO cloud-specific logic)

## Deliverables

- `QueueClient` interface
- `CloudMessage` model
- `SendOptions`
- `@QueueListener` annotation
- `Acknowledgement` interface
- `CloudCapabilities` interface

## Requirements

- Must not depend on any cloud SDK
- Keep API minimal and stable
- Kotlin/Java interoperability

## Example

```kotlin
interface QueueClient {
    fun send(queue: String, message: CloudMessage, options: SendOptions? = null)
}
```

## Acceptance Criteria

- Clean API
- No provider-specific logic
- Unit tests added
