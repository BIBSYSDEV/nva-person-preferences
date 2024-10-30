package no.sikt.nva.person.preferences.commons.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest;
import no.sikt.nva.person.preferences.commons.model.Persistable;
import nva.commons.apigateway.exceptions.NotFoundException;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static no.sikt.nva.person.preferences.storage.PersonPreferencesTransactionConstants.PRIMARY_PARTITION_KEY;

public class PersonService {

    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Could not find person preferences";
    private static final String CREATED = "created";
    private final AmazonDynamoDB client;
    private final ServiceWithTransactions serviceWithTransactions;
    private final String tableName;

    public PersonService(AmazonDynamoDB client, String tableName) {
        this.tableName = tableName;
        this.serviceWithTransactions = new ServiceWithTransactions(client, tableName);
        this.client = client;
    }

    public Map<String, AttributeValue> fetchResource(Persistable<?> person)
        throws NotFoundException {
        var primaryKey = primaryKey(person.toDynamoFormat());
        var result = client.getItem(new GetItemRequest()
            .withTableName(tableName)
            .withKey(primaryKey));
        if (isNull(result.getItem())) {
            throw new NotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
        }
        return result.getItem();
    }

    public Map<String, AttributeValue> upsert(Persistable<?> person) throws NotFoundException {
        var primaryKey = primaryKey(person.toDynamoFormat());
        var persistedDao = client.getItem(new GetItemRequest()
            .withTableName(tableName)
            .withKey(primaryKey));
        return isNull(persistedDao.getItem())
            ? create(person)
            : update(person);
    }

    private Map<String, AttributeValue> create(Persistable<?> person) throws NotFoundException {
        var transactionItems = List.of(
            serviceWithTransactions.newPutTransactionItem(person.toDynamoFormat())
        );
        var request = new TransactWriteItemsRequest()
            .withTransactItems(transactionItems);
        serviceWithTransactions.sendTransactionWriteRequest(request);
        return fetchResource(person);
    }

    private Map<String, AttributeValue> update(Persistable<?> person) throws NotFoundException {
        var created = fetchResource(person).get(CREATED);
        var toBePersisted = person.toDynamoFormat();
        toBePersisted.merge(CREATED, created, (a, b) -> b);
        var transactionItem = serviceWithTransactions.updatePutTransactionItem(toBePersisted);
        var request = new TransactWriteItemsRequest().withTransactItems(List.of(transactionItem));
        serviceWithTransactions.sendTransactionWriteRequest(request);
        return fetchResource(person);
    }

    private Map<String, AttributeValue> primaryKey(Map<String, AttributeValue> person) {
        return Map.of(PRIMARY_PARTITION_KEY, new AttributeValue(person.get(PRIMARY_PARTITION_KEY).getS()));
    }
}
