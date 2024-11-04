package no.sikt.nva.person.preferences.commons.service;

import no.sikt.nva.person.preferences.commons.model.DataAccessClass;
import no.sikt.nva.person.preferences.commons.model.DataAccessService;
import nva.commons.apigateway.exceptions.NotFoundException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class IndexService<T extends DataAccessClass<T>> implements DataAccessService<T> {
    private final DynamoDbTable<T> table;

    public IndexService(String tableName, Class<T> tClass) {
        this(DynamoDbClient.create(), tableName, tClass);
    }

    public IndexService(DynamoDbClient dynamoDbClient, String tableName, Class<T> tClass) {
        var enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
//        this.serviceWithTransactions = new ServiceWithTransactions(enhancedClient, tableName);

        this.table = enhancedClient.table(tableName, TableSchema.fromImmutableClass(tClass));
    }

    @Override
    public void persist(T item) {
        table.putItem(item);
    }

    @Override
    public T fetch(T item) throws NotFoundException {
        Key key = Key.builder()
                .partitionValue(item.withId().toString())
                .sortValue(item.withType())
                .build();
        return table.getItem(r -> r.key(key));
    }
}

