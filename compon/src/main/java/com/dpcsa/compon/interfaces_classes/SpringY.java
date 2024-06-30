package com.dpcsa.compon.interfaces_classes;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class SpringY {
    private float velocity;
    private int repeatTime;
    private View v;
    private float startPos, startValue;
    private SpringAnimation springAnimation;
    private int count;
    SpringForce springForce;

    public SpringY(View v, float velocity, int repeatTime){
        this.v = v;
        this.velocity = velocity;
        this.startValue = - 150;
        this.repeatTime = repeatTime;
        init();
    }

    public SpringY(View v, float startValue, float velocity, int repeatTime){
        this.v = v;
        this.velocity = velocity;
        this.startValue = startValue;
        this.repeatTime = repeatTime;
        init();
    }

    private void init() {
        count = 0;
        springAnimation = new SpringAnimation(v, DynamicAnimation.Y);
        springForce = new SpringForce();
        startPos = v.getY();
        springForce.setFinalPosition(startPos);
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
        springAnimation.setStartValue(startPos + startValue);
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
