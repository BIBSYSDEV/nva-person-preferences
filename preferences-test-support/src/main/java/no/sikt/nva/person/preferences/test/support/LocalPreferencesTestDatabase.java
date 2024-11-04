package no.sikt.nva.person.preferences.test.support;


import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class LocalPreferencesTestDatabase {

    protected static DynamoDbClient client;

    public void init(String tableName) {
        client = DynamoDbTestClientProvider.geClient();
        new DynamoDbTableCreator(client).createTable(tableName);
    }

    @AfterEach
    public void shutdown() {
//        client.close();
    }

//    private CreateTableRequest createTableRequest(String tableName) {
//        return new CreateTableRequest()
//                .withTableName(tableName)
//                .withAttributeDefinitions(primaryKeyAttribute(), typeAttribute())
//                .withKeySchema(primaryKeySchema())
//                .withGlobalSecondaryIndexes(typeSecondaryIndex())
//                .withBillingMode(BillingMode.PAY_PER_REQUEST);
//    }
//
//    private GlobalSecondaryIndex typeSecondaryIndex() {
//        return new GlobalSecondaryIndex().withIndexName(TYPE)
//                .withProvisionedThroughput(getProvisionedThroughput())
//                .withKeySchema(typeSchema())
//                .withProjection(new Projection().withProjectionType("KEYS_ONLY"));
//    }

//    private ProvisionedThroughput getProvisionedThroughput() {
//        return new ProvisionedThroughput()
//                .withReadCapacityUnits(1L)
//                .withWriteCapacityUnits(1L);
//    }
//
//    private KeySchemaElement primaryKeySchema() {
//        return new KeySchemaElement()
//                .withAttributeName(PRIMARY_PARTITION_KEY)
//                .withKeyType(KeyType.HASH);
//    }
//
//    private KeySchemaElement typeSchema() {
//        return new KeySchemaElement()
//                .withAttributeName(TYPE)
//                .withKeyType(KeyType.HASH);
//    }
//
//    private AttributeDefinition typeAttribute() {
//        return new AttributeDefinition()
//                .withAttributeName(TYPE)
//                .withAttributeType(ScalarAttributeType.S);
//    }
//
//    private AttributeDefinition primaryKeyAttribute() {
//        return new AttributeDefinition()
//                .withAttributeName(PRIMARY_PARTITION_KEY)
//                .withAttributeType(ScalarAttributeType.S);
//    }
}
