package no.sikt.nva.person.preferences.rest;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static no.sikt.nva.person.preferences.rest.FetchPersonPreferencesHandler.PERSON_PREFERENCES_NOT_FOUND_MESSAGE;
import static no.sikt.nva.person.preferences.rest.PersonPreferencesRestHandlersTestConfig.restApiMapper;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;
import no.sikt.nva.person.preferences.test.support.LocalPreferencesTestDatabase;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

class FetchPersonPreferencesHandlerTest extends LocalPreferencesTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    public static final String IDENTIFIER = "personId";
    private static final Context CONTEXT = mock(Context.class);
    private ByteArrayOutputStream output;
    private PersonPreferencesService personPreferencesService;
    private FetchPersonPreferencesHandler handler;

    @BeforeEach
    public void init() {
        super.init(TABLE_NAME);
        output = new ByteArrayOutputStream();
        personPreferencesService = new PersonPreferencesService(client, TABLE_NAME);
        handler = new FetchPersonPreferencesHandler(personPreferencesService);
    }

    @Test
    void shouldFetchPersonPreferences() throws IOException {
        var personPreferences = profileWithCristinIdentifier(randomUri()).create();
        var request = createRequest(personPreferences.personId());

        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, PersonPreferences.class);

        assertThat(personPreferencesService.getPreferencesByPersonId(personPreferences.personId()),
                   is(equalTo(response.getBodyObject(PersonPreferences.class))));
    }

    @Test
    void shouldReturnNotFoundWhenPersonIdentifierDoesNotExist() throws IOException {
        var request = createRequest(randomUri());
        handler.handleRequest(request, output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Problem.class);
        var detail = response.getBodyObject(Problem.class).getDetail();

        assertThat(response.getStatusCode(), is(equalTo(HTTP_NOT_FOUND)));
        assertThat(detail, containsString(PERSON_PREFERENCES_NOT_FOUND_MESSAGE));
    }

    private PersonPreferences profileWithCristinIdentifier(URI cristinIdentifier) {
        return new PersonPreferences.Builder(personPreferencesService)
            .withPersonId(cristinIdentifier)
            .withPromotedPublications(List.of(randomUri(), randomUri()))
            .build();
    }

    private InputStream createRequest(URI identifier) throws JsonProcessingException {
        var pathParameters = Map.of(IDENTIFIER, identifier.toString());
        var headers = Map.of(ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        return new HandlerRequestBuilder<InputStream>(restApiMapper)
            .withHeaders(headers)
            .withPathParameters(pathParameters)
            .build();
    }
}