package no.sikt.nva.person.preferences.commons.model;

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
        var personPreferences = randomPersonPreferences();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(personPreferences);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, PersonPreferences.class);
        assertThat(personPreferences, is(equalTo(regeneratedObject)));
    }

    @Test
    void shouldCreateCopy() {
        var personPreferences = randomPersonPreferences();
        var copy = personPreferences.copy()
                       .withPersonId(personPreferences.personId())
                       .withPromotedPublications(List.of())
                       .build();
        assertThat(personPreferences, is(not(equalTo(copy))));
    }

    private PersonPreferences randomPersonPreferences() {
        return new PersonPreferences.Builder()
                   .withPersonId(randomUri())
                   .withPromotedPublications(List.of(randomUri()))
                   .build();
    }
}
