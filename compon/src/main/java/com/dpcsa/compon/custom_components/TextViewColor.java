package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.dpcsa.compon.R;

public class TextViewColor extends AppCompatTextView {

    private int colorText;

    public TextViewColor(Context context) {
        super(context);
        init(context, null);
    }

    public TextViewColor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TextViewColor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
        try {
            colorText = a.getInt(R.styleable.Simple_textColor, 0xFF000000);
        } finally {
            a.recycle();
        }
        setTextColor(colorText);
    }
}
