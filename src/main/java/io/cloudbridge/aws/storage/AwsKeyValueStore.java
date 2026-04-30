package io.cloudbridge.aws.storage;

import io.cloudbridge.config.CloudBridgeProperties;
import io.cloudbridge.core.storage.KeyValueStore;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class AwsKeyValueStore implements KeyValueStore {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public AwsKeyValueStore(DynamoDbClient dynamoDbClient, CloudBridgeProperties properties) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = properties.getStorage().getTableName();
    }

    @Override
    public void put(String key, String value) {
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(Map.of(
                        "pk", AttributeValue.builder().s(key).build(),
                        "payload", AttributeValue.builder().s(value).build()))
                .build());
    }

    @Override
    public Optional<String> get(String key) {
        var response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("pk", AttributeValue.builder().s(key).build()))
                .build());
        if (!response.hasItem() || !response.item().containsKey("payload")) {
            return Optional.empty();
        }
        return Optional.ofNullable(response.item().get("payload").s());
    }

    @Override
    public void delete(String key) {
        dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("pk", AttributeValue.builder().s(key).build()))
                .build());
    }
}
