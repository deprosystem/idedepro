package com.dpcsa.compon.interfaces_classes;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class SpringScale {
    private float velocity;
    private int repeatTime;
    private View v;
    private FloatPropertyCompat<View> scale;
    private SpringAnimation springAnimation;
    private SpringForce springForce;
    private int count;

    public SpringScale(View v, float velocity, int repeatTime){
        this.v = v;
        this.velocity = velocity;
        this.repeatTime = repeatTime;
        init();
    }

    private void init() {
        count = 1;
        scale =
                new FloatPropertyCompat<View>("scale") {
                    @Override
                    public float getValue(View view) {
                        return view.getScaleX();
                    }

                    @Override
                    public void setValue(View view, float value) {
                        view.setScaleX(value);
                        view.setScaleY(value);
                    }
                };
        springAnimation = new SpringAnimation(v, scale);
        springAnimation.setMinimumVisibleChange(
                DynamicAnimation.MIN_VISIBLE_CHANGE_SCALE);
        springForce = new SpringForce();
        springForce.setFinalPosition(v.getScaleX());
        springForce.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
        springForce.setStiffness(SpringForce.STIFFNESS_LOW);
        springAnimation.setSpring(springForce);
        if (repeatTime > 1) {
            springAnimation.addEndListener(
                    new DynamicAnimation.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(DynamicAnimation animation,
                                                   boolean canceled,
                                                   float value, float velocity) {
                            handler.postDelayed(rr, repeatTime);
                        }
                    });
        }
    }

    public void startAnim() {
        springAnimation.setStartVelocity(velocity);
        springAnimation.start();
    }

    Handler handler = new Handler();

    Runnable rr = new Runnable() {
        @Override
        public void run() {
            if (count < repeatTime) {
                count ++;
                startAnim();
            }
        }
    };
}
