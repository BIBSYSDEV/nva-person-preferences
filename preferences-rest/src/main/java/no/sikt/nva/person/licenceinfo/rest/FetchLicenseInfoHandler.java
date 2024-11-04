package no.sikt.nva.person.licenceinfo.rest;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.person.preferences.commons.model.LicenseInfoDto;
import no.sikt.nva.person.preferences.commons.model.LicenseInfoDao;
import no.sikt.nva.person.preferences.commons.service.IndexService;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.JacocoGenerated;

import java.net.HttpURLConnection;

import static no.sikt.nva.person.Constants.TABLE_NAME;
import static no.sikt.nva.person.Constants.getCristinId;


public class FetchLicenseInfoHandler extends ApiGatewayHandler<Void, LicenseInfoDto> {

    private final IndexService<LicenseInfoDao> indexService;

    @JacocoGenerated
    public FetchLicenseInfoHandler() {
        this(new IndexService<>(TABLE_NAME, LicenseInfoDao.class));
    }

    public FetchLicenseInfoHandler(IndexService<LicenseInfoDao> daoIndexService) {
        super(Void.class);
        this.indexService = daoIndexService;
    }

    @Override
    protected void validateRequest(Void unused, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        //        requestInfo.getCurrentCustomer();
    }

    @Override
    protected LicenseInfoDto processInput(Void input, RequestInfo requestInfo, Context context)
            throws ApiGatewayException {

        return LicenseInfoDao.builder()
                .withId(getCristinId(requestInfo))
                .build()
                .fetch(indexService)
                .toDto();
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, LicenseInfoDto output) {
        return HttpURLConnection.HTTP_OK;
    }

}
