package com.dpcsa.compon.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import com.dpcsa.compon.interfaces_classes.IVolleyListener;
import com.dpcsa.compon.param.AppParams;
import com.dpcsa.compon.single.Injector;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class VolleyRequest <T> extends Request<T> {

    public static final String PROTOCOL_CHARSET = "utf-8";
    public static final String PROTOCOL_CONTENT_TYPE = "application/json";
    private IVolleyListener listener;
    private Map<String, String> headers;
    private byte[] data;
    private AppParams appParams;

    public VolleyRequest(int method, String url, IVolleyListener listener,
                         Map<String, String> headers, byte[] data) {
        super(method, url, listener);
        appParams = Injector.getComponGlob().appParams;
        if (appParams.LOG_LEVEL > 1) Log.d(appParams.NAME_LOG_NET, "method=" + method + " url=" + url);
        this.headers = headers;
        this.listener = listener;
        if (data == null) {
            String st = "{}";
            this.data = st.getBytes();
        } else {
            this.data = data;
        }
        setRetryPolicy(new DefaultRetryPolicy(appParams.NETWORK_TIMEOUT_LIMIT,
                appParams.RETRY_COUNT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonSt = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            if (appParams.LOG_LEVEL > 2) Log.d(appParams.NAME_LOG_NET, "Respons json=" + jsonSt);
            CookieManager.checkAndSaveSessionCookie(response.headers);
            return Response.success( (T) jsonSt, HttpHeaderParser.parseCacheHeaders(response));
//            return Response.success( (T) Html.fromHtml(jsonSt).toString(),
//                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            if (appParams.LOG_LEVEL > 0) Log.d(appParams.NAME_LOG_NET, "UnsupportedEncodingException="+e);
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse((String) response);
    }

    @Override
    public void deliverError(VolleyError error) {
        listener.onErrorResponse(error);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (appParams.LOG_LEVEL > 2) Log.d(appParams.NAME_LOG_NET,"VolleyRequest headers="+headers);
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    protected String getParamsEncoding() {
        return PROTOCOL_CHARSET;
    }

    @Override
    protected Map<String, String> getParams() {
//        Log.d("SMPL", "VOLLEY Params=");
        return null;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (appParams.LOG_LEVEL > 2) Log.d(appParams.NAME_LOG_NET,"VolleyRequest getBody data="+new String(data));
        return data;
    }
}
