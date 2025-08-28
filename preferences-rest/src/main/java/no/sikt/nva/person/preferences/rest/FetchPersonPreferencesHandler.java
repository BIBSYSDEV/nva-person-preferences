package no.sikt.nva.person.preferences.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences;
import no.sikt.nva.person.preferences.commons.model.PersonPreferences.Builder;
import no.sikt.nva.person.preferences.commons.service.PersonPreferencesService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.NotFoundException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;

public class FetchPersonPreferencesHandler extends ApiGatewayHandler<Void, PersonPreferences> {

    private static final String TABLE_NAME = new Environment().readEnv("TABLE_NAME");
    private static final String CRISTIN_ID = "cristinId";
    private final PersonPreferencesService personPreferencesService;

    @JacocoGenerated
    public FetchPersonPreferencesHandler() {
        this(new PersonPreferencesService(AmazonDynamoDBClientBuilder.defaultClient(), TABLE_NAME), new Environment());
    }

    public FetchPersonPreferencesHandler(PersonPreferencesService personPreferencesService, Environment environment) {
        super(Void.class, environment);
        this.personPreferencesService = personPreferencesService;
    }

    private static URI getCristinId(RequestInfo requestInfo) {
        return URI.create(URLDecoder.decode(requestInfo.getPathParameters().get(CRISTIN_ID), StandardCharsets.UTF_8));
    }

    @Override
    protected void validateRequest(Void unused, RequestInfo requestInfo, Context context) {
        //Do nothing
    }

    @Override
    protected PersonPreferences processInput(Void input, RequestInfo requestInfo, Context context) {
        var cristinId = getCristinId(requestInfo);
        PersonPreferences personPreferences;
        try {
            personPreferences = fetchPersonPreferences(cristinId);
        } catch (NotFoundException e) {
            personPreferences = new Builder()
                       .withPersonId(cristinId)
                       .withPromotedPublications(List.of())
                       .build();
        }
        return personPreferences;
    }

    private PersonPreferences fetchPersonPreferences(URI cristinId) throws NotFoundException {
        return new PersonPreferences.Builder(personPreferencesService)
                   .withPersonId(cristinId)
                   .build()
                   .fetch();
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, PersonPreferences output) {
        return HttpURLConnection.HTTP_OK;
    }
}
