package no.unit.nva.person.preferences.commons.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import no.unit.nva.person.preferences.commons.service.ProfileService;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record Profile(URI identifier,
                      List<String> promotedPublications,
                      Instant created,
                      Instant modified) {

    public Profile create(ProfileService profileService) {
        return profileService.createProfile(this);
    }

    public Profile update(ProfileService profileService) {
        return profileService.updateProfile(this);
    }

    public Builder copy() {
        return new Builder()
                   .withIdentifier(this.identifier)
                   .withPromotedPublication(this.promotedPublications);
    }

    public ProfileDao toDao() {
        return new ProfileDao(this);
    }

    public static class Builder {

        private URI identifier;
        private List<String> promotedPublications;
        private Instant created;
        private Instant modified;

        public Builder withIdentifier(URI userIdentifier) {
            this.identifier = userIdentifier;
            return this;
        }

        public Builder withPromotedPublication(List<String> promotedPublications) {
            this.promotedPublications = promotedPublications;
            return this;
        }

        public Builder withCreated(Instant created) {
            this.created = created;
            return this;
        }

        public Builder withModified(Instant modified) {
            this.modified = modified;
            return this;
        }

        public Profile build() {
            return new Profile(identifier, promotedPublications, created, modified);
        }
    }
}
