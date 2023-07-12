package no.sikt.nva.person.preferences.commons.model;

import static java.util.Objects.nonNull;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.beans.Transient;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import no.sikt.nva.person.preferences.commons.service.PreferencesService;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record PersonPreferences(URI id,
                                List<String> promotedPublications,
                                @Transient PreferencesService preferencesService) {

    public PersonPreferences(URI id, List<String> promotedPublications, PreferencesService preferencesService) {
        this.id = id;
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
        return new PersonPreferencesDao(id, promotedPublications, null, null);
    }

    public static class Builder {

        private PreferencesService preferencesService;
        private URI identifier;
        private List<String> promotedPublications;

        public Builder(PreferencesService preferencesService) {
            this.preferencesService = preferencesService;
        }

        public Builder() {
        }

        public Builder withId(URI userIdentifier) {
            this.identifier = userIdentifier;
            return this;
        }

        public Builder withPromotedPublication(List<String> promotedPublications) {
            this.promotedPublications = promotedPublications;
            return this;
        }

        public PersonPreferences fromDao(PersonPreferencesDao dao) {
            return new PersonPreferences.Builder()
                       .withId(dao.identifier())
                       .withPromotedPublication(dao.promotedPublications())
                       .build();
        }

        public PersonPreferences build() {
            return new PersonPreferences(identifier, promotedPublications, preferencesService);
        }
    }
}
