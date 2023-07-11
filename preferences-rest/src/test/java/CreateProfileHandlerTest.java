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
import no.unit.nva.person.preferences.commons.model.PersonPreferences;
import no.unit.nva.person.preferences.commons.service.PreferencesService;
import no.unit.nva.person.preferences.rest.CreateProfileHandler;
import no.unit.nva.person.preferences.test.support.UserProfileLocalTestDatabase;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

public class CreateProfileHandlerTest extends UserProfileLocalTestDatabase {

    public static final String TABLE_NAME = "nonExistentTableName";
    private static final Context CONTEXT = mock(Context.class);
    private ByteArrayOutputStream output;
    private PreferencesService preferencesService;
    private CreateProfileHandler handler;

    @BeforeEach
    public void init() {
        super.init(TABLE_NAME);
        output = new ByteArrayOutputStream();
        preferencesService = new PreferencesService(client, TABLE_NAME);
        handler = new CreateProfileHandler(preferencesService);
    }

    @Test
    void shouldCreateProfileWhenAuthenticatedCristinPerson() throws IOException {
        var profile = profileWithCristinIdentifier(randomUri());
        handler.handleRequest(createRequest(profile), output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, PersonPreferences.class)
                           .getBodyObject(PersonPreferences.class);

        assertThat(preferencesService.getPreferencesByIdentifier(response.identifier()), is(equalTo(profile)));
    }

    @Test
    void shouldReturnUnauthorizedWhenNonAuthorizedCristinPerson() throws IOException {
        var profile = profileWithCristinIdentifier(randomUri());
        handler.handleRequest(createUnauthorizedRequest(profile), output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Problem.class);

        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_UNAUTHORIZED)));
    }

    private InputStream createUnauthorizedRequest(PersonPreferences profile) throws JsonProcessingException {
        return new HandlerRequestBuilder<PersonPreferences>(dtoObjectMapper)
                   .withUserName(randomString())
                   .withBody(profile)
                   .build();
    }

    private InputStream createRequest(PersonPreferences profile) throws JsonProcessingException {
        return new HandlerRequestBuilder<PersonPreferences>(dtoObjectMapper)
                   .withUserName(randomString())
                   .withCurrentCustomer(randomUri())
                   .withPersonCristinId(profile.identifier())
                   .withCurrentCustomer(randomUri())
                   .withBody(profile)
                   .build();
    }

    private PersonPreferences profileWithCristinIdentifier(URI cristinIdentifier) {
        return new PersonPreferences.Builder()
                   .withIdentifier(cristinIdentifier)
                   .withPromotedPublication(List.of(randomString(), randomString()))
                   .build();
    }
}
