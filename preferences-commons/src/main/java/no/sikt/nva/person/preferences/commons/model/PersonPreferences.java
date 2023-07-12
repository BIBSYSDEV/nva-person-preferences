package no.sikt.nva.person.preferences.commons.model;

import static java.util.Objects.nonNull;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.beans.Transient;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record PersonPreferences(URI personId,
                                List<String> promotedPublications,
                                @Transient PersonPreferencesService preferencesService) {

    public PersonPreferences(URI personId,
                             List<String> promotedPublications,
                             PersonPreferencesService preferencesService) {
        this.personId = personId;
        this.preferencesService = preferencesService;
        this.promotedPublications = nonNull(promotedPublications) ? promotedPublications : Collections.emptyList();
    }

    public PersonPreferences create() {
        return preferencesService.createProfile(this);
    }

    public void update() {
        preferencesService.updateProfile(this);
    }

    public PersonPreferencesDao toDao() {
        return new PersonPreferencesDao(personId, promotedPublications, null, null);
    }

    public static class Builder {

        private PersonPreferencesService preferencesService;
        private URI personId;
        private List<String> promotedPublications;

        public Builder(PersonPreferencesService preferencesService) {
            this.preferencesService = preferencesService;
        }

        public Builder() {
        }

        public Builder withPersonId(URI personId) {
            this.personId = personId;
            return this;
        }

        public Builder withPromotedPublications(List<String> promotedPublications) {
            this.promotedPublications = promotedPublications;
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
