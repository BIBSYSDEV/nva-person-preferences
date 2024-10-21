package no.sikt.nva.person.preferences.commons.model;

import java.net.URI;
import java.time.Instant;

public record LicenseInfo(
        Instant signed,
        URI licenseUri
) {
}
