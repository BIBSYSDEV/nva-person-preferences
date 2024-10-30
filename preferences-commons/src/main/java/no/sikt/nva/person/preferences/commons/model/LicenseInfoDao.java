package no.sikt.nva.person.preferences.commons.model;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.apigateway.exceptions.NotFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import static nva.commons.core.attempt.Try.attempt;

@JsonSerialize
public record LicenseInfoDao(
    URI personId,
    Instant created,
    Instant modified,
    URI licenseUri
) implements Persistable<LicenseInfoDao> {

    @Override
    public LicenseInfoDao upsert(PersonService service) throws NotFoundException {
        return new Builder()
            .fromDynamoFormat(service.upsert(this));
    }

    @Override
    public LicenseInfoDao fetch(PersonService service) throws NotFoundException {
        return new Builder()
            .fromDynamoFormat(service.fetchResource(this));
    }

    @Override
    public Map<String, AttributeValue> toDynamoFormat() {
        return Persistable.toDynamoFormat(this);

    }

    public LicenseInfo toDto() {
        return new LicenseInfo.Builder()
            .fromDao(this);
    }

    public static class Builder {
        private URI personId;
        private URI licenseUri;

        public Builder withPersonId(URI personId) {
            this.personId = personId;
            return this;
        }


        public Builder withLicenseUri(URI licenseUri) {
            this.licenseUri = licenseUri;
            return this;
        }

        public LicenseInfoDao fromDynamoFormat(Map<String, AttributeValue> dynamoDbMap) {
            return attempt(() -> JsonUtils.dynamoObjectMapper.readValue(ItemUtils.toItem(dynamoDbMap).toJSON(),
                LicenseInfoDao.class))
                .orElseThrow();
        }

        public LicenseInfoDao build() {
            var timestamp = Instant.now();
            return new LicenseInfoDao(
                personId,
                timestamp,
                timestamp,
                licenseUri);
        }
    }

}