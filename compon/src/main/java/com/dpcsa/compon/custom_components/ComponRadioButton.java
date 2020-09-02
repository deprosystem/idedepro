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
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.dpcsa.compon.R;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class ComponRadioButton extends LinearLayout {

    public ImageView iv;
    public TextView tv;
    private Context context;
    float DENSITY = getResources().getDisplayMetrics().density;
    float SP_DENSITY = getResources().getDisplayMetrics().scaledDensity;

    int imageLocation;
    float tv_size = 12f;
    float sp_10 = SP_DENSITY * 10;
    float sp_txt = SP_DENSITY * 8;
    float sp_12 = SP_DENSITY * 12;
    float sp_8 = SP_DENSITY * 8;
    int dp_6 = (int) (6 * DENSITY);
    int dp_10 = (int) (10 * DENSITY);
    int dp_24 = (int) (24f * DENSITY);
    int img_s = (int) (22f * DENSITY);
    int colorNorm, colorSelect;
    int stText = 0;
    public int selectBackground;
    boolean noImgChangeColor, toAnimate;

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
        setGravity(Gravity.CENTER);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        tv.setSelected(selected);
        if (iv != null) {
            iv.setSelected(selected);
        }
        if (selectBackground != 0) {
            if (selected) {
                setBackgroundResource(selectBackground);
            } else {
                setBackgroundColor(0);
            }
        }
        if (toAnimate) {
            if (selected) {
                animate().scaleXBy(0.15f).scaleYBy(0.15f).setDuration(200).start();
            } else {
                animate().scaleXBy(-0.15f).scaleYBy(-0.15f).setDuration(200).start();
            }
        }
    }

    private void setSelect(ImageView iv) {
        if (iv == null || noImgChangeColor) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv.setImageTintList(selectorImage());
        } else {
            iv.setImageDrawable(tintIcon(iv.getDrawable(), selectorImage()));
        }
    }

    public void setImg(int img, int il) {
        switch (il) {
            case 0:     //  left
                removeAllViews();
                setOrientation(HORIZONTAL);
                if (img != 0) {
                    newImg();
                    addView(iv);
                }
                newText(dp_6, 0);
                addView(tv);
                break;
            case 1:     //  top
                removeAllViews();
                setOrientation(VERTICAL);
                if (img != 0) {
                    newImg();
                    addView(iv);
                }
                newText(0, 0);
                addView(tv);
                break;
            case 2:     //  right
                removeAllViews();
                setOrientation(HORIZONTAL);
                newText(0, dp_6);
                tv.setTextSize(sp_12);
                addView(tv);
                if (img != 0) {
                    newImg();
                    addView(iv);
                }
                break;
            case 3:     //  bottom
                removeAllViews();
                setOrientation(VERTICAL);
                newText(0, 0);
                addView(tv);
                if (img != 0) {
                    newImg();
                    addView(iv);
                }
                break;
        }
        if (stText != 0) {
            tv.setText(stText);
        }
        if (iv != null) {
            iv.setImageResource(img);
        }
    }

    private void newImg() {
        iv = new ImageView(context);
        LayoutParams li = new LayoutParams(img_s, img_s);
        iv.setLayoutParams(li);
        setSelect(iv);
    }

    private void newText(int margL, int margR) {
        tv = new TextView(context);
        LayoutParams lt = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        if (margL != 0 || margR != 0) {
            lt.setMargins(margL, 0, margR, 0);
        }
        tv.setLayoutParams(lt);
        tv.setTextSize(tv_size);
        tv.setTextColor(selectorText());
    }

    public void setText(int txt) {
        stText = txt;
        tv.setText(txt);
    }

    public void setColors(int colorNorm, int colorSelect, int backgr, boolean noImgChangeColor, boolean toAnimate) {
        this.colorNorm = colorNorm;
        this.colorSelect = colorSelect;
        this.noImgChangeColor = noImgChangeColor;
        this.toAnimate = toAnimate;
        selectBackground = backgr;
        if (tv != null) {
            tv.setTextColor(selectorText());
        }
        if (iv != null) {
            setSelect(iv);
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
