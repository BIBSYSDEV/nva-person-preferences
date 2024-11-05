package no.sikt.nva.person.preferences.test.support;


import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class LocalPreferencesTestDatabase {

    protected static DynamoDbClient client;

    public void init(String tableName) {
        client = DynamoDbTestClientProvider.geClient();
        new DynamoDbTableCreator(client).createTable(tableName);
    }

}
