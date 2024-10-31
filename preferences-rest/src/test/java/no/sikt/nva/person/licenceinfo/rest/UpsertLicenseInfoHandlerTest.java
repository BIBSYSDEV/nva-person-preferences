package no.sikt.nva.person.licenceinfo.rest;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.sikt.nva.person.preferences.commons.model.LicenseInfo;
import no.sikt.nva.person.preferences.commons.model.LicenseInfoDao;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import no.sikt.nva.person.preferences.test.support.LocalPreferencesTestDatabase;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import nva.commons.apigateway.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;
import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class UpsertLicenseInfoHandlerTest extends LocalPreferencesTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    private static final Context CONTEXT = mock(Context.class);
    private ByteArrayOutputStream output;
    private PersonService personPreferencesService;
    private UpsertLicenseInfoHandler handler;

    @BeforeEach
    public void init() {
        super.init(TABLE_NAME);
        output = new ByteArrayOutputStream();
        personPreferencesService = new PersonService(client, TABLE_NAME);
        handler = new UpsertLicenseInfoHandler(personPreferencesService);
    }

    @Test
    void shouldCreateLicenseInfoWhenDoesNotExist() throws IOException {
        var existingLicenseInfo = profileWithCristinIdentifier(randomUri());

        handler.handleRequest(createRequest(existingLicenseInfo.toDto()), output, CONTEXT);

        var response = GatewayResponse.fromOutputStream(output, LicenseInfo.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_OK)));
        assertThat(response.getBodyObject(LicenseInfo.class).personId(),
                is(equalTo(existingLicenseInfo.personId())));
    }

    private InputStream createRequest(LicenseInfo personPreferences) throws JsonProcessingException {
        return new HandlerRequestBuilder<LicenseInfo>(dtoObjectMapper)
                .withUserName(randomString())
                .withPersonCristinId(personPreferences.personId())
                .withCurrentCustomer(personPreferences.personId())
                .withPathParameters(Map.of("cristinId", personPreferences.personId().toString()))
                .withBody(personPreferences)
                .build();
    }

    private LicenseInfoDao profileWithCristinIdentifier(URI cristinIdentifier) {
        return new LicenseInfoDao.Builder()
                .withPersonId(cristinIdentifier)
                .withLicenseUri(randomUri())
                .build();
    }

    @Test
    void shouldUpdateLicenseInfoWhenExist() throws IOException, NotFoundException {
        var existingLicenseInfo = profileWithCristinIdentifier(randomUri())
                .upsert(personPreferencesService).toDto();

        var updateLicenseInfo = new LicenseInfo.Builder()
                .withPersonId(existingLicenseInfo.personId())
                .build();

        handler.handleRequest(createRequest(updateLicenseInfo), output, CONTEXT);

        var response = GatewayResponse.fromOutputStream(output, LicenseInfo.class);
        var person = response.getBodyObject(LicenseInfo.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_OK)));

        assertThat(person.personId(), is(equalTo(updateLicenseInfo.personId())));
    }

    @Test
    void shouldReturnUnauthorizedWhenNonAuthorized() throws IOException {
        var profile = profileWithCristinIdentifier(randomUri()).toDto();
        handler.handleRequest(createUnauthorizedRequest(profile), output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Problem.class);

        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_UNAUTHORIZED)));
    }

    private InputStream createUnauthorizedRequest(LicenseInfo personPreferences) throws JsonProcessingException {
        return new HandlerRequestBuilder<LicenseInfo>(dtoObjectMapper)
                .withUserName(randomString())
                .withBody(personPreferences)
                .build();
    }

    @Test
    void shouldReturnUnauthorizedWhenProvidedCristinIdDoesNotMatch() throws IOException {
        var profile = profileWithCristinIdentifier(randomUri()).toDto();
        handler.handleRequest(createRequestWithNotMatchingCristinIds(profile), output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Problem.class);

        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_UNAUTHORIZED)));
    }

    private InputStream createRequestWithNotMatchingCristinIds(LicenseInfo personPreferences)
            throws JsonProcessingException {
        return new HandlerRequestBuilder<LicenseInfo>(dtoObjectMapper)
                .withUserName(randomString())
                .withCurrentCustomer(randomUri())
                .withPersonCristinId(personPreferences.personId())
                .withCurrentCustomer(randomUri())
                .withPathParameters(Map.of("cristinId", randomString()))
                .withBody(personPreferences)
                .build();
    }
}
