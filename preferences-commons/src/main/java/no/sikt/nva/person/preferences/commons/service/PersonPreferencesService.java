package no.sikt.nva.person.preferences.commons.service;

import static java.util.Objects.isNull;
import static no.sikt.nva.person.preferences.storage.PersonPreferencesTransactionConstants.PRIMARY_PARTITION_KEY;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import nva.commons.apigateway.exceptions.NotFoundException;

public class PersonPreferencesService {

    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Could not find person preferences";
    private final AmazonDynamoDB client;
    private final ServiceWithTransactions serviceWithTransactions;
    private final String tableName;

    public PersonPreferencesService(AmazonDynamoDB client, String tableName) {
        this.tableName = tableName;
        this.serviceWithTransactions = new ServiceWithTransactions(client, tableName);
        this.client = client;
    }

    public PersonPreferences upsertPreferences(PersonPreferences personPreferences) throws NotFoundException {
        var primaryKey = primaryKey(personPreferences);
        var persistedDao = client.getItem(new GetItemRequest()
                                              .withTableName(tableName)
                                              .withKey(primaryKey));
        return isNull(persistedDao.getItem())
                   ? createPreferences(personPreferences)
                   : updatePreferences(personPreferences);
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
        var request = new TransactWriteItemsRequest().withTransactItems(List.of(transactionItem));
        serviceWithTransactions.sendTransactionWriteRequest(request);
        return new PersonPreferences.Builder().fromDao(fetchPersonPreferences(personPreferences));
    }

    private PersonPreferences updatePreferences(PersonPreferences personPreferences) throws NotFoundException {
        var persistedDao = fetchPersonPreferences(personPreferences);
        var dao = injectModifiedTimeStamp(personPreferences, persistedDao);
        var transactionItem = serviceWithTransactions.updatePutTransactionItem(dao);
        var request = new TransactWriteItemsRequest().withTransactItems(List.of(transactionItem));
        serviceWithTransactions.sendTransactionWriteRequest(request);
        return new PersonPreferences.Builder().fromDao(fetchPersonPreferences(personPreferences));
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

    private Map<String, AttributeValue> primaryKey(PersonPreferences userPreferences) {
        return Map.of(PRIMARY_PARTITION_KEY, new AttributeValue(userPreferences.personId().toString()));
    }

    private PersonPreferencesDao fetchPersonPreferences(PersonPreferences personPreferences) throws NotFoundException {
        var primaryKey = primaryKey(personPreferences);
        return new PersonPreferencesDao.Builder()
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
