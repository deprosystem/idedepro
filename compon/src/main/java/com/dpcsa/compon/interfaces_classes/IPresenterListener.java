package com.dpcsa.compon.interfaces_classes;

import android.view.View;

import com.dpcsa.compon.json_simple.Field;

public interface IPresenterListener {
    void onResponse(Field response);
    void onError(int statusCode, String message, View.OnClickListener click);
}
