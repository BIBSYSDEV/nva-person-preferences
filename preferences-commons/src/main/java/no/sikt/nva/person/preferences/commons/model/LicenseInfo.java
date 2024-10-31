package no.sikt.nva.person.preferences.commons.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.net.URI;
import java.time.Instant;

@JsonSerialize
public record LicenseInfo(
        URI personId,
        Instant signed,
        URI licenseUri) {

    public static class Builder {
        private URI personId;
        private Instant licenseSignedDate;
        private URI licenseUri;

        public Builder() {
        }

        public LicenseInfo fromDao(LicenseInfoDao dao) {
            return new LicenseInfo.Builder()
                    .withPersonId(dao.personId())
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

        public LicenseInfo build() {
            return new LicenseInfo(this.personId, this.licenseSignedDate, this.licenseUri);
        }
    }
}
