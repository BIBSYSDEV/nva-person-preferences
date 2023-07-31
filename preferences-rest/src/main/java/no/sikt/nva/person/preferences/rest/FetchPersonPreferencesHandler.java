package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import java.net.URI;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;

public class FetchPersonPreferencesHandler extends ApiGatewayHandler<Void, PersonPreferencesDao> {

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
    protected PersonPreferencesDao processInput(Void input, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {

        var personId = getPersonId(requestInfo);
        return personPreferencesService.fetchPreferencesByPersonId(personId);
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, PersonPreferencesDao output) {
        return HttpURLConnection.HTTP_OK;
    }

    private static URI getPersonId(RequestInfo requestInfo) {
        var personId = requestInfo.getPathParameters().get(PERSON_ID);
        return URI.create(personId);
    }
}
