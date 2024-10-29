package no.sikt.nva.person.preferences.commons.model;


import java.net.URI;
import java.time.Instant;

public class LicenseInfoDao extends PersonDao {

    private final Instant licenseSignedDate;
    private final URI licenseUri;

    public LicenseInfoDao(Builder<?> builder) {
        super(builder);
        this.licenseSignedDate = builder.licenseSignedDate;
        this.licenseUri = builder.licenseUri;
    }

    public Instant getLicenseSignedDate() {
        return licenseSignedDate;
    }

    public URI getLicenseUri() {
        return licenseUri;
    }

    @Override
    public LicenseInfoDao.Builder<?> copy() {
        return new LicenseInfoDao.Builder<>()
                .withPersonId(this.getPersonId())
                .withCreatedDate(this.getCreated())
                .withModifiedDate(this.getModified())
                .withLicenseSignedDate(this.getLicenseSignedDate())
                .withLicenseUri(this.getLicenseUri());
    }


    public static class Builder<T extends Builder<T>> extends PersonDao.Builder<T> {

        private Instant licenseSignedDate;
        private URI licenseUri;


        public T withLicenseSignedDate(Instant licenseSignedDate) {
            this.licenseSignedDate = licenseSignedDate;
            return self();
        }

        public T withLicenseUri(URI licenseUri) {
            this.licenseUri = licenseUri;
            return self();
        }

        @Override
        public LicenseInfoDao build() {
            return new LicenseInfoDao(this);
        }
    }
}
