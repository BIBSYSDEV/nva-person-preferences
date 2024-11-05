package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDto;
import no.sikt.nva.person.preferences.commons.service.DynamoCrudService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.JacocoGenerated;

import java.net.HttpURLConnection;

import static no.sikt.nva.person.Constants.TABLE_NAME;
import static no.sikt.nva.person.Constants.getCristinId;

public class FetchPersonPreferencesHandler extends ApiGatewayHandler<Void, PersonPreferencesDto> {

    private final DynamoCrudService<PersonPreferencesDao> crudPreferenceService;

    @JacocoGenerated
    public FetchPersonPreferencesHandler() {
        this(new DynamoCrudService<>(TABLE_NAME, PersonPreferencesDao.class));
    }

    public FetchPersonPreferencesHandler(DynamoCrudService<PersonPreferencesDao> crudPreferenceService) {
        super(Void.class);
        this.crudPreferenceService = crudPreferenceService;
    }

    @Override
    protected void validateRequest(Void unused, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        //        requestInfo.getCurrentCustomer();
    }

    @Override
    protected PersonPreferencesDto processInput(Void input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {

        return PersonPreferencesDao.builder()
                .personId(getCristinId(requestInfo))
                .build()
                .fetch(crudPreferenceService)
                .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, PersonPreferencesDto output) {
        return HttpURLConnection.HTTP_OK;
    }

}
