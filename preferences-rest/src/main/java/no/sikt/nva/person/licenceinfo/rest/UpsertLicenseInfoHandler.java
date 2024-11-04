package no.sikt.nva.person.licenceinfo.rest;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.person.preferences.commons.model.LicenseInfoDto;
import no.sikt.nva.person.preferences.commons.model.LicenseInfoDao;
import no.sikt.nva.person.preferences.commons.service.IndexService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.UnauthorizedException;
import nva.commons.core.JacocoGenerated;

import java.net.HttpURLConnection;

import static no.sikt.nva.person.Constants.TABLE_NAME;
import static no.sikt.nva.person.Constants.dontMatchCustomerAndPerson;

public class UpsertLicenseInfoHandler extends ApiGatewayHandler<LicenseInfoDto, LicenseInfoDto> {

    private final IndexService<LicenseInfoDao> dynamoDbService;

    @JacocoGenerated
    public UpsertLicenseInfoHandler() {
        this(new IndexService<>( TABLE_NAME, LicenseInfoDao.class));
    }

    public UpsertLicenseInfoHandler(IndexService<LicenseInfoDao> personPreferencesService) {
        super(LicenseInfoDto.class);
        this.dynamoDbService = personPreferencesService;
    }


    @Override
    protected void validateRequest(LicenseInfoDto licenseInfoDto, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        if (dontMatchCustomerAndPerson(requestInfo)) {
            throw new UnauthorizedException();
        }
    }


    @Override
    protected LicenseInfoDto processInput(LicenseInfoDto input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        return LicenseInfoDao.builder()
                .withId(requestInfo.getPersonCristinId())
                .licenseUri(input.licenseUri())
                .build()
                .upsert(dynamoDbService)
                .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(LicenseInfoDto licenseInfoDto, LicenseInfoDto o) {
        return HttpURLConnection.HTTP_OK;
    }
}
