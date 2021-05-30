package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.dpcsa.compon.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ElementMenu extends RecyclerView {
    private Context context;
    public int colorNorm, colorSelect, colorEnabl, colorBadge, colorDivider;
    public int colorNormDef, colorSelectDef, colorEnablDef = 0xffcccccc, colorBadgeDef, colorDividerDef = 0xffcccccc;

    public ElementMenu(@NonNull Context context) {
        super(context);
    }

    public ElementMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ElementMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        context = getContext();
        colorNormDef = getThemeColor("colorPrimary");
        colorSelectDef = getThemeColor("colorPrimaryDark");
        colorBadgeDef = getThemeColor("colorAccent");
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
        colorNorm = a.getColor(R.styleable.Simple_normColor, colorNormDef);
        colorSelect = a.getColor(R.styleable.Simple_selectColor, colorSelectDef);
        colorEnabl = a.getColor(R.styleable.Simple_enablColor, colorEnablDef);
        colorBadge = a.getColor(R.styleable.Simple_badgeColor, colorBadgeDef);
        colorDivider = a.getColor(R.styleable.Simple_dividerColor, colorDividerDef);
        a.recycle();
    }

    public int getThemeColor (String nameColor) {
        int colorAttr = getResources().getIdentifier(nameColor, "attr", context.getPackageName());
        TypedValue value = new TypedValue ();
        context.getTheme().resolveAttribute (colorAttr, value, true);
        return value.data;
    }
}
