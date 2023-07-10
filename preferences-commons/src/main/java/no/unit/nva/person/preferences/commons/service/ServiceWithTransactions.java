package no.unit.nva.person.preferences.commons.service;

import static nva.commons.core.attempt.Try.attempt;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Put;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItem;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest;
import java.util.Map;
import no.unit.nva.person.preferences.commons.model.ProfileDao;

public class ServiceWithTransactions {

    public static final String PARTITION_KEY_NAME_PLACEHOLDER = "#partitionKey";
    public static final String KEY_NOT_EXISTS_CONDITION = keyNotExistsCondition();
    public static final String PRIMARY_PARTITION_KEY = "identifier";
    public static final String PARTITION_KEY_EQUALITY_CONDITION = "#partitionKey=:partitionKey";
    public static final String PARTITION_KEY_VALUE_PLACEHOLDER = ":partitionKey";
    private final AmazonDynamoDB client;
    private final String tableName;

    protected ServiceWithTransactions(AmazonDynamoDB client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    protected final AmazonDynamoDB getClient() {
        return client;
    }

    protected void sendTransactionWriteRequest(TransactWriteItemsRequest transactWriteItemsRequest) {
        attempt(() -> getClient().transactWriteItems(transactWriteItemsRequest))
            .orElseThrow();
    }

    protected TransactWriteItem newPutTransactionItem(ProfileDao data) {
        return new TransactWriteItem().withPut(constructNewPut(data));
    }

    protected TransactWriteItem updatePutTransactionItem(ProfileDao data) {
        return new TransactWriteItem().withPut(constructUpdatePut(data));
    }

    private static String keyNotExistsCondition() {
        return String.format("attribute_not_exists(%s)", PARTITION_KEY_NAME_PLACEHOLDER);
    }

    private Put constructNewPut(ProfileDao dao) {
        return new Put()
                   .withItem(dao.toDynamoFormat())
                   .withTableName(tableName)
                   .withConditionExpression(KEY_NOT_EXISTS_CONDITION)
                   .withExpressionAttributeNames(Map.of(PARTITION_KEY_NAME_PLACEHOLDER, PRIMARY_PARTITION_KEY));
    }

    private Put constructUpdatePut(ProfileDao dao) {
        var expressionAttributeValues = Map.of(
            PARTITION_KEY_VALUE_PLACEHOLDER, new AttributeValue(dao.profile().identifier().toString()));
        return new Put()
                   .withItem(dao.toDynamoFormat())
                   .withTableName(tableName)
                   .withConditionExpression(PARTITION_KEY_EQUALITY_CONDITION)
                   .withExpressionAttributeNames(Map.of(PARTITION_KEY_NAME_PLACEHOLDER, PRIMARY_PARTITION_KEY))
                   .withExpressionAttributeValues(expressionAttributeValues);
    }
}