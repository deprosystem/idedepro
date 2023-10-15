package com.dpcsa.compon.param;

import com.dpcsa.compon.R;

import androidx.annotation.NonNull;

public abstract class AppParams<T> {
    public int youtubeApiKey = 0;   // id строкового ресурса с youtube Api Key

    public String baseUrl;  // Базовый Url
    public String schema = "";
    public int defaultMethod = ParamModel.GET;
        // Если в модели не будет указан явно метод, то будет использоваться метод заданный в этом параметре
    public int NETWORK_TIMEOUT_LIMIT = 30000; // milliseconds
    public int RETRY_COUNT = 0; // Сколько будет дополнительных обращений к серверу в случае неудачи
    public ParamModel.TypeParam typeParameterTransfer = ParamModel.TypeParam.NAME;
        // тип передачи параметров в запросе: NAME - пары название - значение после знака "?"
        // SLASH - значения разделенные знаком "/"
    public int LOG_LEVEL = 3;  // 0 - not, 1 - ERROR, 2 - ERROR + URL, 3 - ERROR + URL + jsonResponse
    public static String NAME_LOG_NET = "SMPL_NET";
    public static String NAME_LOG_APP = "SMPL_APP";
    public static String NAME_LOG_DB = "SMPL_DB";

    public boolean isFullScreen = false;

    public int paginationPerPage = 20;  // количество записей на странице (в блоке) при пагинации
    public String paginationNameParamPerPage = "PerPage";   // название ключа заголовка в котором передается paginationPerPage
    public String paginationNameParamNumberPage = "NumberPage"; // название ключа заголовка в котором передается номер страницы
    public String commonParameters = "";
    public Class<T>  classProgress;
    public Class<T>  classErrorDialog;

    public int errorDialogViewId = 0,
            errorDialogLayoutId = 0,
            errorDialogPositiveId = 0,  // id элемента разметки, который соответствует кнопке Positive
            errorDialogNegativeId = 0;  // id элемента разметки, который соответствует кнопке Negative
    public int progressLayoutId = R.layout.smpl_dialog_progress;

    public String nameTokenInHeader = "Auth-token";
    public String nameTokenPush = "push-token";
    public String nameTokenPushInHeader = "push-token";

    public String nameLanguageInHeader = "",
            nameLanguageInParam = "",
            initialLanguage = "";

    public String nameVersionInHeader = "",
            valueVersionInHeader = "";

    public boolean nameLanguageInURL = false;

    public int idStringERRORINMESSAGE = 0;
    public int idStringDefaultErrorTitle = 0;
    public int idStringNOCONNECTION_TITLE = 0;
    public int idStringNOCONNECTIONERROR = 0;
    public int idStringTIMEOUT = 0;
    public int idStringSERVERERROR = 0;
    public int idStringJSONSYNTAXERROR = 0;
    public int idStringNO_AUTH = 0;
    public int idStringNO_HTTP = 0;

    public abstract void setParams();
    public AppParams() {
        setParams();
    }
}
