package no.sikt.nva.person.preferences.rest;

import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;
import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.service.PreferencesService;
import no.sikt.nva.person.preferences.test.support.LocalPreferencesTestDatabase;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

public class UpdatePreferencesHandlerTest extends LocalPreferencesTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    private static final Context CONTEXT = mock(Context.class);
    private ByteArrayOutputStream output;
    private PreferencesService preferencesService;
    private UpdatePreferencesHandler handler;

    @BeforeEach
    public void init() {
        super.init(TABLE_NAME);
        output = new ByteArrayOutputStream();
        preferencesService = new PreferencesService(client, TABLE_NAME);
        handler = new UpdatePreferencesHandler(preferencesService);
    }

    @Test
    void shouldUpdatePersonPreferences() throws IOException {
        var existingPersonPreferences = persistPersonPreferences();
        var updatePersonPreferences = existingPersonPreferences.copy()
                                          .withPromotedPublication(null)
                                          .build();
        handler.handleRequest(createRequest(updatePersonPreferences), output, CONTEXT);

        assertThat(preferencesService.getPreferencesByIdentifier(existingPersonPreferences.id()),
                   is(equalTo(updatePersonPreferences)));
    }

    @Test
    void shouldReturnUnauthorizedWhenNonAuthorized() throws IOException {
        var profile = profileWithCristinIdentifier(randomUri());
        handler.handleRequest(createUnauthorizedRequest(profile), output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Problem.class);

        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_UNAUTHORIZED)));
    }

    private PersonPreferences persistPersonPreferences() {
        return profileWithCristinIdentifier(randomUri())
                   .create(preferencesService);
    }

    private InputStream createUnauthorizedRequest(PersonPreferences personPreferences) throws JsonProcessingException {
        return new HandlerRequestBuilder<PersonPreferences>(dtoObjectMapper)
                   .withUserName(randomString())
                   .withBody(personPreferences)
                   .build();
    }

    private InputStream createRequest(PersonPreferences personPreferences) throws JsonProcessingException {
        return new HandlerRequestBuilder<PersonPreferences>(dtoObjectMapper)
                   .withUserName(randomString())
                   .withCurrentCustomer(randomUri())
                   .withPersonCristinId(personPreferences.id())
                   .withCurrentCustomer(randomUri())
                   .withBody(personPreferences)
                   .build();
    }

    private PersonPreferences profileWithCristinIdentifier(URI cristinIdentifier) {
        return new PersonPreferences.Builder()
                   .withId(cristinIdentifier)
                   .withPromotedPublication(List.of(randomString(), randomString()))
                   .build();
    }
}
