# Task: Storage Layer

## Goal

Add key-value abstraction

## Deliverables

- `KeyValueStore` interface
- AWS DynamoDB adapter
- Basic CRUD

## Requirements

- Keep minimal (no complex queries)

## Acceptance Criteria

- Put/get/delete works

## Delivery Notes

- `KeyValueStore` defines the provider-neutral CRUD contract
- `AwsKeyValueStore` maps the contract to DynamoDB using a simple `pk` / `payload` item shape
