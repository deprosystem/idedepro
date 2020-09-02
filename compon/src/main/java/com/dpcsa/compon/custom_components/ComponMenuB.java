package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RadioGroup;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;

public class ComponMenuB extends RadioGroup implements IAlias {
    private Context context;
    private String alias;
    public int imageLocale;
    public int colorNorm, colorSelect;
    public int colorNormDef, colorSelectDef;
    public int selBackgr;
    public boolean noSelImgChangeColor, toAnimate;

    public ComponMenuB(Context context) {
        super(context);
    }

    public ComponMenuB(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        colorNormDef = getThemeColor("colorAccent");
        colorSelectDef = getThemeColor("colorAccentDark");
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
        imageLocale = a.getInt(R.styleable.Simple_imageLocale, 1);
        colorNorm = a.getColor(R.styleable.Simple_normColor, colorNormDef);
        colorSelect = a.getColor(R.styleable.Simple_selectColor, colorSelectDef);
        selBackgr = a.getResourceId(R.styleable.Simple_selectBackground, 0);
        noSelImgChangeColor = a.getBoolean(R.styleable.Simple_noSelImgChangeColor, false);
        toAnimate = a.getBoolean(R.styleable.Simple_toAnimate, true);
        alias = a.getString(R.styleable.Simple_alias);
        a.recycle();
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public int getThemeColor (String nameColor) {
        int colorAttr = getResources().getIdentifier(nameColor, "attr", context.getPackageName());
        TypedValue value = new TypedValue ();
        context.getTheme().resolveAttribute (colorAttr, value, true);
        return value.data;
    }
}
