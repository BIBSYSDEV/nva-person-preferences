package no.unit.nva.person.preferences.commons.model;

import static nva.commons.core.attempt.Try.attempt;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import no.unit.nva.commons.json.JsonUtils;
import no.unit.nva.person.preferences.commons.model.PersonPreferences.Builder;

public record PersonPreferencesDao(URI identifier,
                                   List<String> promotedPublications,
                                   Instant created,
                                   Instant modified) {

    public Map<String, AttributeValue> toDynamoFormat() {
        var item = attempt(() -> Item.fromJSON(
            JsonUtils.dynamoObjectMapper.writeValueAsString(this))).orElseThrow();
        return ItemUtils.toAttributeValues(item);
    }

    public PersonPreferencesDao.Builder copy() {
        return new PersonPreferencesDao.Builder()
                   .withIdentifier(this.identifier)
                   .withPromotedPublication(this.promotedPublications)
                   .withCreatedDate(this.created)
                   .withModifiedDate(this.modified);
    }

    public static class Builder {

        private URI identifier;
        private List<String> promotedPublications;
        private Instant created;
        private Instant modified;

        public Builder withIdentifier(URI userIdentifier) {
            this.identifier = userIdentifier;
            return this;
        }

        public Builder withPromotedPublication(List<String> promotedPublications) {
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
            return attempt(() -> JsonUtils.dynamoObjectMapper.readValue(ItemUtils.toItem(map).toJSON(),
                                                                        PersonPreferencesDao.class))
                       .orElseThrow();
        }

        public PersonPreferencesDao build() {
            return new PersonPreferencesDao(identifier, promotedPublications, created, modified);
        }
    }
}
