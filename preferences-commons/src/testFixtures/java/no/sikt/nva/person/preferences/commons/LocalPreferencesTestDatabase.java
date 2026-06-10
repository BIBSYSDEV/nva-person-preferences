package no.sikt.nva.person.preferences.commons;

import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.dynamodb.services.local.embedded.DynamoDBEmbedded;

import static no.sikt.nva.person.preferences.commons.PersonPreferencesTransactionConstants.PRIMARY_PARTITION_KEY;


public class LocalPreferencesTestDatabase {

    protected DynamoDbClient client;

    public void init(String tableName) {
        client = DynamoDBEmbedded.create(null, true).dynamoDbClient();
        client.createTable(createTableRequest(tableName));
    }

    @AfterEach
    public void shutdown() {
        client.close();
    }

    private CreateTableRequest createTableRequest(String tableName) {
        return CreateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions(newAttribute())
                .keySchema(newKeyElement())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();
    }

    private KeySchemaElement newKeyElement() {
        return KeySchemaElement.builder()
                .attributeName(PRIMARY_PARTITION_KEY)
                .keyType(KeyType.HASH)
                .build();
    }

    private AttributeDefinition newAttribute() {
        return AttributeDefinition.builder()
                .attributeName(PRIMARY_PARTITION_KEY)
                .attributeType(ScalarAttributeType.S)
                .build();
    }
}
