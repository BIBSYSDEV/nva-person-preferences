package no.sikt.nva.person.licenceinfo.rest;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.sikt.nva.person.preferences.commons.model.LicenseInfoDao;
import no.sikt.nva.person.preferences.commons.model.LicenseInfoDto;
import no.sikt.nva.person.preferences.commons.service.IndexService;
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
import java.net.URI;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static no.sikt.nva.person.Constants.CRISTIN_ID;
import static no.sikt.nva.person.preferences.commons.model.DataAccessService.RESOURCE_NOT_FOUND_MESSAGE;
import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;
import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

class FetchLicenseInfoDtoHandlerTest extends LocalPreferencesTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    private static final Context CONTEXT = mock(Context.class);
    private ByteArrayOutputStream output;
    private IndexService<LicenseInfoDao> personPreferencesService;
    private FetchLicenseInfoHandler handler;

    @BeforeEach
    public void init() {
        super.init(TABLE_NAME);
        output = new ByteArrayOutputStream();
        personPreferencesService = new IndexService<>(client, TABLE_NAME, LicenseInfoDao.class);
        handler = new FetchLicenseInfoHandler(personPreferencesService);
    }

    @Test
    void shouldFetchLicenseInfo() throws IOException, NotFoundException {
        var personPreferences = profileWithCristinIdentifier(randomUri())
                .upsert(personPreferencesService)
                .toDto();

        var request = createRequest(personPreferences.personId());

        handler.handleRequest(request, output, CONTEXT);

        var response = GatewayResponse.fromOutputStream(output, LicenseInfoDto.class);

        assertThat(personPreferences, is(equalTo(response.getBodyObject(LicenseInfoDto.class))));
    }

    private LicenseInfoDao profileWithCristinIdentifier(URI cristinIdentifier) {
        return LicenseInfoDao.builder()
                .withId(cristinIdentifier)
                .licenseUri(randomUri())
                .build();
    }

    private InputStream createRequest(URI identifier) throws JsonProcessingException {
        var pathParameters = Map.of(CRISTIN_ID, identifier.toString());
        // var headers = Map.of(ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        return new HandlerRequestBuilder<LicenseInfoDto>(dtoObjectMapper)
                .withUserName(randomString())
                .withPersonCristinId(identifier)
                .withCurrentCustomer(randomUri())
                .withPathParameters(pathParameters)
                .build();
    }

    @Test
    void shouldReturnNotFoundWhenPersonIdentifierDoesNotExist() throws IOException, NotFoundException {
        profileWithCristinIdentifier(randomUri())
                .upsert(personPreferencesService);

        var request = createRequest(randomUri());
        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Problem.class);
        var detail = response.getBodyObject(Problem.class).getDetail();

        assertThat(response.getStatusCode(), is(equalTo(HTTP_NOT_FOUND)));
        assertThat(detail, containsString(RESOURCE_NOT_FOUND_MESSAGE));
    }
}