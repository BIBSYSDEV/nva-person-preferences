package no.sikt.nva.person.preferences.commons.model;

import nva.commons.apigateway.exceptions.NotFoundException;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import static java.util.Objects.isNull;

@DynamoDbImmutable(builder = PersonPreferencesDao.Builder.class)
public record PersonPreferencesDao(
        @DynamoDbPartitionKey URI withId,
        @DynamoDbSortKey String withType,
        Instant created,
        Instant modified,
        List<URI> promotedPublications
) implements DataAccessClass<PersonPreferencesDao> {


    private PersonPreferencesDao(Builder builder) {
        this(
                builder.id,
                builder.type,
                builder.createdInstant,
                builder.modifiedInstant,
                builder.promotedIds
        );
    }

    public static Builder builder() {
        return new PersonPreferencesDao.Builder();
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
        return service.fetch(this);
    }

    public static class Builder {
        public static final String PROMOTED_PUBLICATIONS = "PromotedPublications";
        private URI id;
        private String type;
        private List<URI> promotedIds;
        private Instant createdInstant;
        private Instant modifiedInstant;

        private Builder() {
        }

        public Builder withId(URI withId) {
            this.id = withId;
            return this;
        }

        public Builder withType(String withType) {
            this.type = withType;
            return this;
        }

        public Builder promotedPublications(List<URI> promotedPublications) {
            this.promotedIds = promotedPublications;
            return this;
        }

        public Builder created(Instant created) {
            this.createdInstant = created;
            return this;
        }

        public Builder modified(Instant modified) {
            this.modifiedInstant = modified;
            return this;
        }

        public PersonPreferencesDao build() {
            if (isNull(modifiedInstant)) {
                modified(Instant.now());
            }
            if (isNull(createdInstant)) {
                created(modifiedInstant);
            }
            if (isNull(type)) {
                withType(PROMOTED_PUBLICATIONS);
            }
            return new PersonPreferencesDao(this);
        }

    }
}
