package no.sikt.nva.person.preferences.commons.model;

import no.unit.nva.commons.json.JsonUtils;
import software.amazon.awssdk.enhanced.dynamodb.document.EnhancedDocument;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static nva.commons.core.attempt.Try.attempt;

public record PersonPreferencesDao(URI personId,
                                   List<URI> promotedPublications,
                                   Instant created,
                                   Instant modified) {

    public Map<String, AttributeValue> toDynamoFormat() {
        return attempt(() -> EnhancedDocument.fromJson(
                JsonUtils.dynamoObjectMapper.writeValueAsString(this)).toMap()).orElseThrow();
    }

    public PersonPreferencesDao.Builder copy() {
        return new PersonPreferencesDao.Builder()
                .withPersonId(this.personId)
                .withPromotedPublications(this.promotedPublications)
                .withCreatedDate(this.created)
                .withModifiedDate(this.modified);
    }

    public static class Builder {

        private URI personId;
        private List<URI> promotedPublications;
        private Instant created;
        private Instant modified;

        public Builder withPersonId(URI personId) {
            this.personId = personId;
            return this;
        }

        public Builder withPromotedPublications(List<URI> promotedPublications) {
            this.promotedPublications = promotedPublications;
            return this;
        }

        public Builder withCreatedDate(Instant created) {
            this.created = created;
            return this;
        }

        public Builder withModifiedDate(Instant modified) {
            this.modified = modified;
            return this;
        }

        public PersonPreferencesDao fromDynamoFormat(Map<String, AttributeValue> map) {
            var json = EnhancedDocument.fromAttributeValueMap(map).toJson();
            return attempt(() -> JsonUtils.dynamoObjectMapper.readValue(json, PersonPreferencesDao.class))
                    .orElseThrow();
        }

        public PersonPreferencesDao build() {
            return new PersonPreferencesDao(personId, promotedPublications, created, modified);
        }
    }
}
