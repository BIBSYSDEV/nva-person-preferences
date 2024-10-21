package no.sikt.nva.person.preferences.commons.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;
import nva.commons.apigateway.exceptions.NotFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record PersonPreferences(URI personId,
                                List<URI> promotedPublications,
                                LicenseInfo licenseInfo,
                                @JsonIgnore PersonPreferencesService personPreferencesService) {

    public PersonPreferences upsert() throws NotFoundException {
        return personPreferencesService.upsertPreferences(this);
    }

    public PersonPreferences fetch() throws NotFoundException {
        return personPreferencesService.fetchPreferences(this);
    }

    public PersonPreferencesDao toDao() {
        return new PersonPreferencesDao.Builder()
                .withPersonId(personId)
                .withPromotedPublications(promotedPublications)
                .withLicenseUri(licenseInfo.licenseUri())
                .withLicenseSignedDate(licenseInfo.signed())
                .build();
    }

    public Builder copy() {
        return new PersonPreferences.Builder(personPreferencesService)
                .withPersonId(this.personId)
                .withPromotedPublications(this.promotedPublications)
                .withLicenseInfo(this.licenseInfo);
    }

    public static class Builder {

        private PersonPreferencesService preferencesService;
        private URI personId;
        private Instant licenseSignedDate;
        private URI licenseUri;
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

        public Builder withLicenseSignedDate(Instant licenseSignedDate) {
            this.licenseSignedDate = licenseSignedDate;
            return this;
        }

        public Builder withLicenseUri(URI licenseUri) {
            this.licenseUri = licenseUri;
            return this;
        }

        public Builder withLicenseInfo(LicenseInfo licenseInfo) {
            return withLicenseUri(licenseInfo.licenseUri())
                    .withLicenseSignedDate(licenseInfo.signed());
        }

        public PersonPreferences fromDao(PersonPreferencesDao dao) {
            return new PersonPreferences.Builder()
                    .withPersonId(dao.personId())
                    .withLicenseUri(dao.licenseUri())
                    .withLicenseSignedDate(dao.licenseSignedDate())
                    .withPromotedPublications(dao.promotedPublications())
                    .build();
        }

        public PersonPreferences build() {
            return new PersonPreferences(
                    personId,
                    promotedPublications,
                    new LicenseInfo(licenseSignedDate,licenseUri),
                    preferencesService);
        }
    }
}
