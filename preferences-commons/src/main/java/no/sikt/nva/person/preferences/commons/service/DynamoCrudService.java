package no.sikt.nva.person.preferences.commons.service;

import no.sikt.nva.person.preferences.commons.model.DataAccessClass;
import no.sikt.nva.person.preferences.commons.model.DataAccessService;
import nva.commons.apigateway.exceptions.NotFoundException;
import nva.commons.core.JacocoGenerated;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;
import java.util.stream.Stream;

public class DynamoCrudService<T extends DataAccessClass<T>> implements DataAccessService<T> {
    private final DynamoDbTable<T> table;
    private final DynamoDbEnhancedClient enhancedClient;

    @JacocoGenerated
    public DynamoCrudService(String tableName, Class<T> tClass) {
        this(DynamoDbClient.create(), tableName, tClass);
    }

    public DynamoCrudService(DynamoDbClient dynamoDbClient, String tableName, Class<T> tClass) {
        this.enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.table = enhancedClient.table(tableName, TableSchema.fromImmutableClass(tClass));
    }

    @Override
    public void persist(T item) {
        table.putItem(item);
    }

    @SafeVarargs
    @Override
    public final void transactionalPersist(T... items) {
        var transBuilder =
                TransactWriteItemsEnhancedRequest.builder();
        Stream.ofNullable(items)
                .flatMap(Stream::of)
                .forEach(item -> transBuilder.addPutItem(table, item));
        enhancedClient.transactWriteItems(transBuilder.build());

    }

    @Override
    public void delete(T item) throws NotFoundException {
        Optional.ofNullable(table.deleteItem(item))
                .orElseThrow(() -> new NotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
    }

    @Override
    public T fetch(T item) throws NotFoundException {
        Key key = Key.builder().partitionValue(item.personId().toString()).sortValue(item.withType()).build();
        return Optional.ofNullable(table.getItem(r -> r.key(key)))
                .orElseThrow(() -> new NotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
    }
}

