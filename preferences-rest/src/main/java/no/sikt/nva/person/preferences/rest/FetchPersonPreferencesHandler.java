package no.sikt.nva.person.preferences.rest;

import static nva.commons.core.attempt.Try.attempt;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.NotFoundException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.attempt.Failure;

public class FetchPersonPreferencesHandler extends ApiGatewayHandler<Void, PersonPreferences> {

    public static final String PERSON_PREFERENCES_NOT_FOUND_MESSAGE = "Person preferences not found: ";
    private static final String TABLE_NAME = new Environment().readEnv("TABLE_NAME");
    private static final String PERSON_ID = "personId";
    private final PersonPreferencesService personPreferencesService;

    @JacocoGenerated
    public FetchPersonPreferencesHandler() {
        this(new PersonPreferencesService(AmazonDynamoDBClientBuilder.defaultClient(), TABLE_NAME));
    }

    public FetchPersonPreferencesHandler(PersonPreferencesService personPreferencesService) {
        super(Void.class);
        this.personPreferencesService = personPreferencesService;
    }

    @Override
    protected PersonPreferences processInput(Void input, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {

        var personId = getPersonId(requestInfo);

        return attempt(() -> new PersonPreferences.Builder(personPreferencesService).withPersonId(personId)
                                 .build()
                                 .fetch()).orElseThrow(personPreferencesNotFound(personId));
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, PersonPreferences output) {
        return HttpURLConnection.HTTP_OK;
    }

    private static Function<Failure<PersonPreferences>, NotFoundException> personPreferencesNotFound(URI personId) {
        return failure -> new NotFoundException(PERSON_PREFERENCES_NOT_FOUND_MESSAGE + personId);
    }

    private static URI getPersonId(RequestInfo requestInfo) {
        var personId = requestInfo.getPathParameters().get(PERSON_ID);
        return URI.create(URLDecoder.decode(personId, StandardCharsets.UTF_8));
    }
}
