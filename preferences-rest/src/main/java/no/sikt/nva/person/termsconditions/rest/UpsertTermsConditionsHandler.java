package no.sikt.nva.person.termsconditions.rest;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.person.preferences.commons.model.TermsConditionsDto;
import no.sikt.nva.person.preferences.commons.model.TermsConditionsDao;
import no.sikt.nva.person.preferences.commons.service.IndexService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.UnauthorizedException;
import nva.commons.core.JacocoGenerated;

import java.net.HttpURLConnection;

import static no.sikt.nva.person.Constants.TABLE_NAME;
import static no.sikt.nva.person.Constants.dontMatchCustomerAndPerson;

public class UpsertTermsConditionsHandler extends ApiGatewayHandler<TermsConditionsDto, TermsConditionsDto> {

    private final IndexService<TermsConditionsDao> dynamoDbService;

    @JacocoGenerated
    public UpsertTermsConditionsHandler() {
        this(new IndexService<>( TABLE_NAME, TermsConditionsDao.class));
    }

    public UpsertTermsConditionsHandler(IndexService<TermsConditionsDao> personPreferencesService) {
        super(TermsConditionsDto.class);
        this.dynamoDbService = personPreferencesService;
    }


    @Override
    protected void validateRequest(TermsConditionsDto termsConditionsDto, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        if (dontMatchCustomerAndPerson(requestInfo)) {
            throw new UnauthorizedException();
        }
    }


    @Override
    protected TermsConditionsDto processInput(TermsConditionsDto input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        return TermsConditionsDao.builder()
                .personId(requestInfo.getPersonCristinId())
                .termsConditionsUri(input.termsConditionsUrl())
                .build()
                .upsert(dynamoDbService)
                .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(TermsConditionsDto termsConditionsDto, TermsConditionsDto o) {
        return HttpURLConnection.HTTP_OK;
    }
}
