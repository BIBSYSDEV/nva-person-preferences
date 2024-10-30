package no.sikt.nva.person.preferences.commons.model;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.apigateway.exceptions.NotFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import static nva.commons.core.attempt.Try.attempt;

public interface Persistable<Dao> {

    URI personId();

    Instant created();

    Instant modified();

    Dao upsert(PersonService service) throws NotFoundException;

    Dao fetch(PersonService service) throws NotFoundException;

    Map<String, AttributeValue> toDynamoFormat();

    static Map<String, AttributeValue> toDynamoFormat(Object object) {
        var item = attempt(() -> Item.fromJSON(
            JsonUtils.dynamoObjectMapper.writeValueAsString(object))).orElseThrow();
        return ItemUtils.toAttributeValues(item);
    }

}
