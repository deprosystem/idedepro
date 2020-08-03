package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.dpcsa.compon.R;

public class ImageViewColor extends AppCompatImageView {

    private int colorText;

    public ImageViewColor(Context context) {
        super(context);
        init(context, null);
    }

    public ImageViewColor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageViewColor(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
        try {
            colorText = a.getInt(R.styleable.Simple_iconColor, 0xFF000000);
        } finally {
            a.recycle();
        }
        setColors();
    }

    public void setColors() {
        ColorStateList stateList = new ColorStateList(
                new int[][]{
                        new int[]{}
                },
                new int[] {
                        colorText
                }
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setImageTintList(stateList);
        } else {
            setImageDrawable(tintIcon(getDrawable(), stateList));
        }
    }

    public Drawable tintIcon(Drawable icon, ColorStateList colorStateList) {
        if(icon!=null) {
            icon = DrawableCompat.wrap(icon).mutate();
            DrawableCompat.setTintList(icon, colorStateList);
            DrawableCompat.setTintMode(icon, PorterDuff.Mode.SRC_IN);
        }
        return icon;
    }
}
