package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;

import androidx.annotation.Nullable;

public class Ratings extends LinearLayout implements IComponent {
    private Context context;
    private int amountStars = 3, colorDot = 0xffff0000;
    float DENSITY = getResources().getDisplayMetrics().density;
    float DEF_WIDTH_STAR = (20 * DENSITY); //dp
    int widthStar;
    int starId, starFilledId;


    public Ratings(Context context) {
        super(context);
        init(context, null);;
    }

    public Ratings(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);;
    }

    public Ratings(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);;
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                    0, 0);
            try {
                amountStars = a.getInteger(R.styleable.Simple_amountStars, 5);
                widthStar = (int) a.getDimension(R.styleable.Simple_widthStar, DEF_WIDTH_STAR);
                starId = a.getResourceId(R.styleable.Simple_star, 0);
                starFilledId = a.getResourceId(R.styleable.Simple_starFilled, 0);
            } finally {
                a.recycle();
            }
        }
    }


    @Override
    public void setData(Object data) {
        int count = 0;
        if (data instanceof Integer) {
            count = (int) data;
        } else if (data instanceof Long) {
            long ll = (long) data;
            count = (int) ll;
        }
        for (int i = 0; i < amountStars; i++) {
            ImageView v = new ImageView(context);
            LayoutParams lpV = new LayoutParams(widthStar, widthStar);
            v.setLayoutParams(lpV);
            if (i < count) {
                v.setBackgroundResource(starFilledId);
            } else {
                v.setBackgroundResource(starId);
            }
            addView(v);
        }
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        return null;
    }

    @Override
    public void clearData() {

    }
}
