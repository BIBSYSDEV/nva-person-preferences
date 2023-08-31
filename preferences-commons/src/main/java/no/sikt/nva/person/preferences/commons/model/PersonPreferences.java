package no.sikt.nva.person.preferences.commons.model;

import static java.util.Objects.nonNull;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;
import nva.commons.apigateway.exceptions.NotFoundException;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record PersonPreferences(URI personId,
                                List<URI> promotedPublications,
                                @JsonIgnore PersonPreferencesService personPreferencesService) {

    public PersonPreferences upsert() throws NotFoundException {
        return personPreferencesService.upsertPreferences(this);
    }

    public PersonPreferences fetch() throws NotFoundException {
        return personPreferencesService.fetchPreferences(this);
    }

    public PersonPreferencesDao toDao() {
        return new PersonPreferencesDao(personId, promotedPublications, null, null);
    }

    public Builder copy() {
        return new PersonPreferences.Builder(personPreferencesService)
            .withPersonId(this.personId)
            .withPromotedPublications(this.promotedPublications);
    }

    public static class Builder {

        private PersonPreferencesService preferencesService;
        private URI personId;
        private List<URI> promotedPublications;

        public Builder(PersonPreferencesService preferencesService) {
            this.preferencesService = preferencesService;
        }

        public Builder() {
        }

        public Builder withPersonId(URI personId) {
            this.personId = personId;
            return this;
        }

        public Builder withPromotedPublications(List<URI> promotedPublications) {
            this.promotedPublications = nonNull(promotedPublications) ? promotedPublications : Collections.emptyList();
            return this;
        }

        public PersonPreferences fromDao(PersonPreferencesDao dao) {
            return new PersonPreferences.Builder()
                .withPersonId(dao.personId())
                .withPromotedPublications(dao.promotedPublications())
                .build();
        }

        public PersonPreferences build() {
            return new PersonPreferences(personId, promotedPublications, preferencesService);
        }
    }
}
