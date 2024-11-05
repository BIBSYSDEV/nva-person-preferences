package no.sikt.nva.person.termsconditions.rest;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.person.preferences.commons.model.TermsConditionsDto;
import no.sikt.nva.person.preferences.commons.model.TermsConditionsDao;
import no.sikt.nva.person.preferences.commons.service.IndexService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.JacocoGenerated;

import java.net.HttpURLConnection;

import static no.sikt.nva.person.Constants.TABLE_NAME;
import static no.sikt.nva.person.Constants.getCristinId;


public class FetchTermsConditionsHandler extends ApiGatewayHandler<Void, TermsConditionsDto> {

    private final IndexService<TermsConditionsDao> indexService;

    @JacocoGenerated
    public FetchTermsConditionsHandler() {
        this(new IndexService<>(TABLE_NAME, TermsConditionsDao.class));
    }

    public FetchTermsConditionsHandler(IndexService<TermsConditionsDao> daoIndexService) {
        super(Void.class);
        this.indexService = daoIndexService;
    }

    @Override
    protected void validateRequest(Void unused, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        //        requestInfo.getCurrentCustomer();
    }

    @Override
    protected TermsConditionsDto processInput(Void input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {

        return TermsConditionsDao.builder()
                .personId(getCristinId(requestInfo))
                .build()
                .fetch(indexService)
                .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, TermsConditionsDto output) {
        return HttpURLConnection.HTTP_OK;
    }

}
