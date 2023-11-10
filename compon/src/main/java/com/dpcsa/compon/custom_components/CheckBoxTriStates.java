package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.dpcsa.compon.R;

public class CheckBoxTriStates extends AppCompatCheckBox {
    static private final int UNKNOW = -1;
    static private final int UNCHECKED = 0;
    static private final int CHECKED = 1;
    private int state;
    private int colorUnknow, colorUnchecked, colorChecked;
    private int colorFlag;
    private Context context;
    private int DEF_COLOR;

    public CheckBoxTriStates(Context context) {
        super(context);
        init(context, null);
    }

    public CheckBoxTriStates(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CheckBoxTriStates(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        DEF_COLOR = getThemeColor("colorPrimary");
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                    0, 0);
            try {
                state = a.getInt(R.styleable.Simple_statusCheck, -1);
                colorUnknow = a.getColor(R.styleable.Simple_colorUnknow, DEF_COLOR);
                colorUnchecked = a.getColor(R.styleable.Simple_colorUnchecked, DEF_COLOR);
                colorChecked = a.getColor(R.styleable.Simple_colorChecked, DEF_COLOR);
                colorFlag = a.getColor(R.styleable.Simple_colorFlag, 0xffffffff);
            } finally {
                a.recycle();
            }
        }
        updateBtn();
        setOnCheckedChangeListener(checkListener);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            setPadding(0, 0, 15, 0);
        } else {
            setPadding(15, 0, 0, 0);
        }
    }

    private OnCheckedChangeListener checkListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (state == CHECKED) {
                state = UNKNOW;
            } else {
                state ++;
            }
            updateBtn();
        }
    };

    private void updateBtn() {
        switch (state) {
            default:
            case UNKNOW:
                setButtonDrawable(shapeUnknow(colorUnknow, colorFlag));
                break;
            case UNCHECKED:
                setButtonDrawable(shapeUncheck(colorUnchecked));
                break;
            case CHECKED:
                setButtonDrawable(shapeCheck(colorChecked, colorFlag));
                break;
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        updateBtn();
    }

    private LayerDrawable shapeCheck(int colorB, int color) {
        float[] outR = new float[] {6, 6, 6, 6, 6, 6, 6, 6};
        ShapeDrawable shape = new ShapeDrawable(
                new RoundRectShape(outR, null, null));
        shape.setIntrinsicHeight(50);
        shape.setIntrinsicWidth(50);
        shape.getPaint().setColor(colorB);
        Path p = new Path();
        p.moveTo(5, 20);
        p.lineTo(30, 40);
        p.lineTo(45,10);
        ShapeDrawable d = new ShapeDrawable(new PathShape(p, 50, 50));
        d.setIntrinsicHeight(50);
        d.setIntrinsicWidth(50);
        d.getPaint().setColor(color);
        d.getPaint().setStyle(Paint.Style.STROKE);
        d.getPaint().setStrokeWidth(7);
        Drawable drawableArray[] = new Drawable[] { shape, d };
        LayerDrawable layerDraw = new LayerDrawable(drawableArray);
        return layerDraw;
    }

    private ShapeDrawable shapeUncheck(int colorB) {
        float[] outR = new float[] {6, 6, 6, 6, 6, 6, 6, 6};
        RectF rectF = new RectF(6, 6, 6, 6);
        float[] inR = new float[] {6, 6, 6, 6, 6, 6, 6, 6};

        ShapeDrawable shape = new ShapeDrawable(
                new RoundRectShape(outR, rectF, inR));
        shape.setIntrinsicHeight(50);
        shape.setIntrinsicWidth(50);
        shape.getPaint().setColor(colorB);
        return shape;
    }

    private LayerDrawable shapeUnknow(int colorB, int color) {
        float[] outR = new float[] {6, 6, 6, 6, 6, 6, 6, 6};
        ShapeDrawable shape = new ShapeDrawable(
                new RoundRectShape(outR, null, null));
        shape.setIntrinsicHeight(50);
        shape.setIntrinsicWidth(50);
        shape.getPaint().setColor(colorB);
        Path p = new Path();
        p.moveTo(10, 25);
        p.lineTo(40, 25);
        ShapeDrawable d = new ShapeDrawable(new PathShape(p, 50, 50));
        d.setIntrinsicHeight(50);
        d.setIntrinsicWidth(50);
        d.getPaint().setColor(color);
        d.getPaint().setStyle(Paint.Style.STROKE);
        d.getPaint().setStrokeWidth(7);
        Drawable drawableArray[] = new Drawable[] { shape, d };
        LayerDrawable layerDraw = new LayerDrawable(drawableArray);
        return layerDraw;
    }

    public int getThemeColor (String nameColor) {
        int colorAttr = getResources().getIdentifier(nameColor, "attr", context.getPackageName());
        TypedValue value = new TypedValue ();
        context.getTheme().resolveAttribute (colorAttr, value, true);
        return value.data;
    }
}
