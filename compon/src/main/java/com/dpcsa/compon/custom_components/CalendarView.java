package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Date;

import com.dpcsa.compon.R;

public class CalendarView extends LinearLayout {
    private Context context;
    public ViewPager viewPager;
    private CalendarAdapter calendarAdapter;
    private int beginDay, endDay, countDay;
    private Date Current_Date;
    private int Current_Month;
    private int Current_Year;
    private int Today;
    public static int[] Days_in_Month = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int baseMonth = -1, basePosition = 1200;
    private int baseViewYear, baseViewMonth;
    private int positionMonth, positionYear;
    private int maxArrayDay = 42;
    private InformDay[] informDay;
    private float DENSITY = getResources().getDisplayMetrics().density;
    private int TITLE_HEIGHT_DEFAULT = (int) (40 * DENSITY);
    private int CELL_HEIGHT_DEFAULT = (int) (32 * DENSITY);
    private int MATCH_PARENT = LayoutParams.MATCH_PARENT;
    private int MAX_MONTH_PAGER = Integer.MAX_VALUE;
    private int TITLE_HEIGHT = TITLE_HEIGHT_DEFAULT;
    private int CELL_HEIGHT = CELL_HEIGHT_DEFAULT;
    private int BORDER = 0;
    private int BORDER_COLOR = Color.BLACK;
    private int OffscreenPageLimit = 2;
    private boolean startPager;
    private int calendarHeigth;
    private int setDayYearMont, setDayPosition, setDayCount, setDayDay,
            setDayYear, setDayMont;
    private boolean setDayIsPager;
    private int colorNoDay = 0x55888888, colorOval, colorWork, colorNoWork, colorToDay,
            colorOvalToDay, colorTitle, colorTitleBackground, colorSelect, colorWeekday;
    private CalendarCallBack calendarCallBack;
    private int WORK_LAST = 0, NEXT = 1, WORK_ALL = 2;
    private int typeWorkDay;

    public CalendarView(Context context)   {
        this(context, null, 0);
    }
    public CalendarView(Context context, AttributeSet attrs)  {
        this(context, attrs, 0);
    }

    public void setCalendarCallBack(CalendarCallBack callBack) {
        calendarCallBack = callBack;
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.setOrientation(VERTICAL);
        setAttributes(attrs);
    }

    private void setAttributes(AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        TITLE_HEIGHT = (int)a.getDimension(R.styleable.CalendarView_heightTitle, TITLE_HEIGHT_DEFAULT);
        CELL_HEIGHT = (int)a.getDimension(R.styleable.CalendarView_heightCell, CELL_HEIGHT_DEFAULT);
        BORDER = (int)a.getDimension(R.styleable.CalendarView_border, 0);
        int i = a.getInt(R.styleable.CalendarView_beginningWeek,0);
        typeWorkDay = a.getInt(R.styleable.CalendarView_workDay,2);
        BORDER_COLOR = a.getColor(R.styleable.CalendarView_borderColorGrid, 0xFF000000);
        colorWork = a.getColor(R.styleable.CalendarView_colorWorkDay, 0xFF000000);
        colorNoWork = a.getColor(R.styleable.CalendarView_colorNoWorkDay, 0xFF888888);
        colorWeekday = a.getColor(R.styleable.CalendarView_colorWeekday, 0xFF888888);
        colorTitle = a.getColor(R.styleable.CalendarView_colorTitleText, 0xff000000);
        colorTitleBackground = a.getColor(R.styleable.CalendarView_colorTitleTint, 0x00000000);
        colorOval = a.getColor(R.styleable.CalendarView_colorSelectTint, 0x880e4ca0);
        colorOvalToDay = a.getColor(R.styleable.CalendarView_colorCurrentTint, 0x440e4ca0);
        colorToDay = a.getColor(R.styleable.CalendarView_colorCurrentText, 0xFF0e4ca0);
        colorSelect = a.getColor(R.styleable.CalendarView_colorSelectText, 0xFFffffff);
        a.recycle();
    }

    public void setHeightTitle(int h){ //в dp
        TITLE_HEIGHT = (int) (h * DENSITY);
    }

    public int getHeightTitle(){ //в dp
        return TITLE_HEIGHT;
    }
    public void setHeightCell(int h){ //в dp
        CELL_HEIGHT = (int) (h * DENSITY);
    }
    public void setBorder(int h){ //в dp
        BORDER = (int) (h * DENSITY);
    }

    public void setDateArray(int position){
        setPositionMonthYear(position);
        countDay = daysInMonth(positionYear, positionMonth);
        if (beginDay > 0) {
            int i;
            if (positionMonth == 0) i = 31;
            else i = daysInMonth(positionYear, positionMonth - 1);
            for (int j = beginDay - 1; j >= 0; j--){
                informDay[j].numDay = i;
                informDay[j].isCurrencyMont = false;
                i--;
            }
        }
        for (int i = 0; i < countDay; i++) {
            informDay[i+beginDay].numDay = i + 1;
            informDay[i+beginDay].isCurrencyMont = true;
        }
        endDay = beginDay + countDay;
        int j = 1;
        for (int i = endDay; i < maxArrayDay; i++) {
            informDay[i].numDay = j;
            informDay[i].isCurrencyMont = false;
            j++;
        }
    }

    public InformDay[] formDateArray(int year, int month, int beginD) {
        InformDay[] id = new InformDay[maxArrayDay];
        for (int j = 0; j < maxArrayDay; j++) {
            id[j] = new InformDay();
        }
        int countD = daysInMonth(year, month);
        if (beginD > 0) {
            int i;
            if (month == 0) i = 31;
            else i = daysInMonth(year, month - 1);
            for (int j = beginD - 1; j >= 0; j--){
                id[j].numDay = i;
                id[j].isCurrencyMont = false;
                i--;
            }
        }
        for (int i = 0; i < countD; i++) {
            id[i+beginD].numDay = i + 1;
            id[i+beginD].isCurrencyMont = true;
        }
        int endD = beginD + countD;
        int j = 1;
        for (int i = endD; i < maxArrayDay; i++) {
            id[i].numDay = j;
            id[i].isCurrencyMont = false;
            j++;
        }
        return id;
    }

    private void initCalendarView(){
        removeAllViews();
        Current_Date = new Date();
        Current_Month = Current_Date.getMonth();
        Current_Year = Current_Date.getYear();
        if (Current_Year < 1000) Current_Year+=1900;
        Today = Current_Date.getDate();
        if (baseMonth < 0) {
            baseMonth = Current_Year * 12 + Current_Month;
            baseViewMonth = Current_Month;
            baseViewYear = Current_Year;
        }
        informDay = new InformDay[maxArrayDay];
        for (int j = 0; j < maxArrayDay; j++)
            informDay[j] = new InformDay();
        viewPager = new ViewPager(context);
        calendarHeigth = TITLE_HEIGHT + CELL_HEIGHT * 7 + BORDER * 8;
        viewPager.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT,
                calendarHeigth));
        addView(viewPager);
        startPager = true;
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(basePosition);
        viewPager.setOffscreenPageLimit(OffscreenPageLimit + 1);
        viewPager.setOnPageChangeListener(changePage);

    }

    public void setCountMonth(int maxMonth, int maxLeftMonth) {
        MAX_MONTH_PAGER = maxMonth;
        basePosition = maxLeftMonth;
    }

    public void setBeginYearMonth(int year, int month) {
        baseMonth = year * 12 + month;
        baseViewMonth = month;
        baseViewYear = year;
    }

    public void monthPlus(int delt) {
        int pos = viewPager.getCurrentItem() + delt;
        if (pos >= 0
                && pos <= MAX_MONTH_PAGER) {
            viewPager.setCurrentItem(pos);
        }
    }

    public void setPositionMonthYear(int position){
        int i = position - basePosition + baseMonth;
        positionYear = i / 12;
        positionMonth = i % 12;
        Date First_Date = new Date(positionYear, positionMonth, 1);
        beginDay = First_Date.getDay();
        if (beginDay<2) beginDay = beginDay + 5;
        else beginDay = beginDay - 2;
    }

    private int getWeekdayToDate(int year, int month, int day) {
        Date First_Date = new Date(year, month, day);
        int w = First_Date.getDay();
        if (w<2) w = w + 5;
        else w = w - 2;
        return w;
    }

    public int getCalendarHeigth(){
        return calendarHeigth;
    }

    public static int daysInMonth(int Year, int Month){
        int n = 28;
        if (Month == 1) {
            if (Year % 4 == 0 && Year % 100 != 0 || Year % 400 == 0) n = 29;
        } else {
            n = Days_in_Month[Month];
        }
        return n;
    }

    public void setAdapter(CalendarAdapter ca){
        if (ca == null) {
            calendarAdapter = new Adapter();
        } else {
            calendarAdapter = ca;
        }
        initCalendarView();
        calendarAdapter.setCurrentDate(Current_Year, Current_Month, Today,
                getWeekdayToDate(Current_Year, Current_Month, Today));
    }

    public void notifAdapter(){
        int c = viewPager.getCurrentItem();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(c);
    }

    private ViewPager.OnPageChangeListener changePage = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            setPositionMonthYear(position);
            calendarAdapter.onChangeMonth(positionMonth, positionYear);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    //                          Adapter

    PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return MAX_MONTH_PAGER;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            Log.d("QWERT","instantiateItem position="+position);
            View view = formView(context, position);
            container.addView(view);
            if (startPager && position == basePosition) {
                startPager = false;
                calendarAdapter.onChangeMonth(baseViewMonth, baseViewYear);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    };

    private View formView(Context context, int position){
        setDateArray(position);
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(VERTICAL);
        ll.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        InfoMonth im = new InfoMonth();
        im.year = positionYear;
        im.month = positionMonth;
        im.beginDay = beginDay;
        im.yearMonth = positionYear * 12 + positionMonth;
        ll.setTag(im);
        if (TITLE_HEIGHT > 0) {
            LinearLayout layoutTitle = new LinearLayout(context);
            layoutTitle.setOrientation(HORIZONTAL);
            layoutTitle.setLayoutParams(new LayoutParams(MATCH_PARENT, TITLE_HEIGHT));
            View vt = calendarAdapter.viewTitle(positionMonth, positionYear);
            vt.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
            layoutTitle.addView(vt);
            ll.addView(layoutTitle);
        }
        if (BORDER > 0) ll.addView(setBorderLayout(MATCH_PARENT, BORDER));
        LinearLayout layoutWeekday = new LinearLayout(context);
        layoutWeekday.setOrientation(HORIZONTAL);
        layoutWeekday.setLayoutParams(new LayoutParams(MATCH_PARENT, CELL_HEIGHT));
        View v = null;
        if (BORDER > 0) layoutWeekday.addView(setBorderLayout(BORDER, MATCH_PARENT));
        for (int i = 0; i < 7; i++){
            v = calendarAdapter.viewWeekday(i);
            v.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT, 1));
            layoutWeekday.addView(v);
            if (BORDER > 0) layoutWeekday.addView(setBorderLayout(BORDER, MATCH_PARENT));
        }
        ll.addView(layoutWeekday);
        if (BORDER > 0) ll.addView(setBorderLayout(MATCH_PARENT, BORDER));

        int t = 0;
        for (int ww = 0; ww < 6; ww++){
            LinearLayout layoutWeek = new LinearLayout(context);
            layoutWeek.setOrientation(HORIZONTAL);
            layoutWeek.setLayoutParams(new LayoutParams(MATCH_PARENT, CELL_HEIGHT));
            if (BORDER > 0) layoutWeek.addView(setBorderLayout(BORDER, MATCH_PARENT));
            for (int i = 0; i < 7; i++){
                if (informDay[t].isCurrencyMont) {
                    v = calendarAdapter.viewDay(positionYear, positionMonth, informDay[t].numDay, i);
                    v.setOnClickListener(clickDay);
//                    v.setOnLongClickListener(clickLong);
                }
                else v = calendarAdapter.viewDayNoMonth(informDay[t].numDay, i);
                v.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT, 1));
                layoutWeek.addView(v);
                if (BORDER > 0) layoutWeek.addView(setBorderLayout(BORDER, MATCH_PARENT));
                t++;
            }
            ll.addView(layoutWeek);
            if (BORDER > 0) ll.addView(setBorderLayout(MATCH_PARENT, BORDER));
        }
        return ll;
    }

    private View setBorderLayout(int w, int h){
        LinearLayout l = new LinearLayout(context);
        l.setLayoutParams(new ViewGroup.LayoutParams(w, h));
        l.setBackgroundColor(BORDER_COLOR);
        return l;
    }

//    private OnLongClickListener clickLong = new OnLongClickListener(){
//
//        @Override
//        public boolean onLongClick(View v) {
//            int day = getNumDay(v);
//            if (day > -1) {
//                int d = day + 1 - beginDay;
//                calendarAdapter.onLongClickDay(v, positionYear, positionMonth, d,
//                        getWeekdayToDate(positionYear, positionMonth, d));
//            }
//            return true;
//        }
//    };

    private OnClickListener clickDay = new OnClickListener(){

        @Override
        public void onClick(View v) {
            int day = getNumDay(v);
            if (day > -1) {
                int d = day - beginDay;
                calendarAdapter.onClickDay(v, positionYear, positionMonth, d,
                        getWeekdayToDate(positionYear, positionMonth, d));
            }
        }
    };

    private int getNumDay(View vv) {
        int position = viewPager.getCurrentItem();
        setPositionMonthYear(position);
        LinearLayout view = (LinearLayout) vv.getParent().getParent();
        int ik = view.getChildCount();
        int d = 1, day = -1;
        int delt = 0;
        if (TITLE_HEIGHT > 0) {
            delt = 1;
        }
        for (int ii = 1 + delt; ii < ik; ii++) {
            LinearLayout viewWeek = (LinearLayout) view.getChildAt(ii);
            int jk = viewWeek.getChildCount();
            for (int j = 0; j < jk; j++) {
                View viewDay = viewWeek.getChildAt(j);
                if (viewDay == vv) {
                    return d;
                }
                d ++;
            }
        }
        return day;
    }

    public void setSelectDate(String date) {
        String[] ar = date.split("-");
        setSelectDate(Integer.valueOf(ar[0]), Integer.valueOf(ar[1]), Integer.valueOf(ar[2]));
    }

    public void setSelectDate(final int year, int month, final int day) {
        int year_month = year * 12 + (month);
        int position = year_month - baseMonth + basePosition;
        viewPager.setCurrentItem(position);
        setPositionMonthYear(position);
        setDayYearMont = year_month;
        setDayYear = year;
        setDayMont = month;
        setDayPosition = position;
        setDayCount = 0;
        setDayDay = day;
        setDayIsPager = false;
        setDay.run();
    }

    Handler handler = new Handler();

    Runnable setDay = new Runnable() {
        @Override
        public void run() {
            InfoMonth im = null;
            LinearLayout v = null;
            int ik = viewPager.getChildCount();
            for (int i = 0; i < ik; i++) {
                v =  (LinearLayout) viewPager.getChildAt(i);
                InfoMonth im1 = (InfoMonth) v.getTag();
                if (im1.yearMonth == setDayYearMont) {
                    im = im1;
                    break;
                }
            }
            if (im != null) {
                int t = setDayDay + im.beginDay - 1;
                int w = t / 7 + 1;
                int d = t % 7;
                LinearLayout v_w = (LinearLayout) v.getChildAt(w);
                calendarAdapter.onClickDay(v_w.getChildAt(d), setDayYear, setDayMont, setDayDay, d);
                calendarAdapter.onChangeMonth(setDayMont, setDayYear);
            } else {
                if (setDayCount < 8) {
                    setDayCount++;
                    handler.postDelayed(setDay, 50);
                }
            }
        }
    };

    public class InformDay{
        public boolean isCurrencyMont;
        public int numDay;
    }

    public class InfoMonth {
        public int yearMonth;
        public int year;
        public int month;
        public int beginDay;
    }

    private class Adapter implements CalendarAdapter{
        final int WORK_DAY = 0, NO_WORK_DAY = 1, TO_DAY = 2;

        int textId, backLayoutId;
        float textSize = 14, diamCell = 32; // SP
        int diamCellP;                      // pixels int
//        int typeWorkDay;
        int currentDate;
        String[] nameWeekdays = new String[7];
        String[] nameMonths = new String[12];
        public SelectDate selectDate;

        public Adapter() {
            for (int i = 0; i < 12; i++) {
                Date d = new Date(2018, i, 1);
                nameMonths[i] = firstUpperCase(DateFormat.format("LLLL", d).toString());
            }

            String[] stD = new DateFormatSymbols().getShortWeekdays();
            for (int i = 2; i < 8; i++) {
                nameWeekdays[i - 2] = stD[i];
            }
            nameWeekdays[6] = stD[1];

            textId = generateViewId();
            backLayoutId = generateViewId();
            diamCellP = (int) (diamCell * DENSITY);
//            typeWorkDay = WORK_LAST;
            selectDate = new SelectDate();
            selectDate.isSelect = false;
        }

        public String firstUpperCase(String word){
            if(word != null && word.length() > 0) {
                return word.substring(0, 1).toUpperCase() + word.substring(1);
            } else return "";
        }

        @Override
        public View viewTitle(int month, int year) {
            TextView tv = new TextView(context);
            ViewGroup.LayoutParams lp = new LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(lp);
            tv.setId(textId);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(colorTitle);
            tv.setBackgroundColor(colorTitleBackground);
            tv.setTextSize(textSize);
            tv.setText(nameMonths[month] + " " + year);
            return tv;
        }

        @Override
        public View viewWeekday(int number) {
            TextView tv = new TextView(context);
            ViewGroup.LayoutParams lp = new LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(lp);
            tv.setId(textId);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(colorWeekday);
            tv.setTextSize(textSize);
            tv.setText(nameWeekdays[number]);
            return tv;
        }

        @Override
        public View viewDay(int year, int month, int number, int weekday) {
            RelativeLayout rl = new RelativeLayout(context);
            ViewGroup.LayoutParams lpRl = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
            rl.setLayoutParams(lpRl);
//            rl.setGravity(Gravity.CENTER);

            LinearLayout ll = new LinearLayout(context);
            RelativeLayout.LayoutParams lpLp = new RelativeLayout.LayoutParams(diamCellP, diamCellP);
            lpLp.addRule(RelativeLayout.CENTER_IN_PARENT);
            ll.setLayoutParams(lpLp);
            ll.setOrientation(VERTICAL);
            ll.setId(backLayoutId);
            ll.setVisibility(GONE);
            ll.setBackground(getOval());
            rl.addView(ll);

            TextView tv = new TextView(context);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            tv.setLayoutParams(lp);
            tv.setId(textId);
            tv.setText(String.valueOf(number));
            tv.setTextColor(colorNoWork);
            tv.setTextSize(textSize);
            rl.addView(tv);
            setDay(rl, year, month, number, weekday);
            return rl;
        }

        private ShapeDrawable getOval() {
            ShapeDrawable oval = new ShapeDrawable (new OvalShape());
            oval.setIntrinsicHeight(diamCellP);
            oval.setIntrinsicWidth(diamCellP);
            oval.getPaint().setColor(colorOval);
            return oval;
        }

        private ShapeDrawable getOvalToDay() {
            ShapeDrawable oval = new ShapeDrawable (new OvalShape());
            oval.setIntrinsicHeight(diamCellP);
            oval.setIntrinsicWidth(diamCellP);
            oval.getPaint().setColor(colorOvalToDay);
            return oval;
        }

        @Override
        public View viewDayNoMonth(int number, int weekday) {
            TextView tv = new TextView(context);
            ViewGroup.LayoutParams lp = new LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(lp);
            tv.setId(textId);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(colorNoDay);
            tv.setTextSize(textSize);
            tv.setText(String.valueOf(number));
            return tv;
        }

        private void setDay(View v, int year, int month, int number, int weekday) {
            int typeDay = getTypeDay(year, month, number, weekday);
            int colorType = colorText(typeDay);
            TextView tv = (TextView) v.findViewById(textId);
            tv.setTextColor(colorType);
            if (typeDay == TO_DAY) {
                LinearLayout ll = (LinearLayout) v.findViewById(backLayoutId);
                ll.setVisibility(View.VISIBLE);
                ll.setBackground(getOvalToDay());
            }

            if (selectDate.isSelect) {
                if (selectDate.year == year
                        && selectDate.month == month
                        && selectDate.day == number) {
                    selectDate.v = v;
                    selectDate.weekday = weekday;
                    tv.setTextColor(colorSelect);
                    LinearLayout ll = (LinearLayout) v.findViewById(backLayoutId);
                    ll.setVisibility(View.VISIBLE);
                }
            }
        }

        private int colorText(int typeDay) {
            switch (typeDay) {
                case WORK_DAY: return colorWork;
                case NO_WORK_DAY: return colorNoWork;
                case TO_DAY: return colorToDay;
            }
            return 0;
        }

        private int getTypeDay(int year, int month, int number, int weekday) {
            int typeDay = WORK_DAY;
            int comeYearMonthDay = (year * 12 + month) * 100 + number;
            if (typeWorkDay == WORK_LAST) {
                if (currentDate < comeYearMonthDay) {
                    typeDay = NO_WORK_DAY;
                }
            } else if (typeWorkDay == NEXT) {
                if (currentDate > comeYearMonthDay) {
                    typeDay = NO_WORK_DAY;
                }
            }
            if (comeYearMonthDay == currentDate) {
                typeDay = TO_DAY;
            }
            return typeDay;
        }

        @Override
        public void onClickDay(View v, int year, int month, int number, int weekday) {
            int typeDay = getTypeDay(year, month, number, weekday);
            if (typeDay != NO_WORK_DAY) {
                if (selectDate.isSelect) {
                    LinearLayout ll_s = (LinearLayout) selectDate.v.findViewById(backLayoutId);
                    if (yearMonthDay(selectDate.year, selectDate.month, selectDate.day) == currentDate) {
                        ll_s.setBackground(getOvalToDay());
                    } else {
                        ll_s.setVisibility(View.GONE);
                    }
                    ((TextView) selectDate.v.findViewById(textId))
                            .setTextColor(colorText(getTypeDay(selectDate.year,
                                    selectDate.month, selectDate.day, selectDate.weekday)));
                }
                selectDate.isSelect = true;
                selectDate.v = v;
                selectDate.year = year;
                selectDate.month = month;
                selectDate.day = number;
                selectDate.weekday = weekday;
                ((TextView) v.findViewById(textId)).setTextColor(colorSelect);
                LinearLayout ll = (LinearLayout) v.findViewById(backLayoutId);
                if (yearMonthDay(year, month, number) == currentDate) {
                    ll.setVisibility(View.GONE);
                    ll.setBackground(getOval());
                }
                ScaleAnimation grow = new ScaleAnimation(0, 1, 0, 1,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                grow.setDuration(200);
                ll.startAnimation(grow);
                ll.setVisibility(View.VISIBLE);
                if (calendarCallBack != null) {
                    calendarCallBack.onChangeDay(v, year, month, number, weekday);
                }
            }
        }

//        @Override
//        public void onLongClickDay(View v, int year, int month, int number, int weekday) {
//
//        }

        @Override
        public void setCurrentDate(int year, int month, int number, int weekday) {
            currentDate = (year * 12 + month) * 100 + number;
            if (calendarCallBack != null) {
                calendarCallBack.setCurrentDate(year, month, number, weekday);
            }
        }

        @Override
        public void onChangeMonth(int month, int year) {

        }

        public int yearMonthDay(int year, int month, int number) {
            return (year * 12 + month) * 100 + number;
        }
    }

    public interface CalendarAdapter {
        View viewTitle(int month, int year);
        View viewWeekday(int number);
        View viewDay(int year, int month, int number, int weekday);
        View viewDayNoMonth(int number, int weekday);
        void onClickDay(View v, int year, int month, int number, int weekday);
//        void onLongClickDay(View v, int year, int month, int number, int weekday);
        void setCurrentDate(int year, int month, int number, int weekday);
        void onChangeMonth(int month, int year);
    }

    public interface CalendarCallBack {
        void onChangeDay(View v, int year, int month, int number, int weekday);
        void onChangeMonth(int year, int month, int number);
        void setCurrentDate(int year, int month, int number, int weekDay);
    }

    public static class SelectDate {
        public boolean isSelect;
        public View v;
        public int year;
        public int month;
        public int day;
        public int weekday;
    }
}
