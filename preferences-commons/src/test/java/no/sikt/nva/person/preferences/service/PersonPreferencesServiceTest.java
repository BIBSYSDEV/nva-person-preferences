package no.sikt.nva.person.preferences.service;

import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.amazonaws.services.kms.model.NotFoundException;
import java.time.Instant;
import java.util.List;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;
import no.sikt.nva.person.preferences.test.support.LocalPreferencesTestDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersonPreferencesServiceTest extends LocalPreferencesTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    private PersonPreferencesService preferencesService;

    @BeforeEach
    void initialize() {
        super.init(TABLE_NAME);
        this.preferencesService = new PersonPreferencesService(client, TABLE_NAME);
    }

    @Test
    void shouldPersistUserPreferences() {
        var personPreferences = new PersonPreferences.Builder(preferencesService)
                                    .withPersonId(randomUri())
                                    .withPromotedPublications(List.of(randomUri()))
                                    .withCreated(Instant.now())
                                    .withModified(Instant.now())
                                    .build()
                                    .create();
        var persistedpersonPreferences = preferencesService
                                             .getPreferencesByPersonId(personPreferences.personId());
        assertThat(persistedpersonPreferences, is(equalTo(personPreferences)));
    }

    @Test
    void shouldUpdateExistingUserPreferences() {
        var userIdentifier = randomUri();
        var personPreferences = new PersonPreferences.Builder(preferencesService)
                                    .withPersonId(userIdentifier)
                                    .withPromotedPublications(List.of(randomUri()))
                                    .build()
                                    .create();

        new PersonPreferences(userIdentifier, List.of(), preferencesService, null, null).update();

        var persistedPreferences = preferencesService.getPreferencesByPersonId(personPreferences.personId());
        assertThat(persistedPreferences.promotedPublications(), is(emptyIterable()));
    }

    @Test
    void shouldThrowExceptionWhenFetchingNonExistentPersonPreferences() {
        var personPreferences = new PersonPreferences.Builder()
                                    .withPersonId(randomUri())
                                    .withPromotedPublications(List.of(randomUri()))
                                    .build();
        assertThrows(NotFoundException.class,
                     () -> preferencesService.getPreferencesByPersonId(personPreferences.personId()));
    }
}
