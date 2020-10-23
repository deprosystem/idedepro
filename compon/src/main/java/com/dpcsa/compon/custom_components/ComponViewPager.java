package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class ComponViewPager extends ViewPager {

    public boolean ScrollEnabled;

    public ComponViewPager(@NonNull Context context) {
        super(context);
        ScrollEnabled = true;
    }

    public ComponViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ScrollEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.ScrollEnabled ? super.onTouchEvent(event) : false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.ScrollEnabled ? super.onInterceptTouchEvent(event) : false;
    }

    @Override
    public boolean executeKeyEvent(KeyEvent event) {
        return this.ScrollEnabled ? super.executeKeyEvent(event) : false;
    }

    public void setSwipeEnabled(boolean enabled) {
        this.ScrollEnabled = enabled;
    }
}
