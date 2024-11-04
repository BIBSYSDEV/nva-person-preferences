package no.sikt.nva.person.preferences.commons.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import nva.commons.apigateway.exceptions.NotFoundException;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static no.sikt.nva.person.preferences.commons.model.DataAccessService.RESOURCE_NOT_FOUND_MESSAGE;
import static no.sikt.nva.person.preferences.commons.model.DataAccessService.WITH_TYPE;

@DynamoDbImmutable(builder = PersonPreferencesDao.Builder.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = WITH_TYPE, visible = true)
@JsonTypeName(PersonPreferencesDao.PROMOTED_PUBLICATIONS)
public record PersonPreferencesDao(
        @DynamoDbPartitionKey
        URI withId,
        @DynamoDbSortKey
        String withType,
        Instant created,
        Instant modified,
        List<URI> promotedPublications
) implements DataAccessClass<PersonPreferencesDao> {

    public static final String PROMOTED_PUBLICATIONS = "PromotedPublications";

    public PersonPreferencesDao(Builder builder) {
        this(
                builder.withId,
                builder.withType,
                builder.created,
                builder.modified,
                builder.promotedPublications
        );
    }

    @DynamoDbIgnore
    public PersonPreferencesDto toDto() {
        return new PersonPreferencesDto.Builder().fromDao(this);
    }

    @DynamoDbIgnore
    @Override
    public PersonPreferencesDao upsert(DataAccessService<PersonPreferencesDao> service) throws NotFoundException {
        service.persist(this);
        return fetch(service);
    }

    @DynamoDbIgnore
    @Override
    public PersonPreferencesDao fetch(DataAccessService<PersonPreferencesDao> service) throws NotFoundException {
        return Optional.ofNullable(service.fetch(this))
                .orElseThrow(() -> new NotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
    }

    public static Builder builder() {
        return new PersonPreferencesDao.Builder();
    }

    public static class Builder {
        private URI withId;
        private String withType; //= "PromotedPublications";
        private List<URI> promotedPublications;
        private Instant created;
        private Instant modified;

        private Builder() {}

        public Builder withId(URI withId) {
            this.withId = withId;
            return this;
        }

        public Builder withType(String withType) {
            this.withType = withType;
            return this;
        }

        public Builder promotedPublications(List<URI> promotedPublications) {
            this.promotedPublications = promotedPublications;
            return this;
        }

        public Builder created(Instant created) {
            this.created = created;
            return this;
        }

        public Builder modified(Instant modified) {
            this.modified = modified;
            return this;
        }

        public PersonPreferencesDao build() {
            if (created == null) {
                created(Instant.now());
            }
            if (modified == null) {
                modified(created);
            }
            if (withType == null) {
                withType("PromotedPublications");
            }
            return new PersonPreferencesDao(this);
        }
    }
}
