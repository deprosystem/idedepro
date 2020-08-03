package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

//import com.makeramen.roundedimageview.RoundedImageView;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;

public class ComponImageView extends androidx.appcompat.widget.AppCompatImageView implements IAlias {
    private Context context;
    private int placeholder, blur;
    private boolean oval;
    private String pathImg;
    private String alias;

    public ComponImageView(Context context) {
        super(context);
        this.context = context;
    }

    public ComponImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttrs(attrs);
    }

    public ComponImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setAttrs(attrs);
    }

    private void setAttrs(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
        try {
            placeholder = a.getResourceId(R.styleable.Simple_placeholder, 0);
            blur = a.getInt(R.styleable.Simple_blur, -1);
            oval = a.getBoolean(R.styleable.Simple_oval, false);
            alias = a.getString(R.styleable.Simple_alias);
        } finally {
            a.recycle();
        }

    }

    public void setPathImg(String path) {
        pathImg = path;
    }

    public String getPathImg() {
        return pathImg;
    }

    public int getPlaceholder() {
        return placeholder;
    }

    public int getBlur() {
        return blur;
    }

    public boolean isOval() {
        return oval;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
