package no.sikt.nva.person.preferences.commons.model;

import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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
    void shouldMakeRoundTripWithoutLossOfInformationWhenPersonPreferencesIsCreatedFromDao() throws JsonProcessingException {
        var personPreferencesDao = randomPersonPreferencesDao();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(personPreferencesDao);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, PersonPreferencesDao.class);
        assertThat(personPreferencesDao, is(equalTo(regeneratedObject)));
    }

    @Test
    void shouldMakeRoundTripWithoutLossOfInformationWhenPersonPreferencesIsCreatedFromDaoAndBack() throws JsonProcessingException {
        var personPreferencesDao = randomPersonPreferencesDao();
        var personPreferences = personPreferencesDao.toDto();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(personPreferences);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, PersonPreferences.class);
        assertThat(personPreferences, is(equalTo(regeneratedObject)));
        var regeneratedDao = personPreferences.toDao();
        assertThat(personPreferencesDao.personId(), is(equalTo(regeneratedDao.personId())));
        assertThat(personPreferencesDao.promotedPublications(), is(equalTo(regeneratedDao.promotedPublications())));
    }

    private PersonPreferences randomPersonPreferences() {
        return new PersonPreferences.Builder()
                   .withPersonId(randomUri())
                   .withPromotedPublications(List.of(randomUri()))
                   .build();
    }

    private PersonPreferencesDao randomPersonPreferencesDao() {
        return new PersonPreferencesDao.Builder()
                   .withPersonId(randomUri())
                   .withPromotedPublications(List.of(randomUri()))
                   .build();
    }
}
