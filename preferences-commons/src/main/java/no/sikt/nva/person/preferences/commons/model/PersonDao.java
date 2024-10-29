package no.sikt.nva.person.preferences.commons.model;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import no.unit.nva.commons.json.JsonUtils;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import static nva.commons.core.attempt.Try.attempt;

public class PersonDao {

    private final URI personId;
    private final Instant created;
    private final Instant modified;

    @JsonIgnore
    private PersonService<PersonDao> service;

    public PersonDao(Builder<?> builder) {
        this.personId = builder.personId;
        this.created = builder.created;
        this.modified = builder.modified;
    }

    public URI getPersonId() {
        return personId;
    }

    public Instant getCreated() {
        return created;
    }

    public Instant getModified() {
        return modified;
    }

    public Map<String, AttributeValue> toDynamoFormat() {
        var item = attempt(() -> Item.fromJSON(
                JsonUtils.dynamoObjectMapper.writeValueAsString(this))).orElseThrow();
        return ItemUtils.toAttributeValues(item);
    }

    public Builder<?> copy() {
        return new PersonDao.Builder<>()
                .withPersonId(this.getPersonId())
                .withCreatedDate(this.getCreated())
                .withModifiedDate(this.getModified());
    }


    public static class Builder<B extends Builder> {
        private URI personId;
        private Instant created;
        private Instant modified;

        B self() {
            return (B) this;
        }

        public B withPersonId(URI uri) {
            this.personId = uri;
            return self();
        }

        public B withCreatedDate(Instant created) {
            this.created = created;
            return self();
        }

        public B withModifiedDate(Instant modified) {
            this.modified = modified;
            return self();
        }

        public PersonDao build() {
            return new PersonDao(this);
        }
    }
}
