package no.sikt.nva.person.preferences.commons.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Put;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItem;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;

import java.util.Map;

import static no.sikt.nva.person.preferences.storage.PersonPreferencesTransactionConstants.PRIMARY_PARTITION_KEY;
import static nva.commons.core.attempt.Try.attempt;

public class ServiceWithTransactions {

    public static final String PARTITION_KEY_NAME_PLACEHOLDER = "#partitionKey";
    public static final String KEY_NOT_EXISTS_CONDITION = keyNotExistsCondition();
    public static final String PARTITION_KEY_EQUALITY_CONDITION = "#partitionKey=:partitionKey";
    public static final String PARTITION_KEY_VALUE_PLACEHOLDER = ":partitionKey";
    private final AmazonDynamoDB client;
    private final String tableName;

    protected ServiceWithTransactions(AmazonDynamoDB client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    private static String keyNotExistsCondition() {
        return String.format("attribute_not_exists(%s)", PARTITION_KEY_NAME_PLACEHOLDER);
    }

    protected final AmazonDynamoDB getClient() {
        return client;
    }

    protected void sendTransactionWriteRequest(TransactWriteItemsRequest transactWriteItemsRequest) {
        attempt(() -> getClient().transactWriteItems(transactWriteItemsRequest))
                .orElseThrow();
    }

    protected TransactWriteItem newPutTransactionItem(PersonPreferencesDao data) {
        return new TransactWriteItem().withPut(constructNewPut(data));
    }

    protected TransactWriteItem updatePutTransactionItem(PersonPreferencesDao data) {
        return new TransactWriteItem().withPut(constructUpdatePut(data));
    }

    private Put constructNewPut(PersonPreferencesDao dao) {
        return new Put()
                .withItem(dao.toDynamoFormat())
                .withTableName(tableName)
                .withConditionExpression(KEY_NOT_EXISTS_CONDITION)
                .withExpressionAttributeNames(Map.of(PARTITION_KEY_NAME_PLACEHOLDER, PRIMARY_PARTITION_KEY));
    }

    private Put constructUpdatePut(PersonPreferencesDao dao) {
        var expressionAttributeValues = Map.of(
                PARTITION_KEY_VALUE_PLACEHOLDER, new AttributeValue(dao.personId().toString()));
        return new Put()
                .withItem(dao.toDynamoFormat())
                .withTableName(tableName)
                .withConditionExpression(PARTITION_KEY_EQUALITY_CONDITION)
                .withExpressionAttributeNames(Map.of(PARTITION_KEY_NAME_PLACEHOLDER, PRIMARY_PARTITION_KEY))
                .withExpressionAttributeValues(expressionAttributeValues);
    }
}