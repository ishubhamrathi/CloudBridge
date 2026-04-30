# Task: Azure + GCP Adapter

## Goal

Add support for additional providers

## Deliverables

- Azure Service Bus adapter
- GCP Pub/Sub adapter

## Requirements

- Follow same interface as AWS
- Document limitations

## Acceptance Criteria

- Basic send/receive works

## Delivery Notes

- `AzureServiceBusQueueClient` and `AzureQueueConsumer` implement send/receive for Service Bus
- `GcpPubSubQueueClient` and `GcpQueueConsumer` implement send/receive for Pub/Sub
- Delay is modeled for Azure; GCP keeps the common API but does not expose provider-specific scheduling
