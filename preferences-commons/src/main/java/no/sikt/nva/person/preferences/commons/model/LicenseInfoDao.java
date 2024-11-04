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
import java.util.Optional;

import static no.sikt.nva.person.preferences.commons.model.DataAccessService.RESOURCE_NOT_FOUND_MESSAGE;

@DynamoDbImmutable(builder = LicenseInfoDao.Builder.class)
public record LicenseInfoDao(
        @DynamoDbPartitionKey URI withId,
        @DynamoDbSortKey String withType,
        Instant created,
        Instant modified,
        URI licenseUri) implements DataAccessClass<LicenseInfoDao> {

    public LicenseInfoDao(Builder builder) {
        this(
                builder.withId,
                builder.withType,
                builder.created,
                builder.modified,
                builder.licenseUri
        );
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
        return Optional.ofNullable(service.fetch(this))
                .orElseThrow(() -> new NotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private URI withId;
        private String withType; //= "TermsOfUse";
        private URI licenseUri;
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

        public Builder licenseUri(URI licenseUri) {
            this.licenseUri = licenseUri;
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

        public LicenseInfoDao build() {
            if (created == null) {
                created = Instant.now();
            }
            if (modified == null) {
                modified = created;
            }
            if (withType == null) {
                withType = "TermsOfUse";
            }
            return new LicenseInfoDao(this);
        }
    }

}