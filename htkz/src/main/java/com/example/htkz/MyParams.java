package com.example.htkz;

import com.dpcsa.compon.param.AppParams;

public class MyParams extends AppParams {

    @Override
    public void setParams() {
        baseUrl = "https://deprosystem.com/depro/";
        nameTokenInHeader = "DEVICE_KEY";
        isFullScreen = true;

        errorDialogViewId = R.id.error;
        idStringNOCONNECTIONERROR = R.string.no_connect;
        idStringNOCONNECTION_TITLE = R.string.no_connect_title;
    }
}
