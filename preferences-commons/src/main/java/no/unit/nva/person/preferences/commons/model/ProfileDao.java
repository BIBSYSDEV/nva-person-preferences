package no.unit.nva.person.preferences.commons.model;

import static nva.commons.core.attempt.Try.attempt;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import java.util.Map;
import no.unit.nva.commons.json.JsonUtils;

public record ProfileDao(Profile profile) {

    public Map<String, AttributeValue> toDynamoFormat() {
        var item = attempt(() -> Item.fromJSON(
            JsonUtils.dynamoObjectMapper.writeValueAsString(this.profile()))).orElseThrow();
        return ItemUtils.toAttributeValues(item);
    }

    public static class Builder {

        private Map<String, AttributeValue> map;

        public Builder fromDynamoFormat(Map<String, AttributeValue> map) {
            this.map = map;
            return this;
        }

        public ProfileDao build() {
            return attempt(() -> JsonUtils.dynamoObjectMapper.readValue(ItemUtils.toItem(map).toJSON(), Profile.class))
                       .orElseThrow()
                       .toDao();
        }
    }
}
