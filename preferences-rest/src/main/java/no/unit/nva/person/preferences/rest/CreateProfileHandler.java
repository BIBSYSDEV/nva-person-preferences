package no.unit.nva.person.preferences.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import no.unit.nva.person.preferences.commons.model.Profile;
import no.unit.nva.person.preferences.commons.service.ProfileService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.UnauthorizedException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;

public class CreateProfileHandler extends ApiGatewayHandler<Profile, Profile> {

    private static final String TABLE_NAME = new Environment().readEnv("TABLE_NAME");
    private final ProfileService profileService;

    @JacocoGenerated
    public CreateProfileHandler() {
        this(new ProfileService(AmazonDynamoDBClientBuilder.defaultClient(), TABLE_NAME));
    }

    public CreateProfileHandler(ProfileService profileService) {
        super(Profile.class);
        this.profileService = profileService;
    }

    @Override
    protected Profile processInput(Profile input, RequestInfo requestInfo, Context context)
        throws UnauthorizedException {
        validateRequest(input, requestInfo);
        return input.create(profileService);
    }

    @Override
    protected Integer getSuccessStatusCode(Profile input, Profile output) {
        return HttpURLConnection.HTTP_CREATED;
    }

    private static void validateRequest(Profile input, RequestInfo requestInfo) throws UnauthorizedException {
        if (isNotAuthenticated(input, requestInfo)) {
            throw new UnauthorizedException();
        }
    }

    private static boolean isNotAuthenticated(Profile input, RequestInfo requestInfo) throws UnauthorizedException {
        return !requestInfo.getPersonCristinId().equals(input.identifier());
    }
}