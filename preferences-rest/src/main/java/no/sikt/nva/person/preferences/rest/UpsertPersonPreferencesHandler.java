package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDao;
import no.sikt.nva.person.preferences.commons.model.PersonPreferencesDto;
import no.sikt.nva.person.preferences.commons.service.IndexService;
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

    private final IndexService<PersonPreferencesDao> personPreferencesService;

    @JacocoGenerated
    public UpsertPersonPreferencesHandler() {
        this(new IndexService<>( TABLE_NAME, PersonPreferencesDao.class));
    }

    public UpsertPersonPreferencesHandler(IndexService<PersonPreferencesDao> personPreferencesService) {
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
    protected PersonPreferencesDto processInput(PreferencesRequest input, RequestInfo requestInfo, Context context)
            throws UnauthorizedException, NotFoundException {

        return PersonPreferencesDao.builder()
                .personId(requestInfo.getPersonCristinId())
                .promotedPublications(input.promotedPublications())
                .build()
                .upsert(personPreferencesService)
                .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(PreferencesRequest input, PersonPreferencesDto output) {
        return HttpURLConnection.HTTP_OK;
    }

}
