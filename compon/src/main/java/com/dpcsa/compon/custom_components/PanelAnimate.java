package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.AnimatePanel;
import com.dpcsa.compon.interfaces_classes.IBase;

public class PanelAnimate extends LinearLayout implements AnimatePanel {
    private Context context;
    private View view;
    private int viewId;
    private IBase iBase;
    private int duration = 2000;
    private float heigthF;

    public PanelAnimate(Context context) {
        super(context);
        init(context, null);
    }

    public PanelAnimate(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PanelAnimate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
            try {
            viewId = a.getResourceId(R.styleable.Simple_viewLayoutId, 0);
            } finally {
                a.recycle();
            }
        }
        if (viewId != 0) {
            setView();
        }
    }

    private void setView() {
        if (viewId != 0) {
            view = LayoutInflater.from(context).inflate(viewId, null);
            addView(view);
        }
    }

    private void animateOpen() {
        setVisibility(VISIBLE);
//        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
//                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0, 1);
        animation.setDuration(duration);
        startAnimation(animation);
    }

    @Override
    public void show(IBase iBase) {
        if (getVisibility() == GONE) {
            setVisibility(VISIBLE);
            animateOpen();
//            open();
//            this.iBase = iBase;
//            iBase.addAnimatePanel(this);
//            if (showTime > 0) {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        hide();
//                    }
//                }, showTime);
//            }
        }
    }

    @Override
    public void hide() {
        if (getVisibility() == VISIBLE) {
//            fadedScreenClose(true, null);
//            iBase.delAnimatePanel(this);
        }
    }

    @Override
    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }
}
