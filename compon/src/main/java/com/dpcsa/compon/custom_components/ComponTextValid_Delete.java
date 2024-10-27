package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ComponTextValid_Delete extends ComponEditText {

    public ComponTextValid_Delete(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ComponTextValid_Delete(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ComponTextValid_Delete(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);
        setBackground(null);
        setFocusable(false);
        setClickable(false);
        setMovementMethod(null);
        setKeyListener(null);
    }
}
