package no.sikt.nva.person.preferences.commons.model;

import nva.commons.apigateway.exceptions.NotFoundException;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.net.URI;
import java.time.Instant;

import static java.util.Objects.isNull;

@DynamoDbImmutable(builder = TermsConditionsDao.Builder.class)
public record TermsConditionsDao(
        @DynamoDbPartitionKey URI personId,
        @DynamoDbSortKey String withType,
        Instant created,
        Instant modified,
        URI termsConditionsUri) implements DataAccessClass<TermsConditionsDao> {

    private TermsConditionsDao(Builder builder) {
        this(
                builder.id,
                builder.type,
                builder.createdInstant,
                builder.modifiedInstant,
                builder.termsUri
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    @DynamoDbIgnore
    public TermsConditionsDto toDto() {
        return new TermsConditionsDto.Builder().fromDao(this);
    }

    @DynamoDbIgnore
    @Override
    public TermsConditionsDao upsert(DataAccessService<TermsConditionsDao> service) throws NotFoundException {
        service.persist(this);
        return fetch(service);
    }

    @DynamoDbIgnore
    @Override
    public TermsConditionsDao fetch(DataAccessService<TermsConditionsDao> service) throws NotFoundException {
        return service.fetch(this);
    }

    public static class Builder {
        public static final String TERMS_OF_USE = "TermsConditions";
        private URI id;
        private String type;
        private URI termsUri;
        private Instant createdInstant;
        private Instant modifiedInstant;

        private Builder() {
        }

        public Builder personId(URI withId) {
            this.id = withId;
            return this;
        }

        public Builder withType(String withType) {
            this.type = withType;
            return this;
        }

        public Builder termsConditionsUri(URI licenseUri) {
            this.termsUri = licenseUri;
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

        public TermsConditionsDao build() {
            if (isNull(modifiedInstant)) {
                modified(Instant.now());
            }
            if (isNull(createdInstant)) {
                created(modifiedInstant);
            }
            if (isNull(type)) {
                withType(TERMS_OF_USE);
            }
            return new TermsConditionsDao(this);
        }
    }

}