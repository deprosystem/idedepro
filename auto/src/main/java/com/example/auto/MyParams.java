package com.example.auto;

import com.dpcsa.compon.param.AppParams;

public class MyParams extends AppParams {

    @Override
    public void setParams() {

        baseUrl = "https://apps.dp-ide.com/";
        nameLanguageInParam = "loc";
        initialLanguage = "en";
        nameLanguageInHeader = "Language";
    }
}
