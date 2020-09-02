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
    private SpringAnimation springAnimation;
    SpringForce springForce;

    public SpringY(View v, float velocity, int repeatTime){
        this.v = v;
        this.velocity = velocity;
        this.repeatTime = repeatTime;
        init();
    }

    private void init() {
        springAnimation = new SpringAnimation(v, DynamicAnimation.Y);
        springForce = new SpringForce();
        springForce.setFinalPosition(v.getY());
        springForce.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
        springForce.setStiffness(SpringForce.STIFFNESS_LOW);
        springAnimation.setSpring(springForce);
        if (repeatTime > 0) {
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
            startAnim();
        }
    };
}
