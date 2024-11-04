package no.sikt.nva.person.preferences.commons.model;

import nva.commons.apigateway.exceptions.NotFoundException;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;

import java.net.URI;
import java.time.Instant;

public interface DataAccessClass<T extends DataAccessClass<T>> {

    URI withId();

    String withType();

    Instant created();

    Instant modified();

    @DynamoDbIgnore
    T upsert(DataAccessService<T> service) throws NotFoundException;

    @DynamoDbIgnore
    T fetch(DataAccessService<T> service) throws NotFoundException;


}
