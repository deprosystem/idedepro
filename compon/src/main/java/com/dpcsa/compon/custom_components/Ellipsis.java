package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dpcsa.compon.R;
import com.google.android.gms.maps.MapView;

import androidx.annotation.Nullable;

public class Ellipsis extends LinearLayout {
    private Context context;
    private int amountDots = 3, colorDot = 0xffff0000;
    float DENSITY = getResources().getDisplayMetrics().density;
    float diametrDot = 1;
    private int diam = (int)(DENSITY + 0.5);


    public Ellipsis(Context context) {
        super(context);
        init(context, null);;
    }

    public Ellipsis(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);;
    }

    public Ellipsis(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);;
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                    0, 0);
            try {
                amountDots = a.getInteger(R.styleable.Simple_amountDots, 3);
                colorDot = a.getColor(R.styleable.Simple_colorDot, 0xffff0000);
                diametrDot = a.getDimension(R.styleable.Simple_diametrDot, DENSITY);
            } finally {
                a.recycle();
            }
            diam = (int)(diametrDot + 0.5f);
        }
        for (int i = 0; i < amountDots; i++) {
            LinearLayout ll = new LinearLayout(context);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            ll.setLayoutParams(lp);
            ll.setGravity(Gravity.CENTER);
            View v = new View(context);
            LayoutParams lpV = new LayoutParams(diam, diam);
            v.setLayoutParams(lpV);
            v.setBackgroundColor(colorDot);
            ll.addView(v);
            addView(ll);
        }
    }
}
