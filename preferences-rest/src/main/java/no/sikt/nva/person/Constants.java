package no.sikt.nva.person;

import nva.commons.apigateway.RequestInfo;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Constants {
    public static final String CRISTIN_ID = "cristinId";
    public static final String TABLE_NAME = new Environment().readEnv("TABLE_NAME");

    public static URI getCristinId(RequestInfo requestInfo) {
        return URI.create(URLDecoder.decode(requestInfo.getPathParameters().get(CRISTIN_ID), StandardCharsets.UTF_8));
    }
    @JacocoGenerated
    private Constants() {
    }
}
