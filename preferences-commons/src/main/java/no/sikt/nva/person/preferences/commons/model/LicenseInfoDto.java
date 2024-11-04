package no.sikt.nva.person.preferences.commons.model;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.net.URI;
import java.time.Instant;

@JsonSerialize
public record LicenseInfoDto(
        URI personId,
        Instant signed,
        URI licenseUri) {

    public static class Builder {
        private URI personId;
        private Instant licenseSignedDate;
        private URI licenseUri;

        public Builder() {
        }

        public LicenseInfoDto fromDao(LicenseInfoDao dao) {
            return new LicenseInfoDto.Builder()
                    .withPersonId(dao.withId())
                    .withLicenseSignedDate(dao.modified())
                    .withLicenseUri(dao.licenseUri())
                    .build();
        }

        public Builder withPersonId(URI personId) {
            this.personId = personId;
            return this;
        }

        public Builder withLicenseSignedDate(Instant licenseSignedDate) {
            this.licenseSignedDate = licenseSignedDate;
            return this;
        }

        public Builder withLicenseUri(URI licenseUri) {
            this.licenseUri = licenseUri;
            return this;
        }

        public LicenseInfoDto build() {
            return new LicenseInfoDto(this.personId, this.licenseSignedDate, this.licenseUri);
        }
    }
}
