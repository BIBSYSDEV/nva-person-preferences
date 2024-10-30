package no.sikt.nva.person.preferences.commons.model;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.apigateway.exceptions.NotFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static nva.commons.core.attempt.Try.attempt;

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record PersonPreferencesDao(
    URI personId,
    Instant created,
    Instant modified,
    List<URI> promotedPublications
) implements Persistable<PersonPreferencesDao> {

    @Override
    public PersonPreferencesDao upsert(PersonService service) throws NotFoundException {
        return new PersonPreferencesDao.Builder()
            .fromDynamoFormat(service.upsert(this));
    }

    @Override
    public PersonPreferencesDao fetch(PersonService service) throws NotFoundException {
        return new PersonPreferencesDao.Builder()
            .fromDynamoFormat(service.fetchResource(this));
    }

    @Override
    public Map<String, AttributeValue> toDynamoFormat() {
        return Persistable.toDynamoFormat(this);
    }

    public PersonPreferences toDto() {
        return new PersonPreferences.Builder().fromDao(this);
    }


    public static class Builder {

        private URI personId;
        private List<URI> promotedPublications;


        public Builder withPersonId(URI personId) {
            this.personId = personId;
            return this;
        }

        public Builder withPromotedPublications(List<URI> promotedPublications) {
            this.promotedPublications = promotedPublications;
            return this;
        }

        public PersonPreferencesDao fromDynamoFormat(Map<String, AttributeValue> map) {
            return attempt(() -> JsonUtils.dynamoObjectMapper.readValue(ItemUtils.toItem(map).toJSON(),
                    PersonPreferencesDao.class))
                    .orElseThrow();
        }

        public PersonPreferencesDao build() {
            var timestamp = Instant.now();
            return new PersonPreferencesDao(
                personId,
                timestamp,
                timestamp,
                promotedPublications);
        }
    }
}
