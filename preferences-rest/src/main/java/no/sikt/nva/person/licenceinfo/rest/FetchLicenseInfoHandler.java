package no.sikt.nva.person.licenceinfo.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.person.preferences.commons.model.LicenseInfo;
import no.sikt.nva.person.preferences.commons.model.LicenseInfoDao;
import no.sikt.nva.person.preferences.commons.service.PersonService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;

import java.net.HttpURLConnection;

public class FetchLicenseInfoHandler extends ApiGatewayHandler<Void, LicenseInfo> {

    private static final String TABLE_NAME = new Environment().readEnv("TABLE_NAME");
    private final PersonService dynamoDbService;

    @JacocoGenerated
    public FetchLicenseInfoHandler() {
        this(new PersonService(AmazonDynamoDBClientBuilder.defaultClient(), TABLE_NAME));
    }

    public FetchLicenseInfoHandler(PersonService personService) {
        super(Void.class);
        this.dynamoDbService = personService;
    }

    @Override
    protected void validateRequest(Void unused, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        requestInfo.getPersonCristinId();
    }

    @Override
    protected LicenseInfo processInput(Void input, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {

        return new LicenseInfoDao.Builder()
            .withPersonId(requestInfo.getPersonCristinId())
            .build()
            .fetch(dynamoDbService)
            .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, LicenseInfo output) {
        return HttpURLConnection.HTTP_OK;
    }

}
