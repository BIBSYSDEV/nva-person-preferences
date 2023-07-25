package no.sikt.nva.person.preferences.commons.model;

import static java.util.Objects.nonNull;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record PersonPreferences(URI personId,
                                List<URI> promotedPublications,
                                @JsonIgnore PersonPreferencesService personPreferencesService, Instant created, Instant modified) {

    public PersonPreferences create() {
        return personPreferencesService.createProfile(this);
    }

    public void update() {
        personPreferencesService.updateProfile(this);
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
        private Instant created;
        private Instant mmodified;

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
                       .withCreated(dao.created())
                       .withModified(dao.modified())
                       .build();
        }

        public PersonPreferences build() {
            return new PersonPreferences(personId, promotedPublications, preferencesService, created, mmodified);
        }

        public Builder withCreated(Instant created) {
            this.created = created;
            return this;
        }

        public Builder withModified(Instant modified) {
            this.mmodified = modified;
            return this;
        }
    }
}
