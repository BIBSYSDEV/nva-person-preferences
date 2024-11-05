package no.sikt.nva.person.preferences.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.commons.json.JsonUtils;

public final class PersonPreferencesRestHandlersTestConfig {

    public static final ObjectMapper restApiMapper = JsonUtils.dtoObjectMapper;

    private PersonPreferencesRestHandlersTestConfig() {

    }
}
