package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.single.Injector;

public class ComponWeb extends WebView implements IComponent, IAlias {

    public ComponGlob componGlob;

    public ComponWeb(Context context) {
        this(context, null);
    }

    public ComponWeb(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComponWeb(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        componGlob = Injector.getComponGlob();
    }

    @Override
    public void setData(Object data) {
        String detail = (String) data;
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        loadDataWithBaseURL(componGlob.appParams.baseUrl, detail,
                "text/html", "utf-8", null);
    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        return null;
    }
}
