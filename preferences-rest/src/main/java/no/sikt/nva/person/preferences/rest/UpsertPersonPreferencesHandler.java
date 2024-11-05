package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDto;
import no.sikt.nva.person.preferences.commons.service.DynamoCrudService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.NotFoundException;
import nva.commons.apigateway.exceptions.UnauthorizedException;
import nva.commons.core.JacocoGenerated;

import java.net.HttpURLConnection;

import static no.sikt.nva.person.Constants.TABLE_NAME;
import static no.sikt.nva.person.Constants.dontMatchCustomerAndPerson;

public class UpsertPersonPreferencesHandler extends ApiGatewayHandler<PreferencesRequest, PersonPreferencesDto> {

    private final DynamoCrudService<PersonPreferencesDao> crudPreferenceService;

    @JacocoGenerated
    public UpsertPersonPreferencesHandler() {
        this(new DynamoCrudService<>( TABLE_NAME, PersonPreferencesDao.class));
    }

    public UpsertPersonPreferencesHandler(DynamoCrudService<PersonPreferencesDao> preferenceService) {
        super(PreferencesRequest.class);
        this.crudPreferenceService = preferenceService;
    }

    @Override
    protected void validateRequest(PreferencesRequest preferencesRequest, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        if (dontMatchCustomerAndPerson(requestInfo)) {
            throw new UnauthorizedException();
        }
    }

    @Override
    protected PersonPreferencesDto processInput(PreferencesRequest input, RequestInfo requestInfo, Context context)
            throws UnauthorizedException, NotFoundException {

        return PersonPreferencesDao.builder()
                .personId(requestInfo.getPersonCristinId())
                .promotedPublications(input.promotedPublications())
                .build()
                .upsert(crudPreferenceService)
                .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(PreferencesRequest input, PersonPreferencesDto output) {
        return HttpURLConnection.HTTP_OK;
    }

}
