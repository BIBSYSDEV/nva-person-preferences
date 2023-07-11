package no.sikt.nva.person.preferences.commons.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import java.util.List;
import no.sikt.nva.person.preferences.commons.service.PreferencesService;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record PersonPreferences(URI id,
                                List<String> promotedPublications) {

    public PersonPreferences create(PreferencesService preferencesService) {
        return preferencesService.createProfile(this);
    }

    public void update(PreferencesService preferencesService) {
        preferencesService.updateProfile(this);
    }

    public Builder copy() {
        return new Builder()
                   .withId(this.id)
                   .withPromotedPublication(this.promotedPublications);
    }

    public PersonPreferencesDao toDao() {
        return new PersonPreferencesDao(id, promotedPublications, null, null);
    }

    public static class Builder {

        private URI identifier;
        private List<String> promotedPublications;

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
            return new PersonPreferences(identifier, promotedPublications);
        }
    }
}
