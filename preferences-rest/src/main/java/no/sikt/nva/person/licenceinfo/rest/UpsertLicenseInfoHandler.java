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
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.isNull;

public class UpsertLicenseInfoHandler extends ApiGatewayHandler<LicenseInfo, LicenseInfo> {

    private static final String CRISTIN_ID = "cristinId";
    private static final String TABLE_NAME = new Environment().readEnv("TABLE_NAME");
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
        if (isNotAuthenticated(requestInfo)) {
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

    private static boolean isNotAuthenticated(RequestInfo requestInfo) throws UnauthorizedException {
        return isNull(requestInfo.getCurrentCustomer()) && isNull(requestInfo.getPersonCristinId())
            || !getCristinId(requestInfo).equals(requestInfo.getPersonCristinId());
    }

    private static URI getCristinId(RequestInfo requestInfo) {
        return URI.create(URLDecoder.decode(requestInfo.getPathParameters().get(CRISTIN_ID), StandardCharsets.UTF_8));
    }

}
