package com.dpcsa.compon.interfaces_classes;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public interface VolleyListener<T> extends Response.ErrorListener{
    public void onErrorResponse(VolleyError error);

    public void onResponse(T response);
}
