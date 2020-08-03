package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;

public class ComponMenuB extends RadioGroup implements IAlias {
    private Context context;
    private String alias;

    public ComponMenuB(Context context) {
        super(context);
    }

    public ComponMenuB(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
//        numberFormat = a.getString(R.styleable.Simple_numberFormat);
//        moneyFormat = a.getString(R.styleable.Simple_moneyFormat);
//        dateFormat = a.getString(R.styleable.Simple_dateFormat);
//        dateMilisec = a.getBoolean(R.styleable.Simple_dateMilisec, true);
        alias = a.getString(R.styleable.Simple_alias);
        a.recycle();
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
