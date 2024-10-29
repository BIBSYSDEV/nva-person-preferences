package no.sikt.nva.person.preferences.commons.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest;
import no.sikt.nva.person.preferences.commons.model.PersonDao;
import nva.commons.apigateway.exceptions.NotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static no.sikt.nva.person.preferences.storage.PersonPreferencesTransactionConstants.PRIMARY_PARTITION_KEY;

public class PersonService<D extends PersonDao> {

    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Could not find person preferences";
    private final AmazonDynamoDB client;
    private final ServiceWithTransactions serviceWithTransactions;
    private final String tableName;

    public PersonService(AmazonDynamoDB client, String tableName) {
        this.tableName = tableName;
        this.serviceWithTransactions = new ServiceWithTransactions(client, tableName);
        this.client = client;
    }

    public D upsert(D person) throws NotFoundException {
        var primaryKey = primaryKey(person);
        var persistedDao = client.getItem(new GetItemRequest()
                                              .withTableName(tableName)
                                              .withKey(primaryKey));
        return isNull(persistedDao.getItem())
                   ? create(person)
                   : update(person);
    }

    public D fetch(D person) throws NotFoundException {

        return new D.Builder()

                .fromDao(fetchR(person));
    }

    private D create(D person) throws NotFoundException {
        var dao = injectCreatedTimeStamp(person.toDao());
        var transactionItem = serviceWithTransactions.newPutTransactionItem(dao);
        var request = new TransactWriteItemsRequest().withTransactItems(List.of(transactionItem));
        serviceWithTransactions.sendTransactionWriteRequest(request);
        return new D.Builder()
                .fromDao(fetchR(person));
    }

    private R update(R person) throws NotFoundException {
        var persistedDao = fetchR(person);
        var dao = injectModifiedTimeStamp(person, persistedDao);
        var transactionItem = serviceWithTransactions.updatePutTransactionItem(dao);
        var request = new TransactWriteItemsRequest().withTransactItems(List.of(transactionItem));
        serviceWithTransactions.sendTransactionWriteRequest(request);
        return new R.Builder()
                .fromDao(fetchR(person));
    }

    private static D injectCreatedTimeStamp(D personPreferencesDao) {
        return personPreferencesDao.copy()
            .withCreatedDate(Instant.now())
            .withModifiedDate(Instant.now())
            .build();
    }

    private static D injectModifiedTimeStamp(R person,
                                                                RDao profile) {
        return person.toDao().copy()
            .withCreatedDate(profile.created())
            .withModifiedDate(Instant.now())
            .build();
    }

    private Map<String, AttributeValue> primaryKey(R userPreferences) {
        return Map.of(PRIMARY_PARTITION_KEY, new AttributeValue(userPreferences.personId().toString()));
    }

    private D fetchR(R person) throws NotFoundException {
        var primaryKey = primaryKey(person);
        return new RDao.Builder()
            .fromDynamoFormat(getResourceByPrimaryKey(primaryKey));
    }

    private Map<String, AttributeValue> getResourceByPrimaryKey(Map<String, AttributeValue> primaryKey)
        throws NotFoundException {
        var result = client.getItem(new GetItemRequest()
                                        .withTableName(tableName)
                                        .withKey(primaryKey));
        if (isNull(result.getItem())) {
            throw new NotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
        }
        return result.getItem();
    }
}
