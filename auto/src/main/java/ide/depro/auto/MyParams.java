package ide.depro.auto;

import com.dpcsa.compon.param.AppParams;

public class MyParams extends AppParams {

    @Override
    public void setParams() {

        baseUrl = "https://apps.dp-ide.com/";
        schema = "uyrz0xz0st6tek9";
        nameLanguageInParam = "loc";
        initialLanguage = "en";
        nameLanguageInHeader = "Language";
    }
}
