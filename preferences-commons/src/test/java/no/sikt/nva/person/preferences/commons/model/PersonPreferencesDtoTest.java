package no.sikt.nva.person.preferences.commons.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.unit.nva.commons.json.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.unit.nva.testutils.RandomDataGenerator.randomInstant;
import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class PersonPreferencesDtoTest {

    @Test
    void shouldMakeRoundTripWithoutLossOfInformation() throws JsonProcessingException {
        var personPreferences = randomPersonPreferences();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(personPreferences);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, PersonPreferencesDto.class);
        assertThat(personPreferences, is(equalTo(regeneratedObject)));
    }

    @Test
    void shouldMakeRoundTripWithoutLossOfInformationWhenPersonPreferencesIsCreatedFromDao()
            throws JsonProcessingException {
        var personPreferencesDao = randomPersonPreferencesDao();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(personPreferencesDao);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, PersonPreferencesDao.class);
        assertThat(personPreferencesDao, is(equalTo(regeneratedObject)));
    }

    @Test
    void shouldMakeRoundTripWithoutLossOfInformationWhenPersonPreferencesIsCreatedFromDaoAndBack()
            throws JsonProcessingException {
        var personPreferencesDao = randomPersonPreferencesDao();
        var personPreferences = personPreferencesDao.toDto();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(personPreferences);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, PersonPreferencesDto.class);
        assertThat(personPreferences, is(equalTo(regeneratedObject)));
        var regeneratedDao = personPreferences.toDao();
        assertThat(personPreferencesDao.personId(), is(equalTo(regeneratedDao.personId())));
        assertThat(personPreferencesDao.promotedPublications(), is(equalTo(regeneratedDao.promotedPublications())));
    }

    private PersonPreferencesDto randomPersonPreferences() {
        return new PersonPreferencesDto.Builder()
                .withPersonId(randomUri())
                .withPromotedPublications(List.of(randomUri()))
                .build();
    }

    private PersonPreferencesDao randomPersonPreferencesDao() {
        var lostInstant = randomInstant();
        return PersonPreferencesDao.builder()
                .personId(randomUri())
                .promotedPublications(List.of(randomUri()))
                .created(lostInstant)
                .modified(lostInstant)
                .withType(randomString())
                .build();
    }
}
