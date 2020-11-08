package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

import com.dpcsa.compon.R;
import com.dpcsa.compon.adapters.CalendarVertAdapter;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.ICalendar;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnCalendarClick;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.interfaces_classes.WeekData;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.single.Injector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarVertical extends RecyclerView implements ICalendar, IComponent, IAlias {

    private float DENSITY = getResources().getDisplayMetrics().density;
    private float DENSITY_SP = getResources().getDisplayMetrics().scaledDensity;
    private int DEF_HeightCell = (int) (DENSITY * 32);
    private int DEF_TextSize = (int) (DENSITY * 14);
    private int countBeforeMonth, countAfterMonth;
    private int viewMonth, viewWeek;
    private boolean afterToday, tillToday;
    private Calendar c;
    private List<WeekData> dataList;
    private String alias;
    int nameMonth;
    int showDateId;
    ParamCalendar paramC;
    Context context;
    long currentDate;
    int currentWeekDay;
    View parentView;
    TextView showDate;
    String showDateText = "";
    SimpleDateFormat format;
    String dateFormat, saveParam;
    Date selectD;
    OnCalendarClick cClick;
    int widthC;
    CalendarVertAdapter adapter;

    public CalendarVertical(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CalendarVertical(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CalendarVertical(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        this.context = context;
        paramC = new ParamCalendar();
        selectD = null;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CalendarV,
                0, 0);
        try {
            float ff = a.getDimension(R.styleable.CalendarV_heightCell, DEF_HeightCell);
            float df = ff * 0.7f;
            paramC.heightCell = (int) ff;
            paramC.heightMonth = (int) a.getDimension(R.styleable.CalendarV_heightMonth, DEF_HeightCell);
            paramC.selectTintDiam = (int) a.getDimension(R.styleable.CalendarV_selectTintDiam, df);
            countBeforeMonth = a.getInt(R.styleable.CalendarV_countBeforeMonth, 0);
            countAfterMonth = a.getInt(R.styleable.CalendarV_countAfterMonth, 0);
            afterToday = a.getBoolean(R.styleable.CalendarV_afterToday, false);
            tillToday = a.getBoolean(R.styleable.CalendarV_tillToday, false);
            nameMonth = a.getResourceId(R.styleable.CalendarV_nameMonth, 0);
            paramC.textColor = a.getColor(R.styleable.CalendarV_workDayColor, 0xff000000);
            paramC.noWorkDayColor = a.getColor(R.styleable.CalendarV_noWorkDayColor, 0x44000000);
            paramC.selectTintColor = a.getColor(R.styleable.CalendarV_selectTintColor, 0xff1b5ae7);
            paramC.selectTextColor = a.getColor(R.styleable.CalendarV_selectTextColor, 0xffffffff);
            paramC.betweenColor = a.getColor(R.styleable.CalendarV_betweenColor, 0x191b5ae7);
            paramC.textSize = a.getDimensionPixelSize(R.styleable.CalendarV_textDaySize, DEF_TextSize);
            paramC.monthSize = a.getDimensionPixelSize(R.styleable.CalendarV_monthSize, DEF_TextSize);
            paramC.textSize = (int) (paramC.textSize / DENSITY_SP + 0.5);
            paramC.monthSize = (int) (paramC.monthSize / DENSITY_SP + 0.5);
            paramC.rangeDate = a.getBoolean(R.styleable.CalendarV_rangeDate, false);
            showDateId = a.getResourceId(R.styleable.CalendarV_showDateId, 0);
            dateFormat = a.getString(R.styleable.CalendarV_dateFormat);
            saveParam = a.getString(R.styleable.CalendarV_saveParam);
            alias = a.getString(R.styleable.CalendarV_alias);
            if (dateFormat == null) {
                dateFormat = "dd.MM.yyyy";
            }
//            viewMonth = a.getResourceId(R.styleable.CalendarV_viewMonth, 0);
//            viewWeek = a.getResourceId(R.styleable.CalendarV_viewWeek, 0);
            if (nameMonth != 0) {
                paramC.arrayMonth = getResources().getStringArray(nameMonth);
            }
            if (afterToday) {
                countBeforeMonth = 0;
                tillToday = false;
            }
            if (tillToday) {
                countAfterMonth = 0;
            }
        } finally {
            a.recycle();
        }
        formData();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (parentView == null) {
            widthC = getWidth();
            paramC.halfWidthCell = widthC / 14;
            parentView = getParentRoot(getParent());
            if (showDateId != 0) {
                showDate = parentView.findViewById(showDateId);
                if (showDate != null) {
                    showDate.setText(showDateText);
                }
            }
        }
    }

    private void formData() {
        paramC.selPos = -1;
        if (dateFormat != null && dateFormat.length() > 0) {
            format = new SimpleDateFormat(dateFormat);
        }
        Calendar sysC = new GregorianCalendar();
        selectD = sysC.getTime();
        showDateText = format.format(selectD);
        if (showDate != null) {
            showDate.setText(showDateText);
        }
        Calendar ca = new GregorianCalendar(sysC.get(Calendar.YEAR), sysC.get(Calendar.MONTH), sysC.get(Calendar.DAY_OF_MONTH));
        long selectDate = ca.getTimeInMillis();
        Calendar startC = new GregorianCalendar(sysC.get(Calendar.YEAR), sysC.get(Calendar.MONTH), sysC.get(Calendar.DAY_OF_MONTH));
        currentDate = startC.getTimeInMillis();
        currentWeekDay = startC.get(Calendar.DAY_OF_WEEK);
        currentWeekDay = (currentWeekDay == 1) ? 7 : --currentWeekDay;
        startC.setFirstDayOfWeek(java.util.Calendar.MONDAY);
        Calendar finalC = new GregorianCalendar();
        if (countBeforeMonth > 0) {
            startC.add(Calendar.MONTH, -countBeforeMonth);
            startC.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            if ( ! afterToday) {
                startC.set(Calendar.DAY_OF_MONTH, 1);
            }
        }
        if (countAfterMonth > 0) {
            finalC.add(Calendar.MONTH, countAfterMonth);
            finalC.set(Calendar.DAY_OF_MONTH, finalC.getActualMaximum(Calendar.DATE));
        } else {
            if ( ! tillToday) {
                finalC.set(Calendar.DAY_OF_MONTH, finalC.getActualMaximum(Calendar.DATE));
            }
        }
        int month = -1;
        long finalDate = finalC.getTimeInMillis();
        long startDate = startC.getTimeInMillis();
        long currentDate = 0;
        dataList = new ArrayList<>();
        WeekData weekData = null;
        boolean isNewWeek = true, isFirstDay = false, isEndDay = false;
        if (afterToday) {
            isFirstDay = true;
        }
        if (tillToday) {
            isEndDay = true;
        }
        do {
            currentDate = startC.getTimeInMillis();
            int mm = startC.get(Calendar.MONTH);
            if (mm != month) {
                if ( ! isNewWeek) {
                    if (weekData.select > 0) {
                        paramC.selDay = weekData.select;
                        paramC.selPos = dataList.size();
                    }
                    dataList.add(weekData);
                }
                weekData = new WeekData(startC.get(Calendar.YEAR), mm, 0);
                month = mm;
                dataList.add(weekData);
                isNewWeek = true;
            }
            if (isNewWeek) {
                weekData = new WeekData(startC.get(Calendar.YEAR), mm, 1);
                isNewWeek = false;
            }
            int dw = startC.get(Calendar.DAY_OF_WEEK);
            dw = (dw == 1) ? 7 : --dw;
            int day = startC.get(Calendar.DAY_OF_MONTH);
            int dw1 = dw - 1;
            weekData.days[dw1] = day;
            weekData.typeDays[dw1] = 0;
            if (isFirstDay) {
                isFirstDay = false;
                if (day > 1) {
                    int d1;
                    int in = dw - day;
                    if (in < 1) {
                        in = 1;
                        d1 = day - dw + 1;
                    } else {
                        in++;
                        d1 = 1;
                    }
                    for (int i = in; i < dw; i++) {
                        int i1 = i - 1;
                        weekData.days[i1] = d1;
                        weekData.typeDays[i1] = 9;
                        d1++;
                    }
                }
            }
            if (selectDate == currentDate) {
                weekData.select = dw;
            }
            if (dw == 7) {
                if (weekData.select > 0) {
                    paramC.selDay = weekData.select;
                    paramC.selPos = dataList.size();

                }
                dataList.add(weekData);
                isNewWeek = true;
            }
            startC.add(Calendar.DAY_OF_MONTH, 1);
        } while (finalDate >= currentDate);
        if (isEndDay) {

        }

        setLayoutManager(new LinearLayoutManager(context));
        adapter = new CalendarVertAdapter(context, dataList, paramC, selDate);
        setAdapter(adapter);
    }

    private SelectDateListener selDate = new SelectDateListener() {
        @Override
        public void setDate(int year, int month, int day) {
            Calendar c = new GregorianCalendar(year, month, day);
            Date d = c.getTime();
            showDateText = format.format(d);
            if (showDate != null) {
                showDate.setText(showDateText);
            }
            if (cClick != null) {
                currentDate = c.getTimeInMillis();
                currentWeekDay = c.get(Calendar.DAY_OF_WEEK);
                currentWeekDay = (currentWeekDay == 1) ? 7 : --currentWeekDay;
                cClick.onChangeDate(currentDate, currentWeekDay);
            }
            if (saveParam != null && saveParam.length() > 0) {
                Injector.getComponGlob().setParamValue(saveParam, String.valueOf(d.getTime()));
            }
        }

        @Override
        public void setDateRange(Calendar min, Calendar max) {
            if (min.getTime().getTime() == max.getTime().getTime()) {
                showDateText = format.format(min.getTime());
            } else {
                showDateText = format.format(min.getTime()) + "-" + format.format(max.getTime());
            }
            if (showDate != null) {
                showDate.setText(showDateText);
            }
            if (saveParam != null && saveParam.length() > 0) {
                Record rec = new Record();
                Injector.getComponGlob().setParamValue(saveParam, min.getTime() + "," + max.getTime());
            }
        }
    };

    private View getParentRoot(ViewParent view) {
        ViewParent viewRoot = view;
        ViewParent view2 = viewRoot;
        ViewParent v = viewRoot.getParent();
        while (v != null) {
            view2 = viewRoot;
            viewRoot = v;
            v = viewRoot.getParent();
        }
        return  (View) view2;
    }

    @Override
    public void setListenerOk(OnCalendarClick cClick) {
        this.cClick = cClick;
        if (cClick != null) {
            cClick.onChangeDate(currentDate, currentWeekDay);
        }
    }

    @Override
    public void setData(Object data) {
        String st = (String) data;
        if (st == null || st.length() == 0) return;
        String[] ard = st.split(",");
        long d0, d1;
        try {
            d0 = Long.valueOf(ard[0]);
            if (ard.length > 1) {
                d1 = Long.valueOf(ard[1]);
                if (d1 < d0) {
                    long d = d0;
                    d0 = d1;
                    d1 = d0;
                }
            } else {
                d1 = d0;
            }
        } catch (NumberFormatException e) {
            return;
        }
        adapter.setData(d0, d1);
    }

    @Override
    public Object getData() {
        return adapter.getData();
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        return null;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public  class ParamCalendar {
        public int heightCell, textSize, textColor,
                selectTextColor, selectTintColor, selectTintDiam,
                monthSize, noWorkDayColor, heightMonth,
                selPos, selDay, betweenColor, halfWidthCell;
        public boolean rangeDate;
        public String[] arrayMonth;
    }

    public interface SelectDateListener {
        void setDate(int year, int month, int day);
        void setDateRange(Calendar min, Calendar max);
    }
}
