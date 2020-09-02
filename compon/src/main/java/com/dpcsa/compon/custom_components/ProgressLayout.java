package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.dpcsa.compon.R;

import androidx.annotation.Nullable;

import static android.graphics.drawable.GradientDrawable.Orientation.RIGHT_LEFT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ProgressLayout extends LinearLayout {

    Context context;
    static int colorProgr, colorProgrN;
    LinearLayout lAnim;

    public ProgressLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ProgressLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                0, 0);
        try {
            colorProgr = a.getColor(R.styleable.Simple_progressColorE, 0);
            colorProgrN = a.getColor(R.styleable.Simple_progressColorN, 0);
        } finally {
            a.recycle();
        }
        setOrientation(HORIZONTAL);
        if (colorProgr == 0) {
            colorProgr = getThemeColor("colorAccent");
        }
        isW.run();
    }

    Handler handler = new Handler();

    Runnable isW = new Runnable() {
        @Override
        public void run() {
            int ww = getWidth();
            if (ww != 0) {
                startAnim(ww);
            } else {
                handler.postDelayed(isW, 20);
            }
        }
    };

    private void startAnim(int ww) {
        int wGrad = ww / 4;
        lAnim = new LinearLayout(context);
        LayoutParams lp = new LayoutParams(wGrad, MATCH_PARENT);
        lAnim.setLayoutParams(lp);
        lAnim.setBackground(gradient());
        addView(lAnim);
        TranslateAnimation animate = new TranslateAnimation(-wGrad, ww, 0, 0);
        animate.setDuration(700);
        animate.setRepeatCount(100);
        animate.setFillAfter(true);
        lAnim.startAnimation(animate);
    }

    public static GradientDrawable gradient() {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[] { colorProgr, colorProgrN});
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setOrientation(RIGHT_LEFT);
        return drawable;
    }

    public int getThemeColor (String nameColor) {
        int colorAttr = getResources().getIdentifier(nameColor, "attr", context.getPackageName());
        TypedValue value = new TypedValue ();
        context.getTheme().resolveAttribute (colorAttr, value, true);
        return value.data;
    }
}
