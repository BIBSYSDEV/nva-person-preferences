package no.sikt.nva.person.preferences.commons.service;

import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import nva.commons.apigateway.exceptions.NotFoundException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static no.sikt.nva.person.preferences.commons.PersonPreferencesTransactionConstants.PRIMARY_PARTITION_KEY;

public class PersonPreferencesService {

    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Could not find person preferences";
    private final DynamoDbClient client;
    private final ServiceWithTransactions serviceWithTransactions;
    private final String tableName;

    public PersonPreferencesService(DynamoDbClient client, String tableName) {
        this.tableName = tableName;
        this.serviceWithTransactions = new ServiceWithTransactions(client, tableName);
        this.client = client;
    }

    private static PersonPreferencesDao injectCreatedTimeStamp(PersonPreferencesDao personPreferencesDao) {
        return personPreferencesDao.copy()
                .withCreatedDate(Instant.now())
                .withModifiedDate(Instant.now())
                .build();
    }

    private static PersonPreferencesDao injectModifiedTimeStamp(PersonPreferences personPreferences,
                                                                PersonPreferencesDao profile) {
        return personPreferences.toDao().copy()
                .withCreatedDate(profile.created())
                .withModifiedDate(Instant.now())
                .build();
    }

    public PersonPreferences upsertPreferences(PersonPreferences personPreferences) throws NotFoundException {
        var primaryKey = primaryKey(personPreferences);
        var persistedDao = client.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(primaryKey)
                .build());
        return persistedDao.hasItem()
                ? updatePreferences(personPreferences)
                : createPreferences(personPreferences);
    }

    public PersonPreferences fetchPreferences(PersonPreferences personPreferences) throws NotFoundException {
        var dao = fetchPersonPreferences(personPreferences);
        return new PersonPreferences.Builder()
                .withPersonId(dao.personId())
                .withPromotedPublications(dao.promotedPublications())
                .build();
    }

    private PersonPreferences createPreferences(PersonPreferences personPreferences) throws NotFoundException {
        var dao = injectCreatedTimeStamp(personPreferences.toDao());
        var transactionItem = serviceWithTransactions.newPutTransactionItem(dao);
        var request = TransactWriteItemsRequest.builder().transactItems(List.of(transactionItem)).build();
        serviceWithTransactions.sendTransactionWriteRequest(request);
        return new PersonPreferences.Builder().fromDao(fetchPersonPreferences(personPreferences));
    }

    private PersonPreferences updatePreferences(PersonPreferences personPreferences) throws NotFoundException {
        var persistedDao = fetchPersonPreferences(personPreferences);
        var dao = injectModifiedTimeStamp(personPreferences, persistedDao);
        var transactionItem = serviceWithTransactions.updatePutTransactionItem(dao);
        var request = TransactWriteItemsRequest.builder().transactItems(List.of(transactionItem)).build();
        serviceWithTransactions.sendTransactionWriteRequest(request);
        return new PersonPreferences.Builder().fromDao(fetchPersonPreferences(personPreferences));
    }

    private Map<String, AttributeValue> primaryKey(PersonPreferences userPreferences) {
        return Map.of(PRIMARY_PARTITION_KEY, AttributeValue.fromS(userPreferences.personId().toString()));
    }

    private PersonPreferencesDao fetchPersonPreferences(PersonPreferences personPreferences) throws NotFoundException {
        var primaryKey = primaryKey(personPreferences);
        return new PersonPreferencesDao.Builder()
                .fromDynamoFormat(getResourceByPrimaryKey(primaryKey));
    }

    private Map<String, AttributeValue> getResourceByPrimaryKey(Map<String, AttributeValue> primaryKey)
            throws NotFoundException {
        var result = client.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(primaryKey)
                .build());
        if (!result.hasItem()) {
            throw new NotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
        }
        return result.item();
    }
}
