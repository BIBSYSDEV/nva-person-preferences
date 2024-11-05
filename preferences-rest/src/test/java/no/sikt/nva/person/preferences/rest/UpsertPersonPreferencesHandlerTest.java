package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;
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
import java.util.List;
import java.util.Map;

import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;
import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class UpsertPersonPreferencesHandlerTest extends LocalPreferencesTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    private static final Context CONTEXT = mock(Context.class);
    private ByteArrayOutputStream output;
    private PersonPreferencesService personPreferencesService;
    private UpsertPersonPreferencesHandler handler;

    @BeforeEach
    public void init() {
        super.init(TABLE_NAME);
        output = new ByteArrayOutputStream();
        personPreferencesService = new PersonPreferencesService(client, TABLE_NAME);
        handler = new UpsertPersonPreferencesHandler(personPreferencesService);
    }

    @Test
    void shouldCreatePersonPreferencesWhenDoesNotExist() throws IOException, NotFoundException {
        var existingPersonPreferences = profileWithCristinIdentifier(randomUri());
        var updatePersonPreferences = existingPersonPreferences.copy()
                .withPromotedPublications(List.of())
                .build();
        handler.handleRequest(createRequest(updatePersonPreferences), output, CONTEXT);

        assertThat(personPreferencesService.fetchPreferences(existingPersonPreferences).personId(),
                is(equalTo(updatePersonPreferences.personId())));
    }

    @Test
    void shouldUpdatePersonPreferencesWhenExist() throws IOException, NotFoundException {
        var existingPersonPreferences = profileWithCristinIdentifier(randomUri()).upsert();
        var updatePersonPreferences = existingPersonPreferences.copy()
                .withPromotedPublications(List.of())
                .build();
        handler.handleRequest(createRequest(updatePersonPreferences), output, CONTEXT);

        assertThat(personPreferencesService.fetchPreferences(existingPersonPreferences).personId(),
                is(equalTo(updatePersonPreferences.personId())));
    }

    @Test
    void shouldReturnUnauthorizedWhenNonAuthorized() throws IOException {
        var profile = profileWithCristinIdentifier(randomUri());
        handler.handleRequest(createUnauthorizedRequest(profile), output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Problem.class);

        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_UNAUTHORIZED)));
    }

    @Test
    void shouldReturnUnauthorizedWhenProvidedCristinIdDoesNotMatch() throws IOException {
        var profile = profileWithCristinIdentifier(randomUri());
        handler.handleRequest(createRequestWithNotMatchingCristinIds(profile), output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Problem.class);

        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_UNAUTHORIZED)));
    }

    private InputStream createUnauthorizedRequest(PersonPreferences personPreferences) throws JsonProcessingException {
        return new HandlerRequestBuilder<PersonPreferences>(dtoObjectMapper)
                .withUserName(randomString())
                .withBody(personPreferences)
                .build();
    }

    private InputStream createRequestWithNotMatchingCristinIds(PersonPreferences personPreferences)
            throws JsonProcessingException {
        return new HandlerRequestBuilder<PersonPreferences>(dtoObjectMapper)
                .withUserName(randomString())
                .withCurrentCustomer(randomUri())
                .withPersonCristinId(personPreferences.personId())
                .withCurrentCustomer(randomUri())
                .withPathParameters(Map.of("cristinId", randomString()))
                .withBody(personPreferences)
                .build();
    }

    private InputStream createRequest(PersonPreferences personPreferences) throws JsonProcessingException {
        return new HandlerRequestBuilder<PersonPreferences>(dtoObjectMapper)
                .withUserName(randomString())
                .withCurrentCustomer(randomUri())
                .withPersonCristinId(personPreferences.personId())
                .withCurrentCustomer(randomUri())
                .withPathParameters(Map.of("cristinId", personPreferences.personId().toString()))
                .withBody(personPreferences)
                .build();
    }

    private PersonPreferences profileWithCristinIdentifier(URI cristinIdentifier) {
        return new PersonPreferences.Builder(personPreferencesService)
                .withPersonId(cristinIdentifier)
                .withPromotedPublications(List.of(randomUri(), randomUri()))
                .build();
    }
}
