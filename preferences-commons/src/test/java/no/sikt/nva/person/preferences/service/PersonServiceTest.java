package no.sikt.nva.person.preferences.service;

import no.sikt.nva.person.preferences.commons.model.LicenseInfoDao;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import no.sikt.nva.person.preferences.commons.service.IndexService;

import no.sikt.nva.person.preferences.test.support.DynamoDbTableCreator;
import no.sikt.nva.person.preferences.test.support.DynamoDbTestClientProvider;
import nva.commons.apigateway.exceptions.NotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonServiceTest {

    public static final String TABLE_NAME = "nonExistentTableName";
    private static IndexService<LicenseInfoDao> licenseInfoService;
    private static IndexService<PersonPreferencesDao> personPreferenceService;


    @AfterAll
    public static void shutdown() {
//        client.close();
    }
    @BeforeAll
    static void initialize() {
        DynamoDbClient client = DynamoDbTestClientProvider.geClient();
        new DynamoDbTableCreator(client).createTable(TABLE_NAME);
        licenseInfoService = new IndexService<>(client, TABLE_NAME, LicenseInfoDao.class);
        personPreferenceService = new IndexService<>(client, TABLE_NAME, PersonPreferencesDao.class);
    }

    @Test
    void shouldPersistUserPreferences() throws NotFoundException {
        var personPreferences = PersonPreferencesDao.builder()
            .withId(randomUri())
            .promotedPublications(List.of(randomUri()))
            .build()
            .upsert(personPreferenceService);

        var persistedpersonPreferences = PersonPreferencesDao.builder()
            .withId(personPreferences.withId())
            .build()
            .fetch(personPreferenceService);
        assertThat(persistedpersonPreferences, is(equalTo(personPreferences)));
    }

    @Test
    void shouldPersistPreferencesAndLicense() throws NotFoundException {
        var persistedPreferences = PersonPreferencesDao.builder()
                .withId(randomUri())
                .promotedPublications(List.of(randomUri()))
                .build()
                .upsert(personPreferenceService);

        var persistedLicenseInfo = LicenseInfoDao.builder()
                .withId(persistedPreferences.withId())
                .licenseUri(randomUri())
                .build()
                .upsert(licenseInfoService);

        assertThat(persistedLicenseInfo.withId(), is(equalTo(persistedPreferences.withId())));

        var fetchedPreferences = PersonPreferencesDao.builder()
                .withId(persistedPreferences.withId())
                .build()
                .fetch(personPreferenceService);
        var fetchedLicenseInfo = LicenseInfoDao.builder()
                .withId(persistedPreferences.withId())
                .build()
                .fetch(licenseInfoService);

        assertThat(fetchedPreferences, is(equalTo(persistedPreferences)));
        assertThat(fetchedLicenseInfo, is(equalTo(persistedLicenseInfo)));

    }


    @Test
    void shouldUpdateExistingUserPreferences() throws NotFoundException {
        var userIdentifier = randomUri();
        var personPreferences = PersonPreferencesDao
            .builder()
            .withId(userIdentifier)
            .promotedPublications(List.of(randomUri()))
            .build()
            .upsert(personPreferenceService);

        assertThat(personPreferences.promotedPublications(), hasSize(1));

        var persistedPreferences = PersonPreferencesDao
                .builder()
            .withId(userIdentifier)
            .build()
            .upsert(personPreferenceService).toDto();

        assertThat(persistedPreferences.promotedPublications(), is(emptyIterable()));
    }

    @Test
    void shouldUpdateExistingUserPreferences2() throws NotFoundException {
        var userIdentifier = randomUri();
        var takeOne = PersonPreferencesDao
                .builder()
            .withId(userIdentifier)
            .promotedPublications(List.of(randomUri()))
            .build()
            .upsert(personPreferenceService);

        assertThat(takeOne.promotedPublications(), hasSize(1));

        var takeTwo = PersonPreferencesDao.builder()
            .withId(userIdentifier)
            .build()
            .upsert(personPreferenceService);

        assertNull(takeTwo.promotedPublications());
    }

    @Test
    void shouldUpdateLicenseInfo() throws NotFoundException {
        var userIdentifier = randomUri();
        var licenseInfoDao = LicenseInfoDao.builder()
            .withId(userIdentifier)
            .licenseUri(randomUri())
            .build()
            .upsert(licenseInfoService);

        var licenseInfo = LicenseInfoDao.builder()
                .withId(userIdentifier)
                .build()
                .fetch(licenseInfoService);


        assertThat(licenseInfoDao, is(equalTo(licenseInfo)));
    }

    @Test
    void shouldFetchUserPreferences() throws nva.commons.apigateway.exceptions.NotFoundException {
        var userIdentifier = randomUri();
        var personPreferences = PersonPreferencesDao.builder()
            .withId(userIdentifier)
            .promotedPublications(List.of(randomUri()))
            .build()
            .upsert(personPreferenceService);

        var persistedpersonPreferences = PersonPreferencesDao.builder()
            .withId(userIdentifier)
            .build()
            .fetch(personPreferenceService);
        assertThat(persistedpersonPreferences, is(equalTo(personPreferences)));
    }

    @Test
    void shouldThrowExceptionWhenFetchingNonExistentPersonPreferences() {
        assertThrows(NotFoundException.class,
            () -> PersonPreferencesDao.builder()
                .withId(randomUri())
                .build()
                .fetch(personPreferenceService));
    }
}
