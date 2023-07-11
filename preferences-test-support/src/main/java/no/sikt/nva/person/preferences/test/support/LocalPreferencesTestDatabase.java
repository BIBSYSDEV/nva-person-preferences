package no.sikt.nva.person.preferences.test.support;

import static no.sikt.nva.person.preferences.storage.PreferencesTransactionConstants.PRIMARY_PARTITION_KEY;
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

public class LocalPreferencesTestDatabase {

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
        attributesList.add(newAttribute());
        AttributeDefinition[] attributesArray = new AttributeDefinition[attributesList.size()];
        attributesList.toArray(attributesArray);
        return attributesArray;
    }

    private Collection<KeySchemaElement> primaryKeySchema() {
        return keySchema();
    }

    private Collection<KeySchemaElement> keySchema() {
        return List.of(newKeyElement());
    }

    private KeySchemaElement newKeyElement() {
        return new KeySchemaElement()
                   .withAttributeName(PRIMARY_PARTITION_KEY)
                   .withKeyType(KeyType.HASH);
    }

    private AttributeDefinition newAttribute() {
        return new AttributeDefinition()
                   .withAttributeName(PRIMARY_PARTITION_KEY)
                   .withAttributeType(ScalarAttributeType.S);
    }
}
