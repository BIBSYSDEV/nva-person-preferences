package no.sikt.nva.person.preferences.service;

import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.amazonaws.services.kms.model.NotFoundException;
import java.util.List;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.service.PreferencesService;
import no.sikt.nva.person.preferences.test.support.LocalPreferencesTestDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicePreferencesTest extends LocalPreferencesTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    private PreferencesService preferencesService;

    @BeforeEach
    void initialize() {
        super.init(TABLE_NAME);
        this.preferencesService = new PreferencesService(client, TABLE_NAME);
    }

    @Test
    void shouldPersistUserPreferences() {
        var profile = new PersonPreferences.Builder()
                          .withId(randomUri())
                          .withPromotedPublication(List.of(randomString()))
                          .build()
                          .create(preferencesService);
        var persistedProfile = preferencesService.getPreferencesByIdentifier(profile.id());
        assertThat(persistedProfile, is(equalTo(profile)));
    }

    @Test
    void shouldUpdateExistingUserPreferences() {
        var profile = new PersonPreferences.Builder()
                          .withId(randomUri())
                          .withPromotedPublication(List.of(randomString()))
                          .build()
                          .create(preferencesService);

        profile.copy()
            .withPromotedPublication(List.of())
            .build()
            .update(preferencesService);

        var persistedPreferences = preferencesService.getPreferencesByIdentifier(profile.id());
        assertThat(persistedPreferences.promotedPublications(), is(nullValue()));
    }

    @Test
    void shouldThrowExceptionWhenFetchingNonExistentPersonPreferences() {
        var profile = new PersonPreferences.Builder()
                          .withId(randomUri())
                          .withPromotedPublication(List.of(randomString()))
                          .build();
        assertThrows(NotFoundException.class,
                     () -> preferencesService.getPreferencesByIdentifier(profile.id()));
    }
}
