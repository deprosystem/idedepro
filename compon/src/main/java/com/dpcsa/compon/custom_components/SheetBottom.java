package com.dpcsa.compon.custom_components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.AnimatePanel;
import com.dpcsa.compon.interfaces_classes.IBase;

public class SheetBottom extends RelativeLayout implements AnimatePanel {

    private LinearLayout fadedScreen;
    private SwipeY panel;
    private FrameLayout sheetContainer;
    private Context context;
    private SheetBottomListener allertListener;
    private float heigthF;
    private int duration = 200;
    private int idContainer;
    private int viewId;
    private int negativeViewId, positiveViewId;
    private int fadedScreenColorDefault = 0x50000000;
    private int fadedScreenColor = fadedScreenColorDefault;
    private IBase iBase;
    private SheetBottom thisSheet;
    private int showTime;

    public SheetBottom(Context context) {
        super(context);
        init(context, null);
    }

    public SheetBottom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SheetBottom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        thisSheet = this;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
            viewId = a.getResourceId(R.styleable.Simple_viewId, 0);
            negativeViewId = a.getResourceId(R.styleable.Simple_negativeViewId, 0);
            positiveViewId = a.getResourceId(R.styleable.Simple_positiveViewId, 0);
            showTime = a.getInt(R.styleable.Simple_showTime, 0);
            fadedScreenColor = a.getColor(R.styleable.Simple_fadedScreenColor, fadedScreenColorDefault);
            a.recycle();
        }
        fadedScreen = new LinearLayout(context);
        LayoutParams lpFadedScreen = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        fadedScreen.setLayoutParams(lpFadedScreen);
        fadedScreen.setGravity(Gravity.BOTTOM);
        fadedScreen.setBackgroundColor(fadedScreenColor);
        addView(fadedScreen);
        panel = new SwipeY(context);
        LinearLayout.LayoutParams lpPanel = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        panel.setLayoutParams(lpPanel);
        fadedScreen.addView(panel);
        sheetContainer = new FrameLayout(context);
        LayoutParams lpContainer = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        sheetContainer.setLayoutParams(lpContainer);
        panel.addView(sheetContainer);
        LayoutInflater.from(context).inflate(viewId, sheetContainer);
        super.setVisibility(GONE);
    }

    public void open() {
        super.setVisibility(VISIBLE);
        fadedScreen.setAlpha(0.0f);
//        fadedScreen.setBackgroundResource(fadedColor);
        fadedScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
//                fadedScreenClose(true, null);
            }
        });
//        LayoutInflater.from(context).inflate(viewId, sheetContainer);
        if (negativeViewId != 0) {
            final View hide = sheetContainer.findViewById(negativeViewId);
            hide.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hide();
                }
            });
        }
        if (positiveViewId != 0) {
            final View positiv = sheetContainer.findViewById(positiveViewId);
            positiv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerForView.proceedChanges(null);
//                    fadedScreenClose(false, null);
                }
            });
        }
//        EditText ed = (EditText) sheetContainer.findViewById(R.id.edit);
//        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
//                Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.showSoftInput(ed, 0);
        startAnim.run();
    }

//    public void open(Fragment sheetFragment, int fadedColor) {
//        setVisibility(VISIBLE);
//        fadedScreen.setAlpha(0.0f);
//        fadedScreen.setBackgroundResource(fadedColor);
//        fadedScreen.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fadedScreenClose(true, null);
//            }
//        });
//        if (sheetFragment instanceof SheetBottomFragment) {
//            ((SheetBottomFragment) sheetFragment).setSheetBottomListener(listenerForView);
//        }
//        fragmentManager.beginTransaction().replace(idContainer, sheetFragment).commit();
//        startAnim.run();
//    }

    Handler handler = new Handler();

    private Runnable startAnim = new Runnable() {
        @Override
        public void run() {
            heigthF = panel.getHeight();
            if (heigthF > 0f) {
                panel.setSwipeView(sheetContainer, listenerForView);
                fadedScreenOpen();
            } else {
                handler.postDelayed(startAnim, 20);
            }
        }
    };

    private void fadedScreenOpen() {
        thisSheet.setVisibility(VISIBLE);
        fadedScreen.animate().alpha(1.0f).setDuration(duration).setListener(null);
//        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
//                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, heigthF, 0);
        animation.setDuration(duration);
        sheetContainer.startAnimation(animation);
    }


    private void fadedScreenClose(final boolean negative, final Bundle data) {

        fadedScreen.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        thisSheet.setVisibility(GONE);
                        if (allertListener != null) {
                            if (negative) {
                                allertListener.negativeClose();
                            } else {
                                allertListener.proceedChanges(data);
                            }
                        }
                    }
                });
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, 0f, heigthF);
        animation.setDuration(duration);
        sheetContainer.startAnimation(animation);
    }

//    private void setVisib(int vis) {
//        super.setVisibility(vis);
//    }

//    @Override
//    public void setVisibility(int visibility) {
//        switch (visibility) {
//            case VISIBLE :
//                open();
//                break;
//            case GONE :
//                fadedScreenClose(true, null);
//                break;
//            default:
//                super.setVisibility(visibility);
//                break;
//        }
////        if (visibility == VISIBLE) {
////            open();
////        } else {
////            fadedScreenClose(true, null);
////        }
//    }

    private SheetBottomListener listenerForView = new SheetBottomListener() {
        @Override
        public void negativeClose() {
            hide();
//            fadedScreenClose(true, null);
        }

        @Override
        public void proceedChanges(Bundle data) {
            iBase.delAnimatePanel(SheetBottom.this);
            fadedScreenClose(false, data);
        }
    };

    @Override
    public void show(IBase iBase) {
        if (getVisibility() == GONE) {
            open();
            this.iBase = iBase;
            iBase.addAnimatePanel(this);
            if (showTime > 0) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hide();
                    }
                }, showTime);
            }
        }
    }

    @Override
    public void hide() {
        if (getVisibility() == VISIBLE) {
            fadedScreenClose(true, null);
            iBase.delAnimatePanel(this);
        }
    }

    @Override
    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }

    public interface SheetBottomListener {
        void negativeClose();
        void proceedChanges(Bundle data);
    }

    public interface SheetBottomFragment {
        void setSheetBottomListener(SheetBottomListener listener);
    }

    private class SwipeY extends RelativeLayout {
        private VelocityTracker mVelocityTracker;
        private FlingAnimation mFlingYAnimation;
        private SpringAnimation animY;
        private View mSwipeView;
        private float mDownY;
        private float mOffsetY;
        private float minV = 0f;
        private float maxV, halfMax;
        private boolean startMove;
        private SheetBottomListener listener;

        public SwipeY(@NonNull Context context) {
            this(context, null);
        }

        public SwipeY(@NonNull Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public SwipeY(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            mVelocityTracker = VelocityTracker.obtain();
            maxV = 0;
            startMove = false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float tY;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownY = event.getY();
                    if (animY != null) {
                        animY.cancel();
                    }
                    if (mFlingYAnimation != null) {
                        mFlingYAnimation.cancel();
                    }
                    mOffsetY = mSwipeView.getTranslationY();
                    mVelocityTracker.addMovement(event);
                    startMove = true;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    tY = (event.getY() - mDownY + mOffsetY);
                    if (startMove && Math.abs(tY) < 20) return true;
                    startMove = false;
                    if (tY > maxV) {
                        tY = maxV;
                    }
                    if (tY < minV ) {
                        tY = minV;
                    }
                    mSwipeView.setTranslationY(tY);
                    mVelocityTracker.addMovement(event);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mVelocityTracker.computeCurrentVelocity(1000);
                    tY = mSwipeView.getTranslationY();
                    float minS, maxS;
                    if (tY <= maxV && tY >= minV) {
                        if (tY < 0) {
                            minS = minV;
                            maxS = 0f;
                        } else {
                            minS = 0f;
                            maxS = maxV;
                        }
                        mFlingYAnimation = new FlingAnimation(mSwipeView,
                                DynamicAnimation.TRANSLATION_Y)
                                .setFriction(0.5f)
                                .setMinValue(minS)
                                .setMaxValue(maxS)
                                .setStartVelocity(mVelocityTracker.getYVelocity())
                                .addEndListener(endListener);
                        mFlingYAnimation.start();
                    }
                    mVelocityTracker.clear();
                    return true;
            }
            return false;
        }

        DynamicAnimation.OnAnimationEndListener endListener = new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                if (velocity == 0 ) {
                    if (value > 0) {
                        if (value < halfMax) {
                            closer(0f);
                        } else {
                            closer(maxV);
                        }
                    }
                } else {
                    if (value > 0 && listener != null) {
                        listener.negativeClose();
                    }
                }
            }
        };

        public void setSwipeView(View view, SheetBottomListener listener) {
            this.listener = listener;
            mSwipeView = view;
            if (mSwipeView.getTranslationY() != 0) {
                mSwipeView.setTranslationY(0);
            }
            maxV = view.getHeight();
            halfMax = maxV / 2;
        }

        private void closer(final float finalPosition) {
            animY = new SpringAnimation(mSwipeView,
                    new FloatPropertyCompat<View>("TranslationY") {
                        @Override
                        public float getValue(View view) {
                            return view.getTranslationY();
                        }

                        @Override
                        public void setValue(View view, float value) {
                            view.setTranslationY(value);
                        }
                    }, finalPosition);
            animY.getSpring().setStiffness(1000f);
            animY.getSpring().setDampingRatio(0.7f);
            animY.setStartVelocity(0);
            animY.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    if (finalPosition > 0 && listener != null) {
                        listener.negativeClose();
                    }
                }
            });
            animY.start();
        }
    }
}