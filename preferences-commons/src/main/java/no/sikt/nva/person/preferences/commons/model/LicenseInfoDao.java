package no.sikt.nva.person.preferences.commons.model;

import nva.commons.apigateway.exceptions.NotFoundException;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.net.URI;
import java.time.Instant;

import static java.util.Objects.isNull;

@DynamoDbImmutable(builder = LicenseInfoDao.Builder.class)
public record LicenseInfoDao(
        @DynamoDbPartitionKey URI withId,
        @DynamoDbSortKey String withType,
        Instant created,
        Instant modified,
        URI licenseUri) implements DataAccessClass<LicenseInfoDao> {

    private LicenseInfoDao(Builder builder) {
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
    public LicenseInfoDto toDto() {
        return new LicenseInfoDto.Builder().fromDao(this);
    }

    @DynamoDbIgnore
    @Override
    public LicenseInfoDao upsert(DataAccessService<LicenseInfoDao> service) throws NotFoundException {
        service.persist(this);
        return fetch(service);
    }

    @DynamoDbIgnore
    @Override
    public LicenseInfoDao fetch(DataAccessService<LicenseInfoDao> service) throws NotFoundException {
        return service.fetch(this);
    }

    public static class Builder {
        public static final String TERMS_OF_USE = "TermsOfUse";
        private URI id;
        private String type;
        private URI termsUri;
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

        public Builder licenseUri(URI licenseUri) {
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

        public LicenseInfoDao build() {
            if (isNull(modifiedInstant)) {
                modified(Instant.now());
            }
            if (isNull(createdInstant)) {
                created(modifiedInstant);
            }
            if (isNull(type)) {
                withType(TERMS_OF_USE);
            }
            return new LicenseInfoDao(this);
        }
    }

}