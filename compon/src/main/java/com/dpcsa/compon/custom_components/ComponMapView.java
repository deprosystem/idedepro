package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.google.android.gms.maps.MapView;

import com.dpcsa.compon.R;

public class ComponMapView extends MapView {

    Context context;
    public int zoomPlus, zoomMinus, location;

    public ComponMapView(Context context) {
        this(context, null);
    }

    public ComponMapView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ComponMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.context = context;
        setAttribute(attributeSet);
    }

    private void setAttribute(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
        try {
            zoomPlus = a.getResourceId(R.styleable.Simple_plusViewId, 0);
            zoomMinus = a.getResourceId(R.styleable.Simple_minusViewId, 0);
            location = a.getResourceId(R.styleable.Simple_location, 0);
        } finally {
            a.recycle();
        }
    }
}
