package com.dpcsa.compon.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.dpcsa.compon.interfaces_classes.IVolleyListener;
import com.dpcsa.compon.param.AppParams;
import com.dpcsa.compon.single.Injector;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.CharsetUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MultipartRequest extends Request<String> {
    private static final String CONTENT_TYPE_IMAGE = "multipart/form-data";
    private final Map<String, File> mFilePart;
    public static final String PROTOCOL_CHARSET = "UTF-8";
    private IVolleyListener listener;
    private Map<String, String> headers;
    private String data;
    MultipartEntityBuilder entity = MultipartEntityBuilder.create();
    HttpEntity httpentity;
    private AppParams appParams;

    public MultipartRequest(String url, IVolleyListener listener, Map<String, String> headers, String data, Map<String, File> file) {
        super(Method.POST, url, listener);
        appParams = Injector.getComponGlob().appParams;
        if (appParams.LOG_LEVEL > 1) Log.d(appParams.NAME_LOG_NET, "method=" + Method.POST + " url=" + url);

        this.headers = headers;
        this.listener = listener;
        this.data = data;
        setRetryPolicy(new DefaultRetryPolicy(appParams.NETWORK_TIMEOUT_LIMIT,
                appParams.RETRY_COUNT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mFilePart = file;
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        try {
            entity.setCharset(CharsetUtils.get("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setBoundary("----WebKitFormBoundary");
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        for (Map.Entry<String, File> entry : mFilePart.entrySet()) {
            if (appParams.LOG_LEVEL > 1) Log.d(appParams.NAME_LOG_NET, "Multipart file=" + entry.getValue().getName());
            entity.addBinaryBody(entry.getKey(), entry.getValue(), ContentType.create(CONTENT_TYPE_IMAGE),
                    entry.getValue().getName());
        }
        if (appParams.LOG_LEVEL > 1) Log.d(appParams.NAME_LOG_NET, "Multipart data=" + data);
        entity.addTextBody("data", data, ContentType.APPLICATION_JSON);
        httpentity = entity.build();
    }

    @Override
    public   String   getBodyContentType ( )   {
        return httpentity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            httpentity.writeTo(bos);
        } catch (IOException e) {
            Log.d(appParams.NAME_LOG_NET,"Multipart getBody error="+e);
        }
//Log.d("QWERT","getBody bos="+bos.toString());
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonSt = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            if (appParams.LOG_LEVEL > 2) Log.d(appParams.NAME_LOG_NET, "Multipart Respons json=" + jsonSt);
            CookieManager.checkAndSaveSessionCookie(response.headers);
            return Response.success( jsonSt, HttpHeaderParser.parseCacheHeaders(response));
//            return Response.success( (T) Html.fromHtml(jsonSt).toString(),
//                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            if (appParams.LOG_LEVEL > 0) Log.d(appParams.NAME_LOG_NET, "Multipart UnsupportedEncodingException="+e);
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (appParams.LOG_LEVEL > 2) Log.d(appParams.NAME_LOG_NET,"Multipart headers="+headers);
        return headers;
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
//        if (error.networkResponse != null && error.networkResponse.data != null) {
//            Log.d(appParams.NAME_LOG_NET, "VolleyRequest deliverError error=" + error.networkResponse.data.toString());
//        }
        listener.onErrorResponse(error);
    }


    @Override
    protected String getParamsEncoding() {
        return PROTOCOL_CHARSET;
    }
}
