package no.sikt.nva.person.preferences.commons.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.unit.nva.commons.json.JsonUtils;
import org.junit.jupiter.api.Test;

import static no.unit.nva.testutils.RandomDataGenerator.randomInstant;
import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TermsConditionsDtoTest {

    @Test
    void shouldMakeRoundTripWithoutLossOfInformation() throws JsonProcessingException {
        var randomLicenseInfo = randomLicenseInfo();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(randomLicenseInfo);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, TermsConditionsDto.class);
        assertThat(randomLicenseInfo, is(equalTo(regeneratedObject)));
    }

    @Test
    void shouldMakeRoundTripWithoutLossOfInformationWhenLicenseInfoIsCreatedFromDao() throws JsonProcessingException {
        var randomLicenseInfoDao = randomLicenseInfoDao();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(randomLicenseInfoDao);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, TermsConditionsDao.class);
        assertThat(randomLicenseInfoDao, is(equalTo(regeneratedObject)));
    }

    @Test
    void shouldMakeRoundTripWithoutLossOfInformationWhenLicenseInfoIsCreatedFromDaoAndBack()
            throws JsonProcessingException {
        var randomLicenseInfoDao = randomLicenseInfoDao();
        var licenseInfo = randomLicenseInfoDao.toDto();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(licenseInfo);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, TermsConditionsDto.class);
        assertThat(licenseInfo, is(equalTo(regeneratedObject)));
    }

    private TermsConditionsDto randomLicenseInfo() {
        return new TermsConditionsDto.Builder()
                .withPersonId(randomUri())
                .withLicenseSignedDate(randomInstant())
                .withLicenseUri(randomUri())
                .build();
    }

    private TermsConditionsDao randomLicenseInfoDao() {
        var lostInstant = randomInstant();
        return TermsConditionsDao.builder()
                .personId(randomUri())
                .created(lostInstant)
                .modified(lostInstant)
                .withType(randomString())
                .licenseUri(randomUri())
                .build();
    }
}
