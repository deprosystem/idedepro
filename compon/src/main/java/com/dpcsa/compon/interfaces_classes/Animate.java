package com.dpcsa.compon.interfaces_classes;

import android.view.View;

public class Animate {
    public enum TYPE {SET, ALPHA, SCALE, TRANSL, ROTATE};
    public TYPE type;
    public int viewId;
    public Animate[] setAnimate;
    public float par1, par2;
    public int duration;
    public boolean onePar;

    public Animate(int viewId, TYPE type, float par2, int duration) {
        this.type = type;
        this.viewId = viewId;
        this.duration = duration;
        this.par2 = par2;
        onePar = true;
    }

    public Animate(int viewId, TYPE type, float par1, float par2, int duration) {
        this.type = type;
        this.viewId = viewId;
        this.duration = duration;
        this.par1 = par1;
        this.par2 = par2;
        onePar = true;
    }

    public Animate(TYPE type, Animate ... animates) {
        this.type = type;
        setAnimate = animates;
    }
}
