package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import no.sikt.nva.person.preferences.test.support.LocalPreferencesTestDatabase;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import nva.commons.apigateway.exceptions.NotFoundException;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static no.sikt.nva.person.preferences.commons.service.PersonService.RESOURCE_NOT_FOUND_MESSAGE;
import static no.sikt.nva.person.preferences.rest.PersonPreferencesRestHandlersTestConfig.restApiMapper;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

class FetchPersonPreferencesHandlerTest extends LocalPreferencesTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    public static final String CRISTIN_ID = "cristinId";
    private static final Context CONTEXT = mock(Context.class);
    private ByteArrayOutputStream output;
    private PersonService personPreferencesService;
    private FetchPersonPreferencesHandler handler;

    @BeforeEach
    public void init() {
        super.init(TABLE_NAME);
        output = new ByteArrayOutputStream();
        personPreferencesService = new PersonService(client, TABLE_NAME);
        handler = new FetchPersonPreferencesHandler(personPreferencesService);
    }

    @Test
    void shouldFetchPersonPreferences() throws IOException, NotFoundException {
        var personPreferences = profileWithCristinIdentifier(randomUri())
            .upsert(personPreferencesService).toDto();
        var request = createRequest(personPreferences.personId());

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, PersonPreferences.class);

        assertThat(personPreferences, is(equalTo(response.getBodyObject(PersonPreferences.class))));
    }

    private PersonPreferencesDao profileWithCristinIdentifier(URI cristinIdentifier) {
        return new PersonPreferencesDao.Builder()
            .withPersonId(cristinIdentifier)
            .withPromotedPublications(List.of(randomUri(), randomUri()))
            .build();
    }

    private InputStream createRequest(URI identifier) throws JsonProcessingException {
        var pathParameters = Map.of(CRISTIN_ID, identifier.toString());
        var headers = Map.of(ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        return new HandlerRequestBuilder<InputStream>(restApiMapper)
            .withHeaders(headers)
            .withPathParameters(pathParameters)
            .build();
    }

    @Test
    void shouldReturnNotFoundWhenPersonIdentifierDoesNotExist() throws IOException {
        var request = createRequest(randomUri());
        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Problem.class);
        var detail = response.getBodyObject(Problem.class).getDetail();

        assertThat(response.getStatusCode(), is(equalTo(HTTP_NOT_FOUND)));
        assertThat(detail, containsString(RESOURCE_NOT_FOUND_MESSAGE));
    }
}