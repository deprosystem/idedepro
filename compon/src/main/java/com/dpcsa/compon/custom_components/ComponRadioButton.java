package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ComponRadioButton extends LinearLayout {

    public ImageView iv;
    public TextView tv;
    private Context context;
    float DENSITY = getResources().getDisplayMetrics().density;
    float SP_DENSITY = getResources().getDisplayMetrics().scaledDensity;

    float sp_10 = SP_DENSITY * 10;
    float sp_8 = SP_DENSITY * 8;
    int dp_10 = (int) (10 * DENSITY);
    int dp_24 = (int) (24f * DENSITY);
    int colorNorm, colorSelect;

    public ComponRadioButton(Context context) {
        super(context);
        init(context, null);
    }

    public ComponRadioButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ComponRadioButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        colorNorm = getThemeColor("colorAccent");
        colorSelect = getThemeColor("colorAccentDark");
        setPadding(0, dp_10, 0, 0);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        iv = new ImageView(context);
        LayoutParams li = new TableLayout.LayoutParams(dp_24, dp_24);
        iv.setLayoutParams(li);
        setSelect(iv);
        addView(iv);

        tv = new TextView(context);
        LayoutParams lt = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        tv.setLayoutParams(lt);
        tv.setTextSize(10);
        tv.setTextColor(selectorText());
        addView(tv);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        tv.setSelected(selected);
        iv.setSelected(selected);
    }

    private void setSelect(ImageView iv) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv.setImageTintList(selectorImage());
        } else {
            iv.setImageDrawable(tintIcon(iv.getDrawable(), selectorImage()));
        }
    }

    public void setImg(int img) {
        iv.setImageResource(img);
    }

    public void setText(int txt) {
        tv.setText(txt);
    }

    public void setColors(int colorNorm, int colorSelect) {
        this.colorNorm = colorNorm;
        this.colorSelect = colorSelect;
        setSelect(iv);
        tv.setTextColor(selectorText());
    }

    public Drawable tintIcon(Drawable icon, ColorStateList colorStateList) {
        if(icon!=null) {
            icon = DrawableCompat.wrap(icon).mutate();
            DrawableCompat.setTintList(icon, colorStateList);
            DrawableCompat.setTintMode(icon, PorterDuff.Mode.SRC_IN);
        }
        return icon;
    }

    public ColorStateList selectorImage() {
        ColorStateList selectorImage = new ColorStateList(
                new int[][]{
                    new int[]{android.R.attr.state_selected},
                    new int[]{}
                },
                new int[] {
                    colorSelect, colorNorm
                }
        );
        return selectorImage;
    }

    public ColorStateList selectorText() {
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_selected},
                new int[] {}
        };
        int[] colors = new int[] {
                colorSelect, colorNorm
        };
        return new ColorStateList(states, colors);
    }

    public int getThemeColor (String nameColor) {
        int colorAttr = getResources().getIdentifier(nameColor, "attr", context.getPackageName());
        TypedValue value = new TypedValue ();
        context.getTheme().resolveAttribute (colorAttr, value, true);
        return value.data;
    }
}
