package no.unit.nva.person.preferences.commons.service;

import static java.util.Objects.isNull;
import static no.unit.nva.person.preferences.commons.service.ServiceWithTransactions.PRIMARY_PARTITION_KEY;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest;
import com.amazonaws.services.kms.model.NotFoundException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import no.unit.nva.person.preferences.commons.model.Profile;
import no.unit.nva.person.preferences.commons.model.ProfileDao;

public class ProfileService {

    public static final String TABLE_NAME = "user-profile";
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "Could not find promoted publications";
    private final AmazonDynamoDB client;
    private final ServiceWithTransactions serviceWithTransactions;

    public ProfileService(AmazonDynamoDB client) {
        this.serviceWithTransactions = new ServiceWithTransactions(client, TABLE_NAME);
        this.client = client;
    }

    public Profile createProfile(Profile profile) {
        var profileWithTimeStamps = injectCreatedTimeStamp(profile);
        var transactionItem = serviceWithTransactions.newPutTransactionItem(profileWithTimeStamps.toDao());
        var request = new TransactWriteItemsRequest().withTransactItems(List.of(transactionItem));
        serviceWithTransactions.sendTransactionWriteRequest(request);
        return fetchUserPreferences(profile);
    }

    public Profile updateProfile(Profile profile) {
        var profileWithTimeStamps = injectModifiedTimeStamp(profile);
        var transactionItem = serviceWithTransactions.updatePutTransactionItem(profileWithTimeStamps.toDao());
        var request = new TransactWriteItemsRequest().withTransactItems(List.of(transactionItem));
        serviceWithTransactions.sendTransactionWriteRequest(request);
        return fetchUserPreferences(profile);
    }

    public Profile getProfileByIdentifier(URI identifier) {
        var dao = new ProfileDao(new Profile(identifier, null, null, null));
        return new ProfileDao.Builder()
                   .fromDynamoFormat(getResourceByPrimaryKey(dao.toDynamoFormat()))
                   .build()
                   .profile();
    }

    private static Profile injectCreatedTimeStamp(Profile profile) {
        return profile.copy()
                   .withCreated(Instant.now())
                   .withModified(Instant.now())
                   .build();
    }

    private static Profile injectModifiedTimeStamp(Profile profile) {
        return profile.copy().withModified(Instant.now()).build();
    }

    private Map<String, AttributeValue> primaryKey(Profile userPreferences) {
        return Map.of(PRIMARY_PARTITION_KEY, new AttributeValue(userPreferences.identifier().toString()));
    }

    private Profile fetchUserPreferences(Profile profile) {
        var primaryKey = primaryKey(profile);
        return new ProfileDao.Builder()
                   .fromDynamoFormat(getResourceByPrimaryKey(primaryKey))
                   .build().profile();
    }

    private Map<String, AttributeValue> getResourceByPrimaryKey(Map<String, AttributeValue> primaryKey) {
        var result = client.getItem(new GetItemRequest()
                                        .withTableName(TABLE_NAME)
                                        .withKey(primaryKey));
        if (isNull(result.getItem())) {
            throw new NotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
        }
        return result.getItem();
    }
}
