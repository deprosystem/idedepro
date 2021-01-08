package com.dpcsa.compon.custom_components;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class ComponRadioButtonRL extends RelativeLayout {

    Context context;
    ComponRadioButton crb;
    LinearLayout ll;
    float DENSITY = getResources().getDisplayMetrics().density;
    float r = 50 * DENSITY;
    float[] outR = new float[] {r, r, r, r, r, r, r, r };

    public ComponRadioButtonRL(Context context) {
        super(context);
        init(context, null);
    }

    public ComponRadioButtonRL(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ComponRadioButtonRL(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        ll = new LinearLayout(context);
        RelativeLayout.LayoutParams pl = new RelativeLayout.LayoutParams(30, 20);
        pl.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        ll.setLayoutParams(pl);
        ll.setVisibility(GONE);
        ShapeDrawable shapeSplash = new ShapeDrawable (new RoundRectShape(outR, null, null));
        shapeSplash.getPaint().setColor(0x55eeeeee);
        ll.setBackground(shapeSplash);
        addView(ll);
        crb = new ComponRadioButton(context);
        RelativeLayout.LayoutParams lpL = new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        crb.setLayoutParams(lpL);
        addView(crb);
    }

    public void setImg(int img, int il) {
        crb.setImg(img, il);
    }

    public void setText(int txt) {
        crb.setText(txt);
    }

    public void setColors(int colorNorm, int colorSelect, int backgr, boolean noImgChangeColor, boolean toAnimate) {
        crb.setColors(colorNorm, colorSelect, backgr, noImgChangeColor, toAnimate);
    }

    public void animClick() {
        ll.setVisibility(VISIBLE);
        ScaleAnimation sa = new ScaleAnimation(1f, 6f, 1f, 6f,
                RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(200);
        sa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ll.startAnimation(sa);
    }
}
