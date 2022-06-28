package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.interfaces_classes.OnClickItemRecycler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class DrumPickerRecycler extends RecyclerView implements IComponent, IAlias {
    public Context context = null;
    private int sideLimit;
    private List<String> values = new ArrayList<>(); //Values
    private int visiblePosition = 5;
    private int countValues;
    private int selectedvalueId;
    private String alias;
    private int DEFAULT_textSize = 24;
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
    private boolean loopMode;
    private int sizeValuesVirtual;
    private int iAlign;
    private int minValue, maxValue;

    private LinearLayoutManager layoutManager;
    private Adapter adapter;
    private int textSizeDP;
    private float DENSYTI = getResources().getDisplayMetrics().density;
    private int overallYScroll;
    private int gravH = Gravity.LEFT;
    private int plusSize = 0;
    private int offsetMid;
    private int deltA = 0, deltS = 0;


    public DrumPickerRecycler(Context context) {
        this(context, null, 0);
    }

    public DrumPickerRecycler(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrumPickerRecycler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setAttributes(attrs);
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Drum);
        textSize = (int) a.getDimension(R.styleable.Drum_textSize, DEFAULT_textSize);
        padText = (textSize / 20) * 2;
        valueHeight = padText + textSize;
        halfValueHeight = valueHeight / 2;
        minTextSize = (int) a.getDimension(R.styleable.Drum_minTextSize, -1);
        textSizeDP = (int) (textSize / DENSYTI);
        if (minTextSize == -1) {
            minTextSize = textSize;
            deltS = 0;
        } else {
            deltS = textSize - minTextSize;
        }
        minAlpha = (int) a.getInteger(R.styleable.Drum_minAlpha, -1);
        if (minAlpha < 0) {
            minAlpha = 255;
            deltA = 0;
        } else {
            deltA = 255 - minAlpha;
        }
        alias = a.getString(R.styleable.Drum_alias);
        iAlign = a.getInt(R.styleable.Drum_align, 1);
        switch (iAlign) {
            case 0:
                gravH = Gravity.LEFT;
                break;
            case 1:
                gravH = Gravity.CENTER_HORIZONTAL;
                break;
            case 2:
                gravH = Gravity.RIGHT;
                break;
        }
        int ii = a.getResourceId(R.styleable.Drum_dataArray, 0);
        selectColor = a.getColor(R.styleable.Drum_selectColor, DEFAULT_selectColor);
        otherColor = a.getColor(R.styleable.Drum_otherColor, DEFAULT_otherColor);
        sideLimit = a.getInteger(R.styleable.Drum_sideLimit, 1);
        loopMode = a.getBoolean(R.styleable.Drum_loopMode, false);
        minValue = a.getInt(R.styleable.Drum_beginValue, Integer.MIN_VALUE);
        maxValue = a.getInt(R.styleable.Drum_endValue, Integer.MIN_VALUE);
        plusSize = sideLimit * 2;
        offsetMid = sideLimit * valueHeight + halfValueHeight;
        Log.d("QWERT","deltS="+deltS+" deltA="+deltA+" sideLimit="+sideLimit+" valueHeight="+valueHeight+" offsetMid="+offsetMid);
        visiblePosition = plusSize + 1;
        a.recycle();
        middleHeight = valueHeight * visiblePosition / 2;
        color_b = 255 & selectColor;
        color_g = 255 & (selectColor >> 8);
        color_r = 255 & (selectColor >> 16);
        sizeValuesVirtual = 0;
        overallYScroll = 0;
        layoutManager = new LinearLayoutManager(context);
//        layoutManager = new DrumLayoutManager(context, 2f);
        setLayoutManager(layoutManager);
        adapter = new Adapter(values, listener, valueHeight);
        addOnScrollListener(scrL);
        setAdapter(adapter);
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

    OnScrollListener scrL = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == 0) {
                int pos = overallYScroll / valueHeight;
                int offs = overallYScroll - pos * valueHeight;
                if (offs > halfValueHeight) {
                    pos++;
                }
                layoutManager.scrollToPositionWithOffset(pos, 0);
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            overallYScroll += dy;
            if (deltA != 0 || deltS != 0) {
                int ss =0;
                int cc = recyclerView.getChildCount();
                Log.d("QWERT", "************ CCCC=" + cc);
                int mid = overallYScroll + offsetMid;
                for (int c = 0; c < cc; c++) {
                    View v = recyclerView.getChildAt(c);
                    ViewHolder vh = getChildViewHolder(v);
                    int pos = vh.getAdapterPosition();
                    int posYScroll = pos * valueHeight + halfValueHeight;
                    float delt = (float)(offsetMid - Math.abs(posYScroll - mid)) / offsetMid;
                    Log.d("QWERT","PP="+c+" delt="+delt+" MIN="+(offsetMid - Math.abs(posYScroll - mid))+" ABS="+Math.abs(posYScroll - mid));
                    if (delt < 0) {
                        delt = 0;
                    }
                    TextView name = (TextView) ((ViewGroup) v).getChildAt(0);
                    if (deltS != 0) {
                        ss = (int) (deltS * delt) + minTextSize;
                        name.setTextSize(ss / DENSYTI);
                    }
                    if (deltA != 0) {
                        float aa = (int) (deltA * delt) + minAlpha;
                        name.setAlpha(aa / 255f);
                    }


                }
            }
//            Log.d("QWERT","XXXXXXXX="+overallYScroll);
        }
    };

    private class DrumLayoutManager extends LinearLayoutManager {

        private final float factor;

        public DrumLayoutManager(Context context, float factor) {
            super(context);
            this.factor = factor;
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {

            final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

                @Override
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    return DrumLayoutManager.this.computeScrollVectorForPosition(targetPosition);
                }

                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return super.calculateSpeedPerPixel(displayMetrics) * factor;
                }
            };

            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<String> ls;
        OnClickItemRecycler listener;
        int h;
//        int id = generateViewId();

        public Adapter(List<String> ls, OnClickItemRecycler listener, int h) {
            this.ls = ls;
            this.listener = listener;
            this.h = h;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout ll = new LinearLayout(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, h);
            ll.setLayoutParams(lp);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setGravity(Gravity.CENTER_VERTICAL | gravH);
            TextView tv = new TextView(context);
            LinearLayout.LayoutParams lpT = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(lpT);
            tv.setTextSize(textSizeDP);
            tv.setTextColor(selectColor);
            ll.addView(tv);
            return new PickHolder(ll);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            PickHolder ph = (PickHolder) holder;
            if (position >= sideLimit && position < sideLimit + countValues) {
                ph.name.setText(ls.get(position - sideLimit));
            } else {
                ph.name.setText("");
            }
        }

        @Override
        public int getItemCount() {
            int ss = ls.size();
            if (ss == 0) {
                return 0;
            }
            return ss + plusSize;
        }

        public class PickHolder extends RecyclerView.ViewHolder {
            TextView name;
            public PickHolder(View itemView) {
                super(itemView);
                name = (TextView) ((ViewGroup) itemView).getChildAt(0);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onClick(PickHolder.this, v, getAdapterPosition(), null);
                        }
                    }
                });
            }
        }
    }

    private OnClickItemRecycler listener = new OnClickItemRecycler() {
        @Override
        public void onClick(ViewHolder holder, View view, int position, Record record) {

        }
    };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        getLayoutParams().height = visiblePosition * valueHeight;
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

    @Override
    public void clearData() {

    }

    public void setValues(List<String> l, Integer startPosition) {
        values.clear();
        values.addAll(l);
        countValues = values.size();
        Log.d("QWERT","countValues="+countValues);
        adapter.notifyDataSetChanged();
    }

    public void setValuesVirtual(int sizeValuesVirtual, int startPosition) {
        this.sizeValuesVirtual = sizeValuesVirtual;
        countValues = sizeValuesVirtual;
        selectedvalueId = startPosition;
        invalidate();
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
}
