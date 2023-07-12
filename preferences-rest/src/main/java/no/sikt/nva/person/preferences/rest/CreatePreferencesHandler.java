package no.sikt.nva.person.preferences.rest;

import static java.util.Objects.isNull;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.service.PreferencesService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.UnauthorizedException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;

public class CreatePreferencesHandler extends ApiGatewayHandler<PreferencesRequest, PersonPreferences> {

    private static final String TABLE_NAME = new Environment().readEnv("TABLE_NAME");
    private final PreferencesService preferencesService;

    @JacocoGenerated
    public CreatePreferencesHandler() {
        this(new PreferencesService(AmazonDynamoDBClientBuilder.defaultClient(), TABLE_NAME));
    }

    public CreatePreferencesHandler(PreferencesService preferencesService) {
        super(PreferencesRequest.class);
        this.preferencesService = preferencesService;
    }

    @Override
    protected PersonPreferences processInput(PreferencesRequest input, RequestInfo requestInfo, Context context)
        throws UnauthorizedException {
        validateRequest(requestInfo);
        return new PersonPreferences.Builder(preferencesService)
                   .withId(requestInfo.getPersonCristinId())
                   .withPromotedPublication(input.promotedPublications())
                   .build()
                   .create();
    }

    @Override
    protected Integer getSuccessStatusCode(PreferencesRequest input, PersonPreferences output) {
        return HttpURLConnection.HTTP_CREATED;
    }

    private static void validateRequest(RequestInfo requestInfo) throws UnauthorizedException {
        if (isNotAuthenticated(requestInfo)) {
            throw new UnauthorizedException();
        }
    }

    private static boolean isNotAuthenticated(RequestInfo requestInfo) throws UnauthorizedException {
        return isNull(requestInfo.getCurrentCustomer()) && isNull(requestInfo.getPersonCristinId());
    }
}