package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

import com.dpcsa.compon.R;

public class PhoneCountry extends ComponEditText {

    private int countryViewId;
    private TextView countryView;

    public PhoneCountry(Context context) {
        super(context);
//        init(context);
    }

    public PhoneCountry(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttribute(context, attrs);
    }

    public PhoneCountry(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttribute(context, attrs);
    }

    private void setAttribute(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                0, 0);
        try {
            countryViewId = a.getResourceId(R.styleable.Simple_countryView, 0);
        } finally {
            a.recycle();
        }
//        init(context);
    }

//    private void init(Context context) {
//        if (countryViewId != 0) {
//            countryView = (TextView) ((View)getParent()).findViewById(countryViewId);
//        }
//    }

    @Override
    public String getString() {
        if (countryViewId != 0) {
            View parent = getParentRoot(getParent());
            countryView = (TextView) parent.findViewById(countryViewId);
            return countryView.getText().toString() + getText().toString();
        } else {
            return getText().toString();
        }
    }

    private View getParentRoot(ViewParent view) {
        ViewParent viewRoot = view;
        ViewParent view2 = viewRoot;
        ViewParent v = viewRoot.getParent();
        while (v != null) {
            view2 = viewRoot;
            viewRoot = v;
            v = viewRoot.getParent();
        }
        return  (View) view2;
    }
}
