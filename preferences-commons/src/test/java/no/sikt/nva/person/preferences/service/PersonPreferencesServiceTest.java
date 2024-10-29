package no.sikt.nva.person.preferences.service;

import no.sikt.nva.person.preferences.commons.model.LicenseInfo2;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;
import no.sikt.nva.person.preferences.test.support.LocalPreferencesTestDatabase;
import nva.commons.apigateway.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonPreferencesServiceTest extends LocalPreferencesTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    private PersonPreferencesService preferencesService;

    @BeforeEach
    void initialize() {
        super.init(TABLE_NAME);
        this.preferencesService = new PersonPreferencesService(client, TABLE_NAME);
    }

    @Test
    void shouldPersistUserPreferences() throws NotFoundException {
        var personPreferences = new PersonPreferences.Builder(preferencesService)
                .withPersonId(randomUri())
                .withPromotedPublications(List.of(randomUri()))
                .build()
                .upsert();
        var persistedpersonPreferences = preferencesService
                .fetchPreferences(personPreferences);
        assertThat(persistedpersonPreferences, is(equalTo(personPreferences)));
    }

    @Test
    void shouldUpdateExistingUserPreferences() throws NotFoundException {
        var userIdentifier = randomUri();
        var personPreferences = new PersonPreferences
                .Builder(preferencesService)
                .withPersonId(userIdentifier)
                .withPromotedPublications(List.of(randomUri()))
                .build()
                .upsert();

        new PersonPreferences(userIdentifier, List.of(), null, preferencesService).upsert();

        var persistedPreferences = preferencesService.fetchPreferences(personPreferences);
        assertThat(persistedPreferences.promotedPublications(), is(emptyIterable()));
    }

    @Test
    void shouldUpdateLicenseInfo() throws NotFoundException {
        var userIdentifier = randomUri();
        var personPreferences = new PersonPreferences
                .Builder(preferencesService)
                .withPersonId(userIdentifier)
                .withPromotedPublications(List.of(randomUri()))
                .build()
                .upsert();

        var licenseInfo = new LicenseInfo2(Instant.now(), randomUri());

        new PersonPreferences.Builder(preferencesService)
                .withPersonId(userIdentifier)
                .withLicenseInfo(licenseInfo)
                .build().upsert();


        var persistedPreferences = preferencesService.fetchPreferences(personPreferences);
        assertThat(persistedPreferences.licenseInfo(), is(equalTo(licenseInfo)));
        assertThat(persistedPreferences.promotedPublications(), is(equalTo(personPreferences.promotedPublications())));
    }

    @Test
    void shouldFetchUserPreferences() throws nva.commons.apigateway.exceptions.NotFoundException {
        var userIdentifier = randomUri();
        var personPreferences = new PersonPreferences.Builder(preferencesService)
                .withPersonId(userIdentifier)
                .withPromotedPublications(List.of(randomUri()))
                .build()
                .upsert();

        new PersonPreferences(userIdentifier, List.of(), null, preferencesService).fetch();
        var persistedpersonPreferences = preferencesService.fetchPreferences(personPreferences);
        assertThat(persistedpersonPreferences, is(equalTo(personPreferences)));
    }

    @Test
    void shouldThrowExceptionWhenFetchingNonExistentPersonPreferences() {
        var personPreferences = new PersonPreferences.Builder()
                .withPersonId(randomUri())
                .withPromotedPublications(List.of(randomUri()))
                .build();
        assertThrows(NotFoundException.class,
                () -> preferencesService.fetchPreferences(personPreferences));
    }
}
