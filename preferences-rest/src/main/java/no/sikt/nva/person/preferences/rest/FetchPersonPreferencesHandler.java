package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import java.net.URI;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchPersonPreferencesHandler extends ApiGatewayHandler<Void, PersonPreferences> {

    private static final String TABLE_NAME = new Environment().readEnv("TABLE_NAME");
    private static final Logger logger = LoggerFactory.getLogger(CreatePersonPreferencesHandler.class);
    private static final String PERSON_ID = "personId";
    private final PersonPreferencesService personPreferencesService;
    private int statusCode = HttpURLConnection.HTTP_OK;

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

        statusCode = HttpURLConnection.HTTP_OK;

        var personId = getPersonId(requestInfo);
        logger.info("requestInfo.personId: " + personId);
        return personPreferencesService.getPreferencesByPersonId(personId);
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, PersonPreferences output) {
        return statusCode;
    }

    private static URI getPersonId(RequestInfo requestInfo) {
        var personId = requestInfo.getPathParameters().get(PERSON_ID);
        return URI.create(personId);
    }
}
