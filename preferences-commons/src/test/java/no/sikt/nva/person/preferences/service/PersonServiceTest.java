package no.sikt.nva.person.preferences.service;

import no.sikt.nva.person.preferences.commons.model.LicenseInfoDao;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import no.sikt.nva.person.preferences.test.support.LocalPreferencesTestDatabase;
import nva.commons.apigateway.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonServiceTest extends LocalPreferencesTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    private PersonService preferencesService;

    @BeforeEach
    void initialize() {
        super.init(TABLE_NAME);
        this.preferencesService = new PersonService(client, TABLE_NAME);
    }

    @Test
    void shouldPersistUserPreferences() throws NotFoundException {
        var personPreferences = new PersonPreferencesDao.Builder()
            .withPersonId(randomUri())
            .withPromotedPublications(List.of(randomUri()))
            .build()
            .upsert(preferencesService);

        var persistedpersonPreferences = new PersonPreferencesDao.Builder()
            .withPersonId(personPreferences.personId())
            .build()
            .fetch(preferencesService);
        assertThat(persistedpersonPreferences, is(equalTo(personPreferences)));
    }

    @Test
    void shouldUpdateExistingUserPreferences() throws NotFoundException {
        var userIdentifier = randomUri();
        var personPreferences = new PersonPreferencesDao
            .Builder()
            .withPersonId(userIdentifier)
            .withPromotedPublications(List.of(randomUri()))
            .build()
            .upsert(preferencesService);

        assertThat(personPreferences.promotedPublications(), hasSize(1));

        var persistedPreferences = new PersonPreferencesDao
            .Builder()
            .withPersonId(userIdentifier)
            .build()
            .upsert(preferencesService).toDto();

        assertThat(persistedPreferences.promotedPublications(), is(emptyIterable()));
    }

    @Test
    void shouldUpdateExistingUserPreferences2() throws NotFoundException {
        var userIdentifier = randomUri();
        var takeOne = new PersonPreferencesDao
            .Builder()
            .withPersonId(userIdentifier)
            .withPromotedPublications(List.of(randomUri()))
            .build()
            .upsert(preferencesService);

        assertThat(takeOne.promotedPublications(), hasSize(1));

        var takeTwo = new PersonPreferencesDao.Builder()
            .withPersonId(userIdentifier)
            .build()
            .upsert(preferencesService);

        assertNull(takeTwo.promotedPublications());
    }

    @Test
    void shouldUpdateLicenseInfo() throws NotFoundException {
        var userIdentifier = randomUri();
        var licenseInfoDao = new LicenseInfoDao.Builder()
            .withPersonId(userIdentifier)
            .withLicenseUri(randomUri())
            .build()
            .upsert(preferencesService);

        var licenseInfo = new LicenseInfoDao.Builder()
                .withPersonId(userIdentifier)
                .build()
                .fetch(preferencesService);


        assertThat(licenseInfoDao, is(equalTo(licenseInfo)));
    }

    @Test
    void shouldFetchUserPreferences() throws nva.commons.apigateway.exceptions.NotFoundException {
        var userIdentifier = randomUri();
        var personPreferences = new PersonPreferencesDao.Builder()
            .withPersonId(userIdentifier)
            .withPromotedPublications(List.of(randomUri()))
            .build()
            .upsert(preferencesService);

        var persistedpersonPreferences = new PersonPreferencesDao.Builder()
            .withPersonId(userIdentifier)
            .build()
            .fetch(preferencesService);
        assertThat(persistedpersonPreferences, is(equalTo(personPreferences)));
    }

    @Test
    void shouldThrowExceptionWhenFetchingNonExistentPersonPreferences() {
        assertThrows(NotFoundException.class,
            () -> new PersonPreferencesDao.Builder()
                .withPersonId(randomUri())
                .build()
                .fetch(preferencesService));
    }
}
