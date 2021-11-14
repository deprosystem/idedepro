package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
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
    private float corners, corner_lt, corner_lb, corner_rt, corner_rb;

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
            corners = a.getDimensionPixelSize(R.styleable.Simple_corners, -1);
            corner_lt = a.getDimensionPixelSize(R.styleable.Simple_corner_lt, -1);
            corner_lb = a.getDimensionPixelSize(R.styleable.Simple_corner_lb, -1);
            corner_rt = a.getDimensionPixelSize(R.styleable.Simple_corner_rt, -1);
            corner_rb = a.getDimensionPixelSize(R.styleable.Simple_corner_rb, -1);
        } finally {
            a.recycle();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        float[] radii;
        boolean isCorner = false;
        if (corners > 0) {
            radii = new float[]{corners, corners, corners, corners, corners, corners, corners, corners};
            isCorner = true;
        } else {
            radii = new float[]{0, 0, 0, 0, 0, 0, 0, 0};
            if (corner_lt > 0) {
                radii[0] = corner_lt;
                radii[1] = corner_lt;
                isCorner = true;
            }
            if (corner_lb > 0) {
                radii[6] = corner_lb;
                radii[7] = corner_lb;
                isCorner = true;
            }
            if (corner_rt > 0) {
                radii[2] = corner_rt;
                radii[3] = corner_rt;
                isCorner = true;
            }
            if (corner_rb> 0) {
                radii[4] = corner_rb;
                radii[5] = corner_rb;
                isCorner = true;
            }
        }
        if (isCorner) {
            Path clipPath = new Path();
            RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
            clipPath.addRoundRect(rect, radii, Path.Direction.CW);
            canvas.clipPath(clipPath);
        }
        super.onDraw(canvas);
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
