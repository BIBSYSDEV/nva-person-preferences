package no.sikt.nva.person.preferences.commons.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@JsonSerialize
public record PersonPreferencesDto(
    URI personId,
    List<URI> promotedPublications) {

    public PersonPreferencesDao toDao() {
        return PersonPreferencesDao.builder()
            .withId(personId)
            .promotedPublications(promotedPublications)
            .build();
    }

    public static class Builder {

        private URI personId;
        private List<URI> promotedPublications;

        public Builder() {
        }

        public PersonPreferencesDto fromDao(PersonPreferencesDao dao) {
            return new PersonPreferencesDto.Builder()
                .withPersonId(dao.withId())
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

        public PersonPreferencesDto build() {
            return new PersonPreferencesDto(
                personId,
                promotedPublications);
        }
    }
}
