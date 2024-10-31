package no.sikt.nva.person.preferences.commons.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.unit.nva.commons.json.JsonUtils;
import org.junit.jupiter.api.Test;

import static no.unit.nva.testutils.RandomDataGenerator.randomInstant;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class LicenseInfoTest {

    @Test
    void shouldMakeRoundTripWithoutLossOfInformation() throws JsonProcessingException {
        var randomLicenseInfo = randomLicenseInfo();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(randomLicenseInfo);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, LicenseInfo.class);
        assertThat(randomLicenseInfo, is(equalTo(regeneratedObject)));
    }

    @Test
    void shouldMakeRoundTripWithoutLossOfInformationWhenLicenseInfoIsCreatedFromDao() throws JsonProcessingException {
        var randomLicenseInfoDao = randomLicenseInfoDao();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(randomLicenseInfoDao);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, LicenseInfoDao.class);
        assertThat(randomLicenseInfoDao, is(equalTo(regeneratedObject)));
    }

    @Test
    void shouldMakeRoundTripWithoutLossOfInformationWhenLicenseInfoIsCreatedFromDaoAndBack() throws JsonProcessingException {
        var randomLicenseInfoDao = randomLicenseInfoDao();
        var licenseInfo = randomLicenseInfoDao.toDto();
        var objectAsString = JsonUtils.dtoObjectMapper.writeValueAsString(licenseInfo);
        var regeneratedObject = JsonUtils.dtoObjectMapper.readValue(objectAsString, LicenseInfo.class);
        assertThat(licenseInfo, is(equalTo(regeneratedObject)));
    }

    private LicenseInfo randomLicenseInfo() {
        return new LicenseInfo.Builder()
                .withPersonId(randomUri())
                .withLicenseSignedDate(randomInstant())
                .withLicenseUri(randomUri())
                .build();
    }

    private LicenseInfoDao randomLicenseInfoDao() {
        return new LicenseInfoDao.Builder()
                .withPersonId(randomUri())
                .withLicenseUri(randomUri())
                .build();
    }
}
