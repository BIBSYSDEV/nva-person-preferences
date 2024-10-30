package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;

public class FetchPersonPreferencesHandler extends ApiGatewayHandler<Void, PersonPreferences> {

    private static final String TABLE_NAME = new Environment().readEnv("TABLE_NAME");
    private static final String CRISTIN_ID = "cristinId";
    private final PersonService dynamoDbService;

    @JacocoGenerated
    public FetchPersonPreferencesHandler() {
        this(new PersonService(AmazonDynamoDBClientBuilder.defaultClient(), TABLE_NAME));
    }

    public FetchPersonPreferencesHandler(PersonService personPreferencesService) {
        super(Void.class);
        this.dynamoDbService = personPreferencesService;
    }

    @Override
    protected void validateRequest(Void unused, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        //Do nothing
    }

    @Override
    protected PersonPreferences processInput(Void input, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {

        return new PersonPreferencesDao.Builder()
            .withPersonId(getCristinId(requestInfo))
            .build()
            .fetch(dynamoDbService)
            .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, PersonPreferences output) {
        return HttpURLConnection.HTTP_OK;
    }

    private static URI getCristinId(RequestInfo requestInfo) {
        return URI.create(URLDecoder.decode(requestInfo.getPathParameters().get(CRISTIN_ID), StandardCharsets.UTF_8));
    }
}
