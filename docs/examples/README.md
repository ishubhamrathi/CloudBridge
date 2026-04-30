# Examples Hub

This directory collects the example documentation for CloudBridge.
The goal is to show how the starter is used in practice before the code examples are finalized.

## What Is Included

- [AWS example](aws-example.md)
- [Multi-cloud example](multi-cloud-example.md)
- Usage guidance for the README and future sample apps

## Example Structure

```text
docs/examples/
  README.md
  aws-example.md
  multi-cloud-example.md
```

## How To Read These Docs

1. Start with the AWS example to see the provider-specific baseline.
2. Read the multi-cloud example to see how the same app switches providers by configuration.
3. Use the root README for a short overview and quick-start path.

## Current Provider Coverage

- AWS messaging and DynamoDB storage
- Azure messaging
- GCP messaging
- OCI messaging

Storage remains AWS-only in the current implementation.

## Delivery Target

The example docs are written to match the PRD scope:

- Portable messaging
- Event-driven processing
- Basic key-value storage
