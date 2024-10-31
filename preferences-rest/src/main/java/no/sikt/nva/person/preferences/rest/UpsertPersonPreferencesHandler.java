package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao.Builder;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.NotFoundException;
import nva.commons.apigateway.exceptions.UnauthorizedException;
import nva.commons.core.JacocoGenerated;

import java.net.HttpURLConnection;

import static no.sikt.nva.person.Constants.TABLE_NAME;
import static no.sikt.nva.person.Constants.dontMatchCustomerAndPerson;

public class UpsertPersonPreferencesHandler extends ApiGatewayHandler<PreferencesRequest, PersonPreferences> {

    private final PersonService personPreferencesService;

    @JacocoGenerated
    public UpsertPersonPreferencesHandler() {
        this(new PersonService(AmazonDynamoDBClientBuilder.defaultClient(), TABLE_NAME));
    }

    public UpsertPersonPreferencesHandler(PersonService personPreferencesService) {
        super(PreferencesRequest.class);
        this.personPreferencesService = personPreferencesService;
    }

    @Override
    protected void validateRequest(PreferencesRequest preferencesRequest, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        if (dontMatchCustomerAndPerson(requestInfo)) {
            throw new UnauthorizedException();
        }
    }

    @Override
    protected PersonPreferences processInput(PreferencesRequest input, RequestInfo requestInfo, Context context)
            throws UnauthorizedException, NotFoundException {

        return new Builder()
                .withPersonId(requestInfo.getPersonCristinId())
                .withPromotedPublications(input.promotedPublications())
                .build()
                .upsert(personPreferencesService)
                .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(PreferencesRequest input, PersonPreferences output) {
        return HttpURLConnection.HTTP_OK;
    }

}
