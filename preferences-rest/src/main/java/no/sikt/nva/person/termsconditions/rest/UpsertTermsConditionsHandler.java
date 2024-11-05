package no.sikt.nva.person.termsconditions.rest;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.person.preferences.commons.model.TermsConditionsDto;
import no.sikt.nva.person.preferences.commons.model.TermsConditionsDao;
import no.sikt.nva.person.preferences.commons.service.DynamoCrudService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.UnauthorizedException;
import nva.commons.core.JacocoGenerated;

import java.net.HttpURLConnection;

import static no.sikt.nva.person.Constants.TABLE_NAME;
import static no.sikt.nva.person.Constants.dontMatchCustomerAndPerson;

public class UpsertTermsConditionsHandler extends ApiGatewayHandler<TermsConditionsDto, TermsConditionsDto> {

    private final DynamoCrudService<TermsConditionsDao> crudTermsConditionsService;

    @JacocoGenerated
    public UpsertTermsConditionsHandler() {
        this(new DynamoCrudService<>( TABLE_NAME, TermsConditionsDao.class));
    }

    public UpsertTermsConditionsHandler(DynamoCrudService<TermsConditionsDao> termsConditionsService) {
        super(TermsConditionsDto.class);
        this.crudTermsConditionsService = termsConditionsService;
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
                .upsert(crudTermsConditionsService)
                .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(TermsConditionsDto termsConditionsDto, TermsConditionsDto o) {
        return HttpURLConnection.HTTP_OK;
    }
}
