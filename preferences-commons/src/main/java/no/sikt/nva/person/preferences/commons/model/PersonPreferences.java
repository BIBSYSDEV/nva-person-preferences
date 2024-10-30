package no.sikt.nva.person.preferences.commons.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record PersonPreferences(
    URI personId,
    List<URI> promotedPublications) {

    public PersonPreferencesDao toDao() {
        return new PersonPreferencesDao.Builder()
            .withPersonId(personId)
            .withPromotedPublications(promotedPublications)
            .build();
    }


    public static class Builder {

        private URI personId;
        private List<URI> promotedPublications;

        public Builder() {
        }

        public PersonPreferences fromDao(PersonPreferencesDao dao) {
            return new PersonPreferences.Builder()
                .withPersonId(dao.personId())
                .withPromotedPublications(dao.promotedPublications())
                .build();
        }

        public Builder withPersonId(URI personId) {
            this.personId = personId;
            return this;
        }

        public Builder withPromotedPublications(List<URI> promotedPublications) {
            this.promotedPublications = nonNull(promotedPublications) ? promotedPublications : Collections.emptyList();
            return this;
        }

        public PersonPreferences build() {
            return new PersonPreferences(
                personId,
                promotedPublications);
        }
    }
}
