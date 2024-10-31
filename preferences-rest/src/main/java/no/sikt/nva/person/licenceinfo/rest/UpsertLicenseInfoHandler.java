package no.sikt.nva.person.licenceinfo.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.person.preferences.commons.model.LicenseInfo;
import no.sikt.nva.person.preferences.commons.model.LicenseInfoDao;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.UnauthorizedException;
import nva.commons.core.JacocoGenerated;

import java.net.HttpURLConnection;

import static no.sikt.nva.person.Constants.TABLE_NAME;
import static no.sikt.nva.person.Constants.dontMatchCustomerAndPerson;

public class UpsertLicenseInfoHandler extends ApiGatewayHandler<LicenseInfo, LicenseInfo> {

    private final PersonService dynamoDbService;

    @JacocoGenerated
    public UpsertLicenseInfoHandler() {
        this(new PersonService(AmazonDynamoDBClientBuilder.defaultClient(), TABLE_NAME));
    }

    public UpsertLicenseInfoHandler(PersonService personPreferencesService) {
        super(LicenseInfo.class);
        this.dynamoDbService = personPreferencesService;
    }


    @Override
    protected void validateRequest(LicenseInfo licenseInfo, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        if (dontMatchCustomerAndPerson(requestInfo)) {
            throw new UnauthorizedException();
        }
    }


    @Override
    protected LicenseInfo processInput(LicenseInfo input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {
        return new LicenseInfoDao.Builder()
                .withPersonId(requestInfo.getPersonCristinId())
                .withLicenseUri(input.licenseUri())
                .build()
                .upsert(dynamoDbService)
                .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(LicenseInfo licenseInfo, LicenseInfo o) {
        return HttpURLConnection.HTTP_OK;
    }
}
