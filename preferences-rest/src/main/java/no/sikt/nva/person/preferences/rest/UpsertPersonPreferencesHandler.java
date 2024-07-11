package no.sikt.nva.person.preferences.rest;

import static java.util.Objects.isNull;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences.Builder;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.NotFoundException;
import nva.commons.apigateway.exceptions.UnauthorizedException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;

public class UpsertPersonPreferencesHandler extends ApiGatewayHandler<PreferencesRequest, PersonPreferences> {

    private static final String CRISTIN_ID = "cristinId";
    private static final String TABLE_NAME = new Environment().readEnv("TABLE_NAME");
    private final PersonPreferencesService personPreferencesService;

    @JacocoGenerated
    public UpsertPersonPreferencesHandler() {
        this(new PersonPreferencesService(AmazonDynamoDBClientBuilder.defaultClient(), TABLE_NAME));
    }

    public UpsertPersonPreferencesHandler(PersonPreferencesService personPreferencesService) {
        super(PreferencesRequest.class);
        this.personPreferencesService = personPreferencesService;
    }

    @Override
    protected void validateRequest(PreferencesRequest preferencesRequest, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {
        validateRequest(requestInfo);
    }

    @Override
    protected PersonPreferences processInput(PreferencesRequest input, RequestInfo requestInfo, Context context)
        throws UnauthorizedException, NotFoundException {

        return new Builder(personPreferencesService)
                   .withPersonId(requestInfo.getPersonCristinId())
                   .withPromotedPublications(input.promotedPublications())
                   .build()
                   .upsert();
    }

    @Override
    protected Integer getSuccessStatusCode(PreferencesRequest input, PersonPreferences output) {
        return HttpURLConnection.HTTP_OK;
    }

    private static void validateRequest(RequestInfo requestInfo) throws UnauthorizedException {
        if (isNotAuthenticated(requestInfo)) {
            throw new UnauthorizedException();
        }
    }

    private static boolean isNotAuthenticated(RequestInfo requestInfo) throws UnauthorizedException {
        return isNull(requestInfo.getCurrentCustomer()) && isNull(requestInfo.getPersonCristinId())
            || !getCristinId(requestInfo).equals(requestInfo.getPersonCristinId());
    }

    private static URI getCristinId(RequestInfo requestInfo) {
        return URI.create(URLDecoder.decode(requestInfo.getPathParameters().get(CRISTIN_ID), StandardCharsets.UTF_8));
    }
}
