package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDto;
import no.sikt.nva.person.preferences.commons.service.DynamoCrudService;
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

public class UpsertPersonPreferencesDtoHandlerTest extends LocalPreferencesTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    private static final Context CONTEXT = mock(Context.class);
    private ByteArrayOutputStream output;
    private DynamoCrudService<PersonPreferencesDao> personPreferencesService;
    private UpsertPersonPreferencesHandler handler;

    @BeforeEach
    public void init() {
        super.init(TABLE_NAME);
        output = new ByteArrayOutputStream();
        personPreferencesService = new DynamoCrudService<>(client,  TABLE_NAME, PersonPreferencesDao.class);
        handler = new UpsertPersonPreferencesHandler(personPreferencesService);
    }

    @Test
    void shouldCreatePersonPreferencesWhenDoesNotExist() throws IOException {
        var existingPersonPreferences = profileWithCristinIdentifier(randomUri());

        handler.handleRequest(createRequest(existingPersonPreferences), output, CONTEXT);

        var response = GatewayResponse.fromOutputStream(output, PersonPreferencesDto.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_OK)));
        assertThat(response.getBodyObject(PersonPreferencesDto.class).personId(),
                is(equalTo(existingPersonPreferences.personId())));
    }

    private InputStream createRequest(PersonPreferencesDto personPreferencesDto) throws JsonProcessingException {
        return new HandlerRequestBuilder<PersonPreferencesDto>(dtoObjectMapper)
                .withUserName(randomString())
                .withPersonCristinId(personPreferencesDto.personId())
                .withCurrentCustomer(personPreferencesDto.personId())
                .withPathParameters(Map.of("cristinId", personPreferencesDto.personId().toString()))
                .withBody(personPreferencesDto)
                .build();
    }

    private PersonPreferencesDto profileWithCristinIdentifier(URI cristinIdentifier) {
        return new PersonPreferencesDto.Builder()
                .withPersonId(cristinIdentifier)
                .withPromotedPublications(List.of(randomUri(), randomUri()))
                .build();
    }

    @Test
    void shouldUpdatePersonPreferencesWhenExist() throws IOException, NotFoundException {
        var existingPersonPreferences = profileWithCristinIdentifier(randomUri())
                .toDao().upsert(personPreferencesService);

        var updatePersonPreferences = new PersonPreferencesDto.Builder()
                .withPromotedPublications(List.of())
                .withPersonId(existingPersonPreferences.personId())
                .build();

        handler.handleRequest(createRequest(updatePersonPreferences), output, CONTEXT);

        var response = GatewayResponse.fromOutputStream(output, PersonPreferencesDto.class);
        var person = response.getBodyObject(PersonPreferencesDto.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_OK)));

        assertThat(person.personId(), is(equalTo(updatePersonPreferences.personId())));
    }

    @Test
    void shouldReturnUnauthorizedWhenNonAuthorized() throws IOException {
        var profile = profileWithCristinIdentifier(randomUri());
        handler.handleRequest(createUnauthorizedRequest(profile), output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Problem.class);

        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_UNAUTHORIZED)));
    }

    private InputStream createUnauthorizedRequest(PersonPreferencesDto personPreferencesDto) throws JsonProcessingException {
        return new HandlerRequestBuilder<PersonPreferencesDto>(dtoObjectMapper)
                .withUserName(randomString())
                .withBody(personPreferencesDto)
                .build();
    }

    @Test
    void shouldReturnUnauthorizedWhenProvidedCristinIdDoesNotMatch() throws IOException {
        var profile = profileWithCristinIdentifier(randomUri());
        handler.handleRequest(createRequestWithNotMatchingCristinIds(profile), output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Problem.class);

        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_UNAUTHORIZED)));
    }

    private InputStream createRequestWithNotMatchingCristinIds(PersonPreferencesDto personPreferencesDto)
            throws JsonProcessingException {
        return new HandlerRequestBuilder<PersonPreferencesDto>(dtoObjectMapper)
                .withUserName(randomString())
                .withPersonCristinId(personPreferencesDto.personId())
                .withCurrentCustomer(randomUri())
                .withPathParameters(Map.of("cristinId", randomString()))
                .withBody(personPreferencesDto)
                .build();
    }
}
