package no.sikt.nva.person.preferences.commons.model;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.net.URI;
import java.time.Instant;

@JsonSerialize
public record TermsConditionsDto(
        URI personId,
        Instant signedDate,
        URI termsConditionsUrl) {

    public static class Builder {
        private URI personId;
        private Instant licenseSignedDate;
        private URI licenseUri;

        public Builder() {
        }

        public TermsConditionsDto fromDao(TermsConditionsDao dao) {
            return new TermsConditionsDto.Builder()
                    .withPersonId(dao.personId())
                    .withSignedDate(dao.modified())
                    .withTermsConditionsUri(dao.termsConditionsUri())
                    .build();
        }

        public Builder withPersonId(URI personId) {
            this.personId = personId;
            return this;
        }

        public Builder withSignedDate(Instant licenseSignedDate) {
            this.licenseSignedDate = licenseSignedDate;
            return this;
        }

        public Builder withTermsConditionsUri(URI termsConditionsUrl) {
            this.licenseUri = termsConditionsUrl;
            return this;
        }

        public TermsConditionsDto build() {
            return new TermsConditionsDto(this.personId, this.licenseSignedDate, this.licenseUri);
        }
    }
}
