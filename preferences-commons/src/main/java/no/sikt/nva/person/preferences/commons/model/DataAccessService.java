package no.sikt.nva.person.preferences.commons.model;

import nva.commons.apigateway.exceptions.NotFoundException;

public interface DataAccessService<T extends DataAccessClass<T>> {

    String RESOURCE_NOT_FOUND_MESSAGE = "Could not find person preferences";
    String WITH_TYPE = "withType";

    void persist(T item) throws NotFoundException;

    T fetch(T item) throws NotFoundException;

}
