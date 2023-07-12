package no.sikt.nva.person.preferences.service;

import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.amazonaws.services.kms.model.NotFoundException;
import java.util.List;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.service.PreferencesService;
import no.sikt.nva.person.preferences.test.support.LocalPreferencesTestDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PreferencesServiceTest extends LocalPreferencesTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    private PreferencesService preferencesService;

    @BeforeEach
    void initialize() {
        super.init(TABLE_NAME);
        this.preferencesService = new PreferencesService(client, TABLE_NAME);
    }

    @Test
    void shouldPersistUserPreferences() {
        var personPreferences = new PersonPreferences.Builder(preferencesService)
                                    .withId(randomUri())
                                    .withPromotedPublication(List.of(randomString()))
                                    .build()
                                    .create();
        var persistedpersonPreferences = preferencesService
                                             .getPreferencesByIdentifier(personPreferences.id());
        assertThat(persistedpersonPreferences, is(equalTo(personPreferences)));
    }

    @Test
    void shouldUpdateExistingUserPreferences() {
        var userIdentifier = randomUri();
        var personPreferences = new PersonPreferences.Builder(preferencesService)
                                    .withId(userIdentifier)
                                    .withPromotedPublication(List.of(randomString()))
                                    .build()
                                    .create();

        new PersonPreferences(userIdentifier, List.of(), preferencesService).update();

        var persistedPreferences = preferencesService.getPreferencesByIdentifier(personPreferences.id());
        assertThat(persistedPreferences.promotedPublications(), is(emptyIterable()));
    }

    @Test
    void shouldThrowExceptionWhenFetchingNonExistentPersonPreferences() {
        var personPreferences = new PersonPreferences.Builder()
                                    .withId(randomUri())
                                    .withPromotedPublication(List.of(randomString()))
                                    .build();
        assertThrows(NotFoundException.class,
                     () -> preferencesService.getPreferencesByIdentifier(personPreferences.id()));
    }
}
