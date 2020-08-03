package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.json_simple.Field;

import java.util.ArrayList;
import java.util.List;

public class DrumPicker extends View  implements IComponent, IAlias {
    public Context context = null;
    private int sideLimit;
    private List<String> values = new ArrayList<>(); //Values
    private int downY, deltaY, getY; //Touch down coordinates
    private int scroll, maxScroll, currentScroll;
    private int visiblePosition = 5;
    private int countValues;
    private int selectedvalueId;
    private int canvasW = 0; //Current weight of canvas
    private int canvasH = 0; //Current height of canvas
    private DrumScrollListener drumScrollListener;
    private String alias;
    private int DEFAULT_textSize = 24;
    private int DEFAULT_minTextSize = 100;
    private int DEFAULT_minAlpha = 50;
    private int DEFAULT_selectColor = 0xFF000000;
    private int DEFAULT_otherColor = 0xFFdddddd;
    private int valueHeight, halfValueHeight, middleHeight;
    private int padText = 0;
    private int textSize;
    private int minAlpha;
    private int minTextSize = 1000;
    private int selectColor, otherColor;
    private int color_r = 0, color_g = 0, color_b = 0;
    private int velocity, minVelocity = 10, oldY, baseVelocity;
    private boolean loopMode;
    private boolean isScroll;
    private int sizeValuesVirtual;
    private int hRect, iAlign, xX, padL, padR;
    private Rect rect;
    private Paint.Align align;
//    private List<String> data;
    private int minValue, maxValue;


    public DrumPicker(Context context) {
        this(context, null, 0);
    }

    public DrumPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrumPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setAttributes(attrs);
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Drum);
//        valueHeight = (int) a.getDimension(R.styleable.Drum_itemHeight, DEFAULT_valueHeight);
        textSize = (int) a.getDimension(R.styleable.Drum_textSize, DEFAULT_textSize);
        padText = (textSize / 20) * 2;
        valueHeight = padText + textSize;
        minTextSize = (int) a.getDimension(R.styleable.Drum_minTextSize, -1);
        if (minTextSize == -1) {
            minTextSize = textSize;
        }
        minAlpha = (int) a.getInteger(R.styleable.Drum_minAlpha, DEFAULT_minAlpha);
        if (minAlpha < 0) {
            minAlpha = DEFAULT_minAlpha;
        }
        alias = a.getString(R.styleable.Drum_alias);
        iAlign = a.getInt(R.styleable.Drum_align, 1);
        int ii = a.getResourceId(R.styleable.Drum_dataArray, 0);
        selectColor = a.getColor(R.styleable.Drum_selectColor, DEFAULT_selectColor);
        otherColor = a.getColor(R.styleable.Drum_otherColor, DEFAULT_otherColor);
        sideLimit = a.getInteger(R.styleable.Drum_sideLimit, 1);
        loopMode = a.getBoolean(R.styleable.Drum_loopMode, false);
        minValue = a.getInt(R.styleable.Drum_beginValue, Integer.MIN_VALUE);
        maxValue = a.getInt(R.styleable.Drum_endValue, Integer.MIN_VALUE);
        visiblePosition = sideLimit * 2 + 1;
        a.recycle();
        halfValueHeight = valueHeight / 2;
        middleHeight = valueHeight * visiblePosition / 2;
        color_b = 255 & selectColor;
        color_g = 255 & (selectColor >> 8);
        color_r = 255 & (selectColor >> 16);
        currentScroll = 0;
        sizeValuesVirtual = 0;
        hRect = (sideLimit * 2 + 1) * valueHeight + padText;

        if (ii != 0) {
            List<String> data;
            data = new ArrayList<>();
            String[] ss = context.getResources().getStringArray(ii);
            if (ss != null) {
                for (String st : ss) {
                    data.add(st);
                }
                setValues(data, 0);
            }
        } else {
            if (minValue != Integer.MIN_VALUE && maxValue != Integer.MIN_VALUE && minValue < maxValue && (maxValue - minValue) < 100) {
                List<String> data;
                data = new ArrayList<>();
                for (int i = minValue; i < maxValue + 1; i++) {
                    data.add(String.valueOf(i));
                }
                setValues(data, 0);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        canvasW = w;
        canvasH = h;
        rect = new Rect(0, 0, w, hRect);
        padL = getPaddingLeft();
        padR = getPaddingRight();
        switch (iAlign) {
            case 0:
                xX = padL;
                align = Paint.Align.LEFT;
                break;
            case 1:
                xX = w / 2;
                align = Paint.Align.CENTER;
                break;
            case 2:
                xX = w - padR;
                align = Paint.Align.RIGHT;
                break;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) event.getY();
                velocity = 0;
                oldY = 0;
                if (isScroll) {
                    isScroll = false;
                    currentScroll = scroll;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                getY = (int) event.getY();
                deltaY = downY - getY;
                velocity = oldY - getY;
                oldY = getY;
                scroll = currentScroll + deltaY;
                setScroll();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isScroll = true;
                currentScroll = scroll;
                if (downY == getY) {
                    int positionEnd = getY / valueHeight;
                    int firstPosition = scroll / valueHeight - sideLimit - 1;
                    new Enderer((firstPosition + positionEnd + 1) * valueHeight, 200, 30).run();
                } else {
                    if (Math.abs(velocity) < minVelocity) {
                        setScrollPosition();
                    } else {
                        baseVelocity = velocity;
                        velocity = velocity * 2;
                        scrollEnderer.run();
                    }
                }
                break;
        }
        return true;
    }

    private void setScroll() {
        if (loopMode) {
            if (scroll < 0) {
                scroll = scroll + maxScroll;
            }
            if (scroll > maxScroll) {
                scroll = scroll - maxScroll;
            }
        } else {
            if (scroll < 0) scroll = 0;
            if (scroll > maxScroll) scroll = maxScroll;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipRect(rect);
        int iRelative, position;
        int deltaH = middleHeight + halfValueHeight;
        int firstPosition = scroll / valueHeight - sideLimit - 1;
        int lastPosition = firstPosition + visiblePosition + 1;
        int offsetScroll = scroll % valueHeight;
        if (loopMode) {
            if (lastPosition > countValues + 2) {
                lastPosition = countValues + 2;
            }
        } else {
            if (lastPosition > countValues) {
                lastPosition = countValues;
            }
        }
        if (drumScrollListener != null) {
            drumScrollListener.onChangeScroll(scroll, maxScroll);
        }
        for (int i = firstPosition ; i < lastPosition + 1; i++) {
            if (i < 0) {
                if (loopMode) {
                    position = i + countValues;
                } else {
                    continue;
                }
            } else {
                if (loopMode &&
                        i >= countValues) {
                    position = i - countValues;
                } else {
                    position = i;
                }
            }
            try {
                iRelative = i - firstPosition;
                Paint paint = new Paint();
                paint.setColor(otherColor);
                int alpha = 0;
                int delt = middleHeight - Math.abs(iRelative * valueHeight - deltaH - offsetScroll);
                if (delt < 0) {
                    delt = 0;
                }
                if (minAlpha < 255) {
                    alpha = minAlpha + (255 - minAlpha) * delt / middleHeight;
                    paint.setColor(Color.argb(alpha, color_r, color_g, color_b));
                } else {
                    if ((iRelative == sideLimit + 1
                            && offsetScroll < halfValueHeight)
                            || (iRelative == sideLimit + 2
                            && offsetScroll >= halfValueHeight)) {
                        paint.setColor(selectColor);
                    }
                }
                if (minTextSize < textSize) {
                    paint.setTextSize(minTextSize + (textSize - minTextSize) * delt / middleHeight);
                } else {
                    paint.setTextSize(textSize);
                }
                paint.setTextAlign(align);
                paint.setAntiAlias(true);
                String txt;
                if (sizeValuesVirtual > 0 && drumScrollListener != null) {
                    txt = drumScrollListener.getValuesVirtual(position);
                } else {
                    txt = values.get(position);
                }
                canvas.drawText(txt, xX,
                        (valueHeight * iRelative - offsetScroll) - padText - 5,
                        paint);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    android.os.Handler handler = new android.os.Handler();

    Runnable scrollEnderer = new Runnable() {
        @Override
        public void run() {
            setScroll();
            if (scroll < 0
                    || scroll > maxScroll
                    || Math.abs(velocity) < minVelocity) {
                setScrollPosition();
            } else {
                if (velocity < baseVelocity) {
                    scroll += velocity;
                } else {
                    scroll += baseVelocity;
                }
                invalidate();
                velocity = velocity * 80 / 100;
                handler.postDelayed(scrollEnderer, 25);
            }
        }
    };

    private void setScrollPosition() {
        Enderer enderer;
        setScroll();
        int y = scroll / valueHeight;
        int offsetScroll = scroll % valueHeight;
        if (offsetScroll > halfValueHeight) {
            enderer = new Enderer((y + 1) * valueHeight, 150, 30);
        } else {
            enderer = new Enderer(y * valueHeight, 150, 30);
        }
        enderer.run();
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void setData(Object dataO) {
        if (dataO instanceof List) {
            List<String> data = new ArrayList<>();
            for(Field ff : (List<Field>) dataO) {
                data.add((String) ff.value);
            }
            setValues(data, 0);
        }
    }

    @Override
    public Object getData() {
        return getSelectTime();
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        return null;
    }

    private class Enderer implements Runnable {
        private int endY, time, stepT;
        private int beginY, stepY;
        private int t;
        public Enderer(int end, int time, int deltaT) {
            endY = end;
            this.time = time;
            stepT = deltaT;
            beginY = scroll;
            stepY = endY - beginY;
            t = 0;
        }
        @Override
        public void run() {
            t += stepT;
            if (t < time) {
                scroll = beginY + stepY * t / time;
                invalidate();
                handler.postDelayed(this, stepT);
            } else {
                scroll = endY;
                currentScroll = scroll;
                if (drumScrollListener != null) {
                    selectedvalueId = scroll / valueHeight;
                    drumScrollListener.onChangeSelect(selectedvalueId);
                }
                invalidate();
            }
        }
    };

    public void setValues(List<String> l, Integer startPosition) {
        values.clear();
        values.addAll(l);
        countValues = values.size();
        maxScroll = valueHeight * (countValues - 1);
        scroll = startPosition * valueHeight;
        currentScroll = scroll;
        selectedvalueId = startPosition;
        invalidate();
    }

    public void setValuesVirtual(int sizeValuesVirtual, int startPosition) {
        this.sizeValuesVirtual = sizeValuesVirtual;
        countValues = sizeValuesVirtual;
        maxScroll = valueHeight * (countValues - 1);
        scroll = startPosition * valueHeight;
        currentScroll = scroll;
        selectedvalueId = startPosition;
        invalidate();
    }

    public void setScrollListener(DrumScrollListener ts) {
        drumScrollListener = ts;
    }

    public int getSelectId() {
        return selectedvalueId;
    }

    public String getSelectTime() {
        if (countValues > 0) {
            return values.get(getSelectId());
        } else {
            return null;
        }
    }

    public interface DrumScrollListener {
        void onChangeScroll(int offset, int max);
        void onChangeSelect(int position);
        String getValuesVirtual(int position);
    }
}
