package no.sikt.nva.person.preferences.commons.model;

import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import no.unit.nva.commons.json.JsonUtils;
import org.junit.jupiter.api.Test;

public class PersonPreferencesTest {

    @Test
    void shouldMakeRoundTripWithoutLossOfInformation() throws JsonProcessingException {
        var userPreferences = randomUserPreferences();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(userPreferences);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, PersonPreferences.class);
        assertThat(userPreferences, is(equalTo(regeneratedObject)));
    }

    @Test
    void shouldMakeCopyOfUserPreferences() {
        var userPreferences = randomUserPreferences();
        var copy = randomUserPreferences().copy()
                       .withPromotedPublication(List.of())
                       .build();
        assertThat(userPreferences, is(not(equalTo(copy))));
    }

    private PersonPreferences randomUserPreferences() {
        return new PersonPreferences.Builder()
                   .withId(randomUri())
                   .withPromotedPublication(List.of(randomString()))
                   .build();
    }
}
