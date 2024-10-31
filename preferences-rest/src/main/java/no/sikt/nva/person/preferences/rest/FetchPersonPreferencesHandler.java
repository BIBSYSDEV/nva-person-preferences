package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;

import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.JacocoGenerated;

import static no.sikt.nva.person.Constants.TABLE_NAME;
import static no.sikt.nva.person.Constants.getCristinId;

public class FetchPersonPreferencesHandler extends ApiGatewayHandler<Void, PersonPreferences> {

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

}
