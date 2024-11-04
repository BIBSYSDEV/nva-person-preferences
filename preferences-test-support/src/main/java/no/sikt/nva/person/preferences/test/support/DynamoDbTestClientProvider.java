package no.sikt.nva.person.preferences.test.support;

import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.*;

import java.net.URI;

public class DynamoDbTestClientProvider {

    private static final DynamoDbClient DYNAMO_DB_CLIENT = DynamoDbClient.builder()
            .endpointOverride(URI.create("http://localhost:8000"))
            // Use dummy credentials when connecting to local DynamoDB
            .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("dummy-access-key", "dummy-secret-key")))
            .region(Region.US_EAST_1) // Region must be specified but can be any valid AWS region
            .build();

    public static DynamoDbClient geClient() {
        return  DynamoDBEmbedded.create().dynamoDbClient();
    }

}