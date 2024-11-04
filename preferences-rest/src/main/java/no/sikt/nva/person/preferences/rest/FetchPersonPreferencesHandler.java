package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDto;
import no.sikt.nva.person.preferences.commons.service.IndexService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.JacocoGenerated;

import java.net.HttpURLConnection;

import static no.sikt.nva.person.Constants.TABLE_NAME;
import static no.sikt.nva.person.Constants.getCristinId;

public class FetchPersonPreferencesHandler extends ApiGatewayHandler<Void, PersonPreferencesDto> {

    private final IndexService<PersonPreferencesDao> dynamoDbService;

    @JacocoGenerated
    public FetchPersonPreferencesHandler() {
        this(new IndexService<>(TABLE_NAME, PersonPreferencesDao.class));
    }

    public FetchPersonPreferencesHandler(IndexService<PersonPreferencesDao> personPreferencesService) {
        super(Void.class);
        this.dynamoDbService = personPreferencesService;
    }

    @Override
    protected void validateRequest(Void unused, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        //        requestInfo.getCurrentCustomer();
    }

    @Override
    protected PersonPreferencesDto processInput(Void input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {

        return PersonPreferencesDao.builder()
                .withId(getCristinId(requestInfo))
                .build()
                .fetch(dynamoDbService)
                .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, PersonPreferencesDto output) {
        return HttpURLConnection.HTTP_OK;
    }

}
