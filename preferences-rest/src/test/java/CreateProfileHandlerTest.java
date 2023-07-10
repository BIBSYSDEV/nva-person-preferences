import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;
import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import no.unit.nva.person.preferences.commons.model.Profile;
import no.unit.nva.person.preferences.commons.service.ProfileService;
import no.unit.nva.person.preferences.commons.utils.UserProfileLocalTestDatabase;
import no.unit.nva.person.preferences.rest.CreateProfileHandler;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;

public class CreateProfileHandlerTest extends UserProfileLocalTestDatabase {

    private static final Context CONTEXT = mock(Context.class);
    public static final String TABLE_NAME = "nonExistentTableName";
    private ByteArrayOutputStream output;
    private ProfileService profileService;
    private CreateProfileHandler handler;

    @BeforeEach
    public void init() {
        super.init(TABLE_NAME);
        output = new ByteArrayOutputStream();
        profileService = new ProfileService(client, TABLE_NAME);
        handler = new CreateProfileHandler(profileService);
    }

    @Test
    void shouldCreateProfileWhenAuthenticatedCristinPerson() throws IOException {
        var profile = profileWithCristinIdentifier(randomUri());
        handler.handleRequest(createRequest(profile), output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Profile.class).getBodyObject(Profile.class);

        assertThat(profileService.getProfileByIdentifier(response.identifier()).identifier(),
                   is(equalTo(profile.identifier())));

        assertThat(profileService.getProfileByIdentifier(response.identifier()).promotedPublications(),
                   is(equalTo(profile.promotedPublications())));
    }

    @Test
    void shouldReturnUnauthorizedWhenNonAuthorizedCristinPerson() throws IOException {
        var profile = profileWithCristinIdentifier(randomUri());
        handler.handleRequest(createUnauthorizedRequest(profile), output, CONTEXT);
        var response = GatewayResponse.fromOutputStream(output, Problem.class);

        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_UNAUTHORIZED)));
    }

    private InputStream createUnauthorizedRequest(Profile profile) throws JsonProcessingException {
        return new HandlerRequestBuilder<Profile>(dtoObjectMapper)
                   .withUserName(randomString())
                   .withBody(profile)
                   .build();
    }

    private InputStream createRequest(Profile profile) throws JsonProcessingException {
        return new HandlerRequestBuilder<Profile>(dtoObjectMapper)
                   .withUserName(randomString())
                   .withCurrentCustomer(randomUri())
                   .withPersonCristinId(profile.identifier())
                   .withBody(profile)
                   .build();
    }

    private Profile profileWithCristinIdentifier(URI cristinIdentifier) {
        return new Profile.Builder()
                   .withIdentifier(cristinIdentifier)
                   .withPromotedPublication(List.of(randomString(), randomString()))
                   .build();
    }
}
