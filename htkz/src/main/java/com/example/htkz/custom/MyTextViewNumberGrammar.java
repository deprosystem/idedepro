package com.example.htkz.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.dpcsa.compon.custom_components.TextViewNumberGrammar;
import com.dpcsa.compon.single.Injector;

public class MyTextViewNumberGrammar extends TextViewNumberGrammar {

    public MyTextViewNumberGrammar(Context context) {
        super(context);
        init();
    }

    public MyTextViewNumberGrammar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewNumberGrammar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        String st = Injector.getComponGlob().getParamValue("night");
        if (st != null && st.length() > 0) {
            setText(st);
        } else {

            setText("7 - 9");
        }
    }
}
