package no.sikt.nva.person.preferences.rest;

import static java.util.Objects.isNull;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.UnauthorizedException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;

public class UpdatePersonPreferencesHandler extends ApiGatewayHandler<PreferencesRequest, Void> {

    private static final String TABLE_NAME = new Environment().readEnv("TABLE_NAME");
    private final PersonPreferencesService personPreferencesService;

    @JacocoGenerated
    public UpdatePersonPreferencesHandler() {
        this(new PersonPreferencesService(AmazonDynamoDBClientBuilder.defaultClient(), TABLE_NAME));
    }

    public UpdatePersonPreferencesHandler(PersonPreferencesService personPreferencesService) {
        super(PreferencesRequest.class);
        this.personPreferencesService = personPreferencesService;
    }

    @Override
    protected Void processInput(PreferencesRequest input, RequestInfo requestInfo, Context context)
        throws UnauthorizedException {

        validateRequest(requestInfo);

        new PersonPreferences.Builder(personPreferencesService)
            .withPersonId(requestInfo.getPersonCristinId())
            .withPromotedPublications(input.promotedPublications())
            .build()
            .update();

        return null;
    }

    @Override
    protected Integer getSuccessStatusCode(PreferencesRequest input, Void output) {
        return HttpURLConnection.HTTP_OK;
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
