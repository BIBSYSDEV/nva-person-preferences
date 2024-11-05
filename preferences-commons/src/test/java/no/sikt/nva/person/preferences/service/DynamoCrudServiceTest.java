package no.sikt.nva.person.preferences.service;

import no.sikt.nva.person.preferences.commons.model.TermsConditionsDao;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import no.sikt.nva.person.preferences.commons.service.DynamoCrudService;
import no.sikt.nva.person.preferences.test.support.DynamoDbTableCreator;
import no.sikt.nva.person.preferences.test.support.DynamoDbTestClientProvider;
import nva.commons.apigateway.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.List;

import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DynamoCrudServiceTest {

    public static final String TABLE_NAME = "nonExistentTableName";
    private static DynamoCrudService<TermsConditionsDao> termsConditionsService;
    private static DynamoCrudService<PersonPreferencesDao> personPreferenceService;


    @BeforeAll
    static void initialize() {
        var client = DynamoDbTestClientProvider
                .geClient();
        new DynamoDbTableCreator(client)
                .createTable(TABLE_NAME);

        termsConditionsService = new DynamoCrudService<>(client, TABLE_NAME, TermsConditionsDao.class);
        personPreferenceService = new DynamoCrudService<>(client, TABLE_NAME, PersonPreferencesDao.class);
    }

    @Test
    void shouldPersistUserPreferences() throws NotFoundException {
        var personPreferences = PersonPreferencesDao.builder()
                .personId(randomUri())
                .promotedPublications(List.of(randomUri()))
                .build()
                .upsert(personPreferenceService);

        var fetched = PersonPreferencesDao.builder()
                .personId(personPreferences.personId())
                .build()
                .fetch(personPreferenceService);
        assertThat(fetched, is(equalTo(personPreferences)));
    }

    @Test
    void shouldTransactionalPersistPreferences() throws NotFoundException {

        var pref1 = PersonPreferencesDao.builder()
                .personId(randomUri())
                .promotedPublications(List.of(randomUri()))
                .build();

        var pref2 = PersonPreferencesDao.builder()
                .personId(randomUri())
                .promotedPublications(List.of(randomUri()))
                .build();
        var pref3 = PersonPreferencesDao.builder()
                .personId(randomUri())
                .promotedPublications(List.of(randomUri()))
                .build();

        personPreferenceService.transactionalPersist(pref3, pref2, pref1);

        var fetched = pref2
                .fetch(personPreferenceService);
        assertThat(fetched, is(equalTo(pref2)));
    }


    @Test
    void shouldTransactionalPersistEmptyPreferences() {
        assertThrows(AwsServiceException.class,
                ()-> personPreferenceService.transactionalPersist());
        assertThrows(AwsServiceException.class,
                ()-> personPreferenceService.transactionalPersist(null));
    }

    @Test
    void shouldPersistPreferencesAndLicense() throws NotFoundException {
        var persistedPreferences = PersonPreferencesDao.builder()
                .personId(randomUri())
                .promotedPublications(List.of(randomUri()))
                .build()
                .upsert(personPreferenceService);

        var persistedTermsConditions = TermsConditionsDao.builder()
                .personId(persistedPreferences.personId())
                .termsConditionsUri(randomUri())
                .build()
                .upsert(termsConditionsService);

        assertThat(persistedTermsConditions.personId(), is(equalTo(persistedPreferences.personId())));

        var fetchedPreferences = persistedPreferences
                .fetch(personPreferenceService);
        var fetchedTermsConditions = persistedTermsConditions
                .fetch(termsConditionsService);

        assertThat(fetchedPreferences, is(equalTo(persistedPreferences)));
        assertThat(fetchedTermsConditions, is(equalTo(persistedTermsConditions)));

    }


    @Test
    void shouldUpdateExistingUserPreferences() throws NotFoundException {
        var userIdentifier = randomUri();
        var personPreferences = PersonPreferencesDao
                .builder()
                .personId(userIdentifier)
                .promotedPublications(List.of(randomUri()))
                .build()
                .upsert(personPreferenceService);

        assertThat(personPreferences.promotedPublications(), hasSize(1));

        var persistedPreferences = PersonPreferencesDao
                .builder()
                .personId(userIdentifier)
                .build()
                .upsert(personPreferenceService).toDto();

        assertThat(persistedPreferences.promotedPublications(), is(emptyIterable()));
    }

    @Test
    void shouldUpdateExistingUserPreferences2() throws NotFoundException {
        var userIdentifier = randomUri();
        var takeOne = PersonPreferencesDao
                .builder()
                .personId(userIdentifier)
                .promotedPublications(List.of(randomUri()))
                .build()
                .upsert(personPreferenceService);

        assertThat(takeOne.promotedPublications(), hasSize(1));

        var takeTwo = PersonPreferencesDao.builder()
                .personId(userIdentifier)
                .build()
                .upsert(personPreferenceService);

        assertNull(takeTwo.promotedPublications());
    }

    @Test
    void shouldUpdateTermsConditions() throws NotFoundException {
        var userIdentifier = randomUri();
        var termsConditionsDao = TermsConditionsDao.builder()
                .personId(userIdentifier)
                .termsConditionsUri(randomUri())
                .build()
                .upsert(termsConditionsService);

        var termsConditions = TermsConditionsDao.builder()
                .personId(userIdentifier)
                .build()
                .fetch(termsConditionsService);


        assertThat(termsConditionsDao, is(equalTo(termsConditions)));
    }

    @Test
    void shouldFetchUserPreferences() throws nva.commons.apigateway.exceptions.NotFoundException {
        var userIdentifier = randomUri();
        var personPreferences = PersonPreferencesDao.builder()
                .personId(userIdentifier)
                .promotedPublications(List.of(randomUri()))
                .build()
                .upsert(personPreferenceService);

        var persistedpersonPreferences = PersonPreferencesDao.builder()
                .personId(userIdentifier)
                .build()
                .fetch(personPreferenceService);
        assertThat(persistedpersonPreferences, is(equalTo(personPreferences)));
    }

    @Test
    void shouldDeleteUserPreferences() throws NotFoundException {
        var userIdentifier = randomUri();
        var personPreferences = PersonPreferencesDao.builder()
                .personId(userIdentifier)
                .promotedPublications(List.of(randomUri()))
                .build()
                .upsert(personPreferenceService);

        personPreferenceService.delete(personPreferences);

        assertThrows(NotFoundException.class,
                () -> PersonPreferencesDao.builder()
                        .personId(userIdentifier)
                        .build()
                        .fetch(personPreferenceService));
    }

    @Test
    void shouldDeleteTermsConditions() throws NotFoundException {
        var userIdentifier = randomUri();
        var termsConditions = TermsConditionsDao.builder()
                .personId(userIdentifier)
                .termsConditionsUri(randomUri())
                .build()
                .upsert(termsConditionsService);

        termsConditionsService.delete(termsConditions);

        assertThrows(NotFoundException.class,
                () -> TermsConditionsDao.builder()
                        .personId(userIdentifier)
                        .build()
                        .fetch(termsConditionsService));
    }

    @Test
    void shouldThrowExceptionWhenFetchingNonExistentPersonPreferences() {
        assertThrows(NotFoundException.class,
                () -> PersonPreferencesDao.builder()
                        .personId(randomUri())
                        .build()
                        .fetch(personPreferenceService));
    }

    @Test
    void shouldThrowExceptionWhenFetchingNonExistentTermsConditions() {
        assertThrows(NotFoundException.class,
                () -> TermsConditionsDao.builder()
                        .personId(randomUri())
                        .build()
                        .fetch(termsConditionsService));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentPersonPreferences() {
        var dao = PersonPreferencesDao.builder()
                .personId(randomUri())
                .build();
        assertThrows(NotFoundException.class,
                () -> personPreferenceService.delete(dao));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTermsConditions() {
        var dao = TermsConditionsDao.builder()
                .personId(randomUri())
                .build();
        assertThrows(NotFoundException.class,
                () -> termsConditionsService.delete(dao));
    }
}
