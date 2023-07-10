package service;

import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.amazonaws.services.kms.model.NotFoundException;
import java.util.List;
import no.unit.nva.person.preferences.commons.model.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import no.unit.nva.person.preferences.commons.utils.UserProfileLocalTestDatabase;

public class ProfileServiceTest extends UserProfileLocalTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    private no.unit.nva.person.preferences.commons.service.ProfileService profileService;

    @BeforeEach
    void initialize() {
        super.init(TABLE_NAME);
        this.profileService = new no.unit.nva.person.preferences.commons.service.ProfileService(client, TABLE_NAME);
    }

    @Test
    void shouldPersistUserPreferences() {
        var profile = new Profile.Builder()
                          .withIdentifier(randomUri())
                          .withPromotedPublication(List.of(randomString()))
                          .build()
                          .create(profileService);
        var persistedProfile = profileService.getProfileByIdentifier(profile.identifier());
        assertThat(persistedProfile, is(equalTo(profile)));
    }

    @Test
    void shouldUpdateExistingUserPreferences() {
        var profile = new Profile.Builder()
                          .withIdentifier(randomUri())
                          .withPromotedPublication(List.of(randomString()))
                          .build()
                          .create(profileService);

        profile.copy()
            .withPromotedPublication(List.of())
            .build()
            .update(profileService);

        var peristedProfile = profileService.getProfileByIdentifier(
            profile.identifier());
        assertThat(peristedProfile.promotedPublications(), is(nullValue()));
    }

    @Test
    void shouldThrowExceptionWhenFetchingNonExistentProfile() {
        var profile = new Profile.Builder()
                                  .withIdentifier(randomUri())
                                  .withPromotedPublication(List.of(randomString()))
                                  .build();
        assertThrows(NotFoundException.class,
                     () -> profileService.getProfileByIdentifier(profile.identifier()));
    }
}
