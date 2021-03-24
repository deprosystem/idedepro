package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.single.Injector;
import com.dpcsa.compon.tools.Constants;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

public class SeekBarRange extends RelativeLayout
        implements IComponent, IAlias {

    private String alias;
    private Object data;

    private Context context;
    private int thumbImg;
    private float DENSITY = getResources().getDisplayMetrics().density;
    private int DEF_BAR_H = (int) (2 * DENSITY);
    private int DEF_TUMB_H = (int) (32 * DENSITY);
    private int DEF_TUMB_COLOR = 0x801B5AE7;
    private int thumbDiam, barHeight, barColor, betweenColor, barDrawable;      // pix
    private int tumbColor;
    private LinearLayout thumb_1, thumb_2, barAll, barBetween, thumb_active;
    private int WRAP = -2, MATCH = -1;
    private boolean barRoundedCorners, singleThumb;
    private int thisXX, thisYY, rootXX, thumbW, thumbH, maxLeft, position_1, position_2, minV, maxV;
    private int thumbWidthHalf;
    private int deltaX, maxBetweenWidth;
    private float startX, startY, deltaValue;
    private int sliderViewInfo, sliderInfoW, sliderInfoHalf, sliderOffset;
    private View sliderInfo;
    private TextView sliderTV, showMinTV, showMaxTV, showMinMaxTV;
    private int valueTh, valueTh_1, valueTh_2, minStart, maxStart;
    private int showMinId, showMaxId, showMinMaxId;
    private View parentRoot, parentRootInPager;
    private ViewPager pager;
    private int intX;
    private boolean mustParamSeekBar;
    private String saveParam;
    private String sendNotif;

    public SeekBarRange(Context context) {
        super(context);
        init(context, null);
    }

    public SeekBarRange(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SeekBarRange(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mustParamSeekBar = true;
        this.context = context;
        barHeight = (int) (2 * DENSITY);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                    0, 0);
            try {
                thumbImg = a.getResourceId(R.styleable.Simple_thumbImg, 0);
                thumbDiam = a.getDimensionPixelOffset(R.styleable.Simple_thumbDiam, 0);
                barHeight = a.getDimensionPixelOffset(R.styleable.Simple_barHeight, DEF_BAR_H);
                barColor = a.getColor(R.styleable.Simple_barColor, 0xff000000);
                tumbColor = a.getColor(R.styleable.Simple_tumbColor, DEF_TUMB_COLOR);
                betweenColor = a.getColor(R.styleable.Simple_betweenColor, 0xff000000);
                barRoundedCorners = a.getBoolean(R.styleable.Simple_barRoundedCorners, false);
                sliderViewInfo = a.getResourceId(R.styleable.Simple_sliderViewInfo, 0);
                barDrawable = a.getResourceId(R.styleable.Simple_barDrawable, 0);
                minV = a.getInteger(R.styleable.Simple_minValueSeek, 0);
                maxV = a.getInteger(R.styleable.Simple_maxValueSeek, 20);
                minStart = a.getInteger(R.styleable.Simple_minStartValue, minV);
                maxStart = a.getInteger(R.styleable.Simple_maxStartValue, maxV);
                singleThumb = a.getBoolean(R.styleable.Simple_singleThumb, false);
                showMaxId = a.getResourceId(R.styleable.Simple_showMaxValue, 0);
                showMinId = a.getResourceId(R.styleable.Simple_showMinValue, 0);
                showMinMaxId = a.getResourceId(R.styleable.Simple_showMinMaxValue, 0);
                sendNotif = a.getString(R.styleable.Simple_sendNotif);
                saveParam = a.getString(R.styleable.Simple_saveParam);
                validMinMax(minV, maxV);
                validStartMinMax(minStart, maxStart);
            } finally {
                a.recycle();
            }
            barAll = bar_1();
            addView(barAll);
            if ( ! singleThumb) {
                barBetween = bar_2();
                addView(barBetween);
            }
            thumb_1 = thumb(false);
            addView(thumb_1);
            if ( ! singleThumb) {
                thumb_2 = thumb(true);
                addView(thumb_2);
            }
            if (sendNotif != null && sendNotif.length() == 0) {
                sendNotif = null;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mustParamSeekBar) {
            mustParamSeekBar = false;
            setParamSeekBar();
        }
    }

    private void setParamSeekBar() {
        thumbW = thumb_1.getWidth();
        thumbWidthHalf = thumbW / 2;
        thumbH = thumb_1.getHeight();
        barAll.setPadding(thumbWidthHalf, 0, thumbWidthHalf, 0);
        if ( ! singleThumb) {
            barBetween.setPadding(thumbW, 0, thumbW, 0);
        }
        parentRoot = getParentRoot(getParent());
        if (parentRootInPager != null) {
            int[] loc = new int[2];
            parentRootInPager.getLocationOnScreen(loc);
            rootXX = loc[0];
        }
        if (sliderViewInfo != 0) {
            sliderInfo = parentRoot.findViewById(sliderViewInfo);
            if (sliderInfo != null) {
                sliderInfo.setVisibility(GONE);
                if (sliderInfo instanceof TextView) {
                    sliderTV = (TextView) sliderInfo;
                }
                sliderInfoW = sliderInfo.getWidth();
                sliderInfoHalf = sliderInfoW / 2;
            }
        }
        if (showMaxId != 0) {
            showMaxTV = parentRoot.findViewById(showMaxId);
        }
        if (showMinId != 0) {
            showMinTV = parentRoot.findViewById(showMinId);
        }
        if (showMinMaxId != 0) {
            showMinMaxTV = parentRoot.findViewById(showMinMaxId);
        }

        int[] location = new int[2];
        getLocationOnScreen(location);
        thisXX = location[0] - rootXX;
        sliderOffset = thisXX - thumbWidthHalf - sliderInfoHalf;
        thisYY = location[1];
        int w = getWidth();
        maxLeft = w - thumbW;
        maxBetweenWidth = maxLeft + thumbWidthHalf;
        deltaValue = maxLeft / (maxV - minV);
        position_1 = (int)thumb_1.getX();
        if ( ! singleThumb) {
            position_2 = (int)thumb_2.getX();
        }
        setStartValue();
        showValue();
    }

    private void sendNotification(String notif) {
        Intent intentBroad = new Intent(sendNotif);
        intentBroad.putExtra(Constants.DATA_STR, notif);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentBroad);
    }

    private void validStartMinMax(int min, int max) {
        if ( ! singleThumb) {
            if (min > max) {
                minStart = max;
                maxStart = min;
            } else {
                minStart = min;
                maxStart = max;
            }
            if (maxStart < minV) {
                maxStart = minV;
            } else if (maxStart > maxV) {
                maxStart = maxV;
            }
            valueTh_2 = maxStart;
        }
        if (minStart < minV) {
            minStart = minV;
        } else if (minStart > maxV) {
            minStart = maxV;
        }
        valueTh_1 = minStart;
    }

    private void validMinMax(int min, int max) {
        if (min > max) {
            minV = max;
            maxV = min;
        } else {
            minV = min;
            maxV = max;
        }
    }

    private ShapeDrawable shapeRounded() {
        float r = barHeight / 2f;
        float[] outR = new float[]{r, r, r, r, r, r, r, r};
        ShapeDrawable shape = new ShapeDrawable(new RoundRectShape(outR, null, null));
        shape.getPaint().setColor(barColor);
        return shape;
    }

    private LinearLayout thumb(boolean right) {
        LinearLayout ll = new LinearLayout(context);
        int w = WRAP;
        int h = WRAP;
        if (thumbDiam != 0) {
            w = thumbDiam;
            h = thumbDiam;
        } else {
            if (thumbImg == 0) {
                w = h = DEF_TUMB_H;
            }
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
        if (right) {
            lp.addRule(ALIGN_PARENT_RIGHT);
        }
        ll.setLayoutParams(lp);
        if (thumbImg != 0) {
            ll.setBackgroundResource(thumbImg);
        } else {
            ll.setBackground(formTumb(DEF_TUMB_H / 2f));
        }
        return ll;
    }

    private ShapeDrawable formTumb(float r) {
        float[] outR = new float[]{r, r, r, r, r, r, r, r};
        ShapeDrawable shape = new ShapeDrawable(new RoundRectShape(outR, null, null));
        shape.getPaint().setColor(tumbColor);
        return shape;
    }

    private LinearLayout bar_1() {
        LinearLayout ll = new LinearLayout(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(MATCH, barHeight);
        lp.addRule(CENTER_VERTICAL);
        ll.setLayoutParams(lp);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout lw = new LinearLayout(context);
        LayoutParams lpw = new LayoutParams(MATCH, MATCH);
        lw.setLayoutParams(lpw);
        ll.addView(lw);
        if (barDrawable != 0) {
            lw.setBackgroundResource(barDrawable);
        } else {
            if (barRoundedCorners) {
                lw.setBackground(shapeRounded());
            } else {
                lw.setBackgroundColor(barColor);
            }
        }
        return ll;
    }

    private LinearLayout bar_2() {
        LinearLayout ll = new LinearLayout(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(MATCH, barHeight);
        lp.addRule(CENTER_VERTICAL);
        ll.setLayoutParams(lp);
        ll.setPadding(thumbWidthHalf, 0, thumbWidthHalf, 0);
        LinearLayout lw = new LinearLayout(context);
        LayoutParams lpw = null;
        lpw = new LayoutParams(MATCH, MATCH);
        lw.setLayoutParams(lpw);
        ll.addView(lw);
        lw.setBackgroundColor(betweenColor);
        return ll;
    }

//    Handler handler = new Handler();

    Runnable delayVisible = new Runnable() {
        @Override
        public void run() {
            sliderInfo.setVisibility(VISIBLE);
        }
    };

    private void setStartValue() {
        int dd = (int)((deltaValue + 0.5) / 2);
        int rr = (int)((minStart - minV) * deltaValue) + dd;
        thumb_1.setX(rr);
        position_1 = rr;
        if ( ! singleThumb) {
            rr = (int) ((maxStart - minV) * deltaValue) + dd;
            thumb_2.setX(rr);
            position_2 = rr;
            if (position_1 < position_2) {
                barBetween.setPadding(position_1 + thumbWidthHalf, 0, maxBetweenWidth - position_2, 0);
            } else {
                barBetween.setPadding(position_2 + thumbWidthHalf, 0, maxBetweenWidth - position_1, 0);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int intY;
        float X;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (pager != null && pager instanceof ComponViewPager) {
                    ((ComponViewPager) pager).setSwipeEnabled(false);
                }
                startX = event.getX();
                startY = event.getY();
                intX = (int) startX;
                intY = (int) startY;
                thumb_active = getActiveThumb(intX + thisXX, intY + thisYY);
                if (thumb_active != null) {
                    if (sliderInfo != null) {
                        sliderInfo.setVisibility(INVISIBLE);
                        sliderInfo.setVisibility(VISIBLE);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (thumb_active != null) {
                    X = event.getX();
                    intX = (int) X - thumbWidthHalf;
                    if (intX < 0) {
                        intX = 0;
                    } else if (intX > maxLeft) {
                        intX = maxLeft;
                    }
                    valueTh = (int) (intX / deltaValue) + minV;
                    thumb_active.setX(intX);
                    if (thumb_active == thumb_1) {
                        position_1 = intX;
                        valueTh_1 = valueTh;
                    } else {
                        position_2 = intX;
                        valueTh_2 = valueTh;
                    }
                    showValue();
                    if (sliderInfo != null) {
                        int leftSlider = intX + sliderOffset;
                        sliderInfo.setX(leftSlider);
                        if (sliderTV != null) {
                            sliderTV.setText(String.valueOf(valueTh));
                        }
                    }

                    if ( ! singleThumb) {
                        if (position_1 < position_2) {
                            barBetween.setPadding(position_1 + thumbWidthHalf, 0, maxBetweenWidth - position_2, 0);
                        } else {
                            barBetween.setPadding(position_2 + thumbWidthHalf, 0, maxBetweenWidth - position_1, 0);
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (sliderInfo != null) {
                    sliderInfo.setVisibility(GONE);
                }
                if (pager != null && pager instanceof ComponViewPager) {
                    ((ComponViewPager) pager).setSwipeEnabled(true);
                }
                thumb_active = null;
                break;
        }
        return true;
    }

    private void showValue() {
        int maxV, minV;
        String str;
        if (valueTh_2 < valueTh_1) {
            maxV = valueTh_1;
            minV = valueTh_2;
        } else {
            maxV = valueTh_2;
            minV = valueTh_1;
        }
        if (saveParam != null && saveParam.length() > 0) {
            if (singleThumb) {
                str = String.valueOf(minV) + " - " + String.valueOf(maxV);
            } else {
                str = String.valueOf(minV);
            }
            Injector.getComponGlob().setParamValue(saveParam, str);
        }
        if (singleThumb) {
            str = String.valueOf(minV);
        } else {
            if (minV == maxV) {
                str = String.valueOf(minV);
            } else {
                str = String.valueOf(minV) + " - " + String.valueOf(maxV);
            }
        }
        if (showMinMaxTV != null) {
            if (showMinMaxTV instanceof TextViewNumberGrammar) {
                ((TextViewNumberGrammar) showMinMaxTV).setData(str);
            } else {
                showMinMaxTV.setText(str);
            }
        } else {
            if (!singleThumb) {
                if (showMaxTV != null) {
                    showMaxTV.setText(String.valueOf(maxV));
                }
            }
            if (showMinTV != null) {
                showMinTV.setText(String.valueOf(minV));
            }
        }
        if (sendNotif != null) {
            sendNotification(str);
        }
    }

    private LinearLayout getActiveThumb(int rawX, int rawY) {
        LinearLayout res = null;
        int x;
        int[] location = new int[2];
        thumb_1.getLocationOnScreen(location);
        x = location[0];
        int delt_1 = x - rawX;
        if (delt_1 < 0) {
            delt_1 = - delt_1;
        }
        location = new int[2];
        thumb_2.getLocationOnScreen(location);
        x = location[0];
        int delt_2 = x - rawX;
        if (delt_2 < 0) {
            delt_2 = - delt_2;
        }
        if (delt_1 < delt_2) {
            res = thumb_1;
        } else {
            res = thumb_2;
        }
        return res;
    }

    private View getParentRoot(ViewParent view) {
        ViewParent viewRoot = view;
        ViewParent view2 = viewRoot;
        ViewParent v = viewRoot.getParent();
        while (v != null) {
            view2 = viewRoot;
            viewRoot = v;
            if (viewRoot instanceof ViewPager) {
                pager = (ViewPager) viewRoot;
                parentRootInPager = (View) view2;
            }
            v = viewRoot.getParent();
        }
        return  (View) view2;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void setData(Object data) {
        Record rec;
        int min, max = maxStart;
        int minSeek, maxSeek;
        Field f;
        if (data instanceof Record) {
            rec = (Record) data;
            int count = rec.size();
            if (count > 0) {
                f = rec.get(0);
                min = rec.fieldToInt(f);
                if (count > 1) {
                    f = rec.get(1);
                    max = rec.fieldToInt(f);

                    if (count > 3) {
                        f = rec.get(2);
                        minSeek = rec.fieldToInt(f);
                        f = rec.get(3);
                        maxSeek = rec.fieldToInt(f);
                        validMinMax(minSeek, maxSeek);
                    }
                }
                validStartMinMax(min, max);
            }
        } else {
            if (data instanceof Long) {
                long lon = (Long) data;
                min = (int) lon;
                validStartMinMax(min, min);
                setStartValue();
                showValue();
            } else if (data instanceof Integer) {
                min = (Integer) data;
                validStartMinMax(min, min);
                setStartValue();
                showValue();
            } else if (data instanceof String) {
                min = 0;
                try {
                    min = Integer.valueOf((String) data);
                    validStartMinMax(min, min);
                    setStartValue();
                    showValue();
                } catch (NumberFormatException e) {}
            }
        }
    }

    @Override
    public Object getData() {
        if (singleThumb) {
            return valueTh_1;
        } else {
            return valueTh_1 + "," + valueTh_2;
        }
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        return null;
    }
}
