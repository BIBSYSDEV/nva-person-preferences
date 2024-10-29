package no.sikt.nva.person.preferences.commons.model;

import java.net.URI;
import java.time.Instant;

import static java.util.Objects.nonNull;

public record LicenseInfo2(
        Instant signed,
        URI licenseUri
) {

    @Override
    public Instant signed() {
        // in order to verify users' licenses, we need to compare the signed date with the current license date
        return nonNull(signed) ? signed : Instant.MIN;
    }
}
