package no.sikt.nva.person.preferences.commons.model;

import nva.commons.apigateway.exceptions.NotFoundException;

import java.net.URI;
import java.time.Instant;

public interface DataAccessClass<T extends DataAccessClass<T>> {

    URI withId();

    String withType();

    Instant created();

    Instant modified();

    T upsert(DataAccessService<T> service) throws NotFoundException;

    T fetch(DataAccessService<T> service) throws NotFoundException;

}
