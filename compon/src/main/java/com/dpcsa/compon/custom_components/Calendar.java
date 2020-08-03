package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dpcsa.compon.R;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class Calendar extends RelativeLayout {

    Context context;
    CalendarView calendarView;
    ArrowRight arrowRight;
    ArrowLeft arrowLeft;
    int HeightTitle = 40;
    float DENSITY = getResources().getDisplayMetrics().density;
    int HeightTitleDens = (int) (HeightTitle * DENSITY);
    int arrowH = (int) (24 * DENSITY);
    int arrowPadding = (int) (10 * DENSITY);
    private CalendarClick cClick;
    TextView viewDate;
    View thisView, rootView;
    public int viewDateId;
    int maxLeftMonth, maxRightMonth;
    public long newDateC;
    public int weekday;
    String dateFormat = "dd.MM.yy";
    SimpleDateFormat sdFormat;

    public Calendar(Context context) {
        this(context, null);
    }

    public Calendar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Calendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        thisView = this;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        maxLeftMonth = a.getInt(R.styleable.CalendarView_countMonthLeft, 0);
        maxRightMonth = a.getInt(R.styleable.CalendarView_countMonthRight, 0);
        String df = a.getString(R.styleable.CalendarView_formatDate);
        if (df != null) {
            sdFormat = new SimpleDateFormat(df);
        } else {
            sdFormat = new SimpleDateFormat(dateFormat);
        }
        viewDateId = a.getResourceId(R.styleable.CalendarView_viewDateId, 0);
        a.recycle();

        int calendarId = generateViewId();
        calendarView = new CalendarView(context, attrs);
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        calendarView.setId(calendarId);
        calendarView.setVisibility(VISIBLE);
        calendarView.setLayoutParams(lp);
        calendarView.setCountMonth(maxLeftMonth + maxRightMonth + 1, maxLeftMonth);
        addView(calendarView);
        calendarView.setCalendarCallBack(callBack);
        calendarView.setAdapter(null);

        RelativeLayout botR = new RelativeLayout(context);
        LayoutParams lBotR = new LayoutParams(HeightTitleDens * 2, HeightTitleDens);
        lBotR.addRule(ALIGN_PARENT_RIGHT);
        botR.setOnClickListener(clickR);
        botR.setLayoutParams(lBotR);
        addView(botR);
        arrowRight = new ArrowRight(context);
        LayoutParams lArr = new LayoutParams(arrowH, arrowH);
        lArr.addRule(ALIGN_PARENT_RIGHT);
        lArr.addRule(CENTER_VERTICAL);
        arrowRight.setLayoutParams(lArr);
        botR.addView(arrowRight);

        RelativeLayout botL = new RelativeLayout(context);
        LayoutParams lBotL = new LayoutParams(HeightTitleDens * 2, HeightTitleDens);
        botL.setLayoutParams(lBotL);
        botL.setOnClickListener(clickL);
        addView(botL);
        arrowLeft = new ArrowLeft(context);
        LayoutParams lArrL = new LayoutParams(arrowH, arrowH);
        lArrL.addRule(CENTER_VERTICAL);
        arrowLeft.setLayoutParams(lArrL);
        botL.addView(arrowLeft);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        rootView = getRoot();
        if (viewDateId != 0) {
            viewDate = (TextView) rootView.findViewById(viewDateId);
            viewDate.setText(sdFormat.format(newDateC));
        }
    }

    private View getRoot() {
        ViewParent viewRoot = this;
        ViewParent view2 = viewRoot;
        ViewParent v = viewRoot.getParent();
        while (v != null) {
            view2 = viewRoot;
            viewRoot = v;
            v = viewRoot.getParent();
        }
        return (View) view2;
    }

    public void setListenerOk(CalendarClick cClick) {
        this.cClick = cClick;
        if (cClick != null) {
            cClick.onChangeDate(newDateC, weekday);
        }
    }

    OnClickListener clickR = new OnClickListener() {
        @Override
        public void onClick(View v) {
            calendarView.monthPlus(1);
        }
    };

    OnClickListener clickL = new OnClickListener() {
        @Override
        public void onClick(View v) {
            calendarView.monthPlus(-1);
        }
    };

    CalendarView.CalendarCallBack callBack = new CalendarView.CalendarCallBack() {
        @Override
        public void onChangeDay(View v, int year, int month, int number, int weekday) {
            newDateC = new GregorianCalendar(year, month, number).getTime().getTime();
            if (viewDate != null) {
                viewDate.setText(sdFormat.format(newDateC));
            }
            if (cClick != null) {
                cClick.onChangeDate(newDateC, weekday);
            }
        }

        @Override
        public void onChangeMonth(int year, int month, int number) {

        }

        @Override
        public void setCurrentDate(int year, int month, int number, int weekDay) {
            newDateC = new GregorianCalendar(year, month, number).getTime().getTime();
            weekday = weekDay;
            if (cClick != null) {
                cClick.onChangeDate(newDateC, weekDay);
            }
        }
    };

    private class ArrowRight extends LinearLayout{
        int colorLine = 0xFF000000;
        float DENSITY = getResources().getDisplayMetrics().density;
        float strokeWidth = DENSITY * 2;
        protected int canvasW, canvasH;

        public ArrowRight(Context context) {
            this(context, null);
        }

        public ArrowRight(Context context, @Nullable AttributeSet attrs) {
            this(context, null, 0);
        }

        public ArrowRight(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setBackgroundColor(0x00000000);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            canvasW = w;
            canvasH = h;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(colorLine);
            paint.setStrokeWidth(strokeWidth);
            canvas.drawLine(0, canvasH, canvasW / 2, canvasH / 2, paint);
            canvas.drawLine(canvasW / 2, canvasH / 2, 0, 0, paint);
        }
    }

    private class ArrowLeft extends LinearLayout{

        int colorLine = 0xFF000000;
        float DENSITY = getResources().getDisplayMetrics().density;
        float strokeWidth = DENSITY * 2;
        protected int canvasW, canvasH;

        public ArrowLeft(Context context) {
            this(context, null);
        }

        public ArrowLeft(Context context, @Nullable AttributeSet attrs) {
            this(context, null, 0);
        }

        public ArrowLeft(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setBackgroundColor(0x00000000);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            canvasW = w;
            canvasH = h;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(colorLine);
            paint.setStrokeWidth(strokeWidth);
            canvas.drawLine(canvasW, canvasH, canvasW / 2, canvasH / 2, paint);
            canvas.drawLine(canvasW / 2, canvasH / 2, canvasW, 0, paint);
        }
    }

    public interface CalendarClick {
        void onChangeDate(long newDate, int weekday);
    }
}
