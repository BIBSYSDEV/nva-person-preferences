package no.sikt.nva.person.preferences.commons.service;

import static no.sikt.nva.person.preferences.commons.PersonPreferencesTransactionConstants.PRIMARY_PARTITION_KEY;
import static nva.commons.core.attempt.Try.attempt;

import java.util.Map;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.Put;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;

public class ServiceWithTransactions {

  public static final String PARTITION_KEY_NAME_PLACEHOLDER = "#partitionKey";
  public static final String KEY_NOT_EXISTS_CONDITION = keyNotExistsCondition();
  public static final String PARTITION_KEY_EQUALITY_CONDITION = "#partitionKey=:partitionKey";
  public static final String PARTITION_KEY_VALUE_PLACEHOLDER = ":partitionKey";
  private final DynamoDbClient client;
  private final String tableName;

  protected ServiceWithTransactions(DynamoDbClient client, String tableName) {
    this.client = client;
    this.tableName = tableName;
  }

  private static String keyNotExistsCondition() {
    return String.format("attribute_not_exists(%s)", PARTITION_KEY_NAME_PLACEHOLDER);
  }

  protected final DynamoDbClient getClient() {
    return client;
  }

  protected void sendTransactionWriteRequest(TransactWriteItemsRequest transactWriteItemsRequest) {
    attempt(() -> getClient().transactWriteItems(transactWriteItemsRequest)).orElseThrow();
  }

  protected TransactWriteItem newPutTransactionItem(PersonPreferencesDao data) {
    return TransactWriteItem.builder().put(constructNewPut(data)).build();
  }

  protected TransactWriteItem updatePutTransactionItem(PersonPreferencesDao data) {
    return TransactWriteItem.builder().put(constructUpdatePut(data)).build();
  }

  private Put constructNewPut(PersonPreferencesDao dao) {
    return Put.builder()
        .item(dao.toDynamoFormat())
        .tableName(tableName)
        .conditionExpression(KEY_NOT_EXISTS_CONDITION)
        .expressionAttributeNames(Map.of(PARTITION_KEY_NAME_PLACEHOLDER, PRIMARY_PARTITION_KEY))
        .build();
  }

  private Put constructUpdatePut(PersonPreferencesDao dao) {
    var expressionAttributeValues =
        Map.of(PARTITION_KEY_VALUE_PLACEHOLDER, AttributeValue.fromS(dao.personId().toString()));
    return Put.builder()
        .item(dao.toDynamoFormat())
        .tableName(tableName)
        .conditionExpression(PARTITION_KEY_EQUALITY_CONDITION)
        .expressionAttributeNames(Map.of(PARTITION_KEY_NAME_PLACEHOLDER, PRIMARY_PARTITION_KEY))
        .expressionAttributeValues(expressionAttributeValues)
        .build();
  }
}
