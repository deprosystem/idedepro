package com.dpcsa.compon.base;

import java.io.File;
import java.util.Map;

public class BaseInternetProvider {
    protected int method;
    protected String url;
    protected String data;
    protected InternetProviderListener listener;
    protected boolean isCanceled;
    public static final int ERRORINMESSAGE = 700;
    public static final int NOCONNECTIONERROR = 701;
    public static final int TIMEOUT = 702;
    public static final int SERVERERROR = 703;
    public static final int AUTHFAILURE = 704;
    public static final int JSONSYNTAXERROR = 705;
    public static final int COUNTRY_CODE = 706;
    public static final int NO_AUTH = 707;
    public static final int NO_HTTP = 708;

    public void setParam(int method, String url, Map<String, String> headers,
                         String data, Map<String, File> file, InternetProviderListener listener) {
        this.method = method;
        this.url = url;
        this.data = data;
        this.listener = listener;
        isCanceled = false;
    }

    public void cancel() {
        isCanceled = true;
    }

    public BaseInternetProvider getThis() {
        return this;
    }

    public interface InternetProviderListener {
        void response(String response);
        void error(int statusCode, String message);
    }
}
