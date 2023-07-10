package no.unit.nva.person.preferences.commons.utils;

import static no.unit.nva.person.preferences.commons.service.ServiceWithTransactions.PRIMARY_PARTITION_KEY;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.BillingMode;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.AfterEach;

public class UserProfileLocalTestDatabase {

    protected AmazonDynamoDB client;

    public void init(String tableName) {
        client = DynamoDBEmbedded.create().amazonDynamoDB();
        CreateTableRequest request = createTableRequest(tableName);
        client.createTable(request);
    }

    @AfterEach
    public void shutdown() {
        client.shutdown();
    }

    private CreateTableRequest createTableRequest(String tableName) {
        return new CreateTableRequest()
                   .withTableName(tableName)
                   .withAttributeDefinitions(attributeDefinitions())
                   .withKeySchema(primaryKeySchema())
                   .withBillingMode(BillingMode.PAY_PER_REQUEST);
    }

    private AttributeDefinition[] attributeDefinitions() {
        List<AttributeDefinition> attributesList = new ArrayList<>();
        attributesList.add(newAttribute(PRIMARY_PARTITION_KEY, ScalarAttributeType.S));
        AttributeDefinition[] attributesArray = new AttributeDefinition[attributesList.size()];
        attributesList.toArray(attributesArray);
        return attributesArray;
    }

    private Collection<KeySchemaElement> primaryKeySchema() {
        return keySchema(PRIMARY_PARTITION_KEY);
    }

    private Collection<KeySchemaElement> keySchema(String hashKey) {
        List<KeySchemaElement> primaryKey = new ArrayList<>();
        primaryKey.add(newKeyElement(hashKey, KeyType.HASH));
        return primaryKey;
    }

    private KeySchemaElement newKeyElement(String primaryKeySortKeyName, KeyType range) {
        return new KeySchemaElement().withAttributeName(primaryKeySortKeyName).withKeyType(range);
    }

    private AttributeDefinition newAttribute(String keyName, ScalarAttributeType type) {
        return new AttributeDefinition()
                   .withAttributeName(keyName)
                   .withAttributeType(type);
    }
}
