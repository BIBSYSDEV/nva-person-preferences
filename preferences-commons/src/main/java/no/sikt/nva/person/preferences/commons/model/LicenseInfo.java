package no.sikt.nva.person.preferences.commons.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import nva.commons.apigateway.exceptions.NotFoundException;

import java.net.URI;
import java.time.Instant;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record LicenseInfo(URI personId,
                          Instant signed,
                          URI licenseUri,
                          @JsonIgnore PersonService<LicenseInfoDao> service) {


    public LicenseInfo upsert() throws NotFoundException {
        return service.upsert(this.toDao());
    }

    public LicenseInfo fetch() throws NotFoundException {
        return service.fetch(this);
    }

    public LicenseInfoDao toDao() {
        return new LicenseInfoDao.Builder()
                .withPersonId(personId)
                .withCreatedDate()
                .build();
    }

    public Builder copy() {
        return new LicenseInfo.Builder(service)
                .withPersonId(this.personId)
                .withLicenseSignedDate(this.signed)
                .withLicenseUri(this.licenseUri);
    }

    public static class Builder {

        private PersonService<LicenseInfo, LicenseInfoDao> preferencesService;
        private URI personId;
        private Instant licenseSignedDate;
        private URI licenseUri;

        public Builder(PersonService<?,?> preferencesService) {
            this.preferencesService = preferencesService;
        }

        public Builder() {
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


        public LicenseInfo fromDao(LicenseInfoDao dao) {
            return new LicenseInfo.Builder()
                    .withPersonId(dao.getPersonId())
                    .withLicenseSignedDate(dao.getLicenseSignedDate())
                    .withLicenseUri(dao.getLicenseUri())
                    .build();
        }

        public LicenseInfo build() {
            return new LicenseInfo(
                    personId,
                    licenseSignedDate,
                    licenseUri,
                    preferencesService);
        }
    }
}
