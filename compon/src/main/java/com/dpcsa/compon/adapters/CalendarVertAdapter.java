package com.dpcsa.compon.adapters;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dpcsa.compon.custom_components.CalendarVertical;
import com.dpcsa.compon.interfaces_classes.WeekData;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;
import static android.widget.RelativeLayout.CENTER_IN_PARENT;

public class CalendarVertAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    CalendarVertical.ParamCalendar paramC;
    private List<WeekData> dataList;
    int MATCH = ViewGroup.LayoutParams.MATCH_PARENT;
    int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;
    SelectDate selOld, selLast, selMin, selMax;
    CalendarVertical.SelectDateListener listener;

    public CalendarVertAdapter(Context context, List<WeekData> dataList,
                               CalendarVertical.ParamCalendar paramC, CalendarVertical.SelectDateListener listener) {
        this.dataList = dataList;
        this.paramC = paramC;
        this.context = context;
        this.listener = listener;
        selOld = new SelectDate(paramC.selPos, paramC.selDay);
        selLast = new SelectDate(paramC.selPos, paramC.selDay);
        setMinMaxSel();
        setFirstDate();
    }

    private void setFirstDate() {
        if (dataList.size() > paramC.selPos) {
            WeekData wd = dataList.get(paramC.selPos);
            wd.typeRange[paramC.selDay - 1] = 4;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = new LinearLayout(context);
        LinearLayout.LayoutParams lp;
        if (viewType == 0) {
            lp = new LinearLayout.LayoutParams(MATCH, paramC.heightMonth);
            v.setLayoutParams(lp);
            v.addView(formTV());
        } else {
            lp = new LinearLayout.LayoutParams(MATCH, paramC.heightCell);
            v.setLayoutParams(lp);
            v.setGravity(Gravity.CENTER_VERTICAL);
            for (int i = 0; i < 7; i++) {
                v.addView(formTvDay());
            }
        }
        v.setOrientation(LinearLayout.HORIZONTAL);
        return new ItemHolder(v);
    }

    private TextView formTV() {
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams lptv = new LinearLayout.LayoutParams(WRAP, MATCH);
        tv.setLayoutParams(lptv);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setTextSize(paramC.monthSize);
        tv.setTextColor(paramC.textColor);
        return tv;
    }

    private RelativeLayout formTvDay() {
        RelativeLayout rr = new RelativeLayout(context);
        LinearLayout.LayoutParams lpll = new LinearLayout.LayoutParams(MATCH, paramC.selectTintDiam, 1);
        rr.setLayoutParams(lpll);

        LinearLayout betw = new LinearLayout(context);
        RelativeLayout.LayoutParams lpBetw = new RelativeLayout.LayoutParams(paramC.halfWidthCell, paramC.selectTintDiam);
        betw.setLayoutParams(lpBetw);
        betw.setBackgroundColor(0xffff0000);
        rr.addView(betw);

        LinearLayout tint = new LinearLayout(context);
        RelativeLayout.LayoutParams lptint = new RelativeLayout.LayoutParams(paramC.selectTintDiam, paramC.selectTintDiam);
        lptint.addRule(CENTER_IN_PARENT);
        tint.setLayoutParams(lptint);
        rr.addView(tint);
        TextView tv = new TextView(context);
        RelativeLayout.LayoutParams lptv = new RelativeLayout.LayoutParams(MATCH, MATCH);
        tv.setLayoutParams(lptv);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(paramC.textSize);
        tv.setTextColor(paramC.textColor);
        rr.addView(tv);
        return rr;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RelativeLayout.LayoutParams lpBetw;
        LinearLayout ll = ( LinearLayout) holder.itemView;
        WeekData wd = dataList.get(position);
        if (getItemViewType(position) == 0) {
            TextView tv = (TextView) ll.getChildAt(0);
            if (paramC.arrayMonth != null) {
                tv.setText(paramC.arrayMonth[wd.month]);
            } else {
                tv.setText("month " + wd.month);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                RelativeLayout rr = (RelativeLayout)ll.getChildAt(i);
                int tvColor = paramC.textColor;
                rr.getChildAt(1).setBackground(null);
                View viewBetw = rr.getChildAt(0);
                viewBetw.setBackground(null);
                rr.setBackground(null);
                if (paramC.rangeDate) {
                    if (wd.days[i] > 0) {
                        switch (wd.typeRange[i]) {
                            case 3:
                                rr.setBackground(shapeRect());
                                break;
                            case 2:
                                rr.getChildAt(1).setBackground(shapeRounded());
                                tvColor = paramC.selectTextColor;
                                viewBetw.setBackground(shapeRect());
                                lpBetw = new RelativeLayout.LayoutParams(paramC.halfWidthCell, paramC.selectTintDiam);
                                viewBetw.setLayoutParams(lpBetw);
                                break;
                            case 1:
                                rr.getChildAt(1).setBackground(shapeRounded());
                                viewBetw.setBackground(shapeRect());
                                lpBetw = new RelativeLayout.LayoutParams(paramC.halfWidthCell, paramC.selectTintDiam);
                                lpBetw.addRule(ALIGN_PARENT_RIGHT);
                                viewBetw.setLayoutParams(lpBetw);
                                tvColor = paramC.selectTextColor;
                                break;
                            case 4:
                                rr.getChildAt(1).setBackground(shapeRounded());
                                tvColor = paramC.selectTextColor;
                                break;
                        }
                    }
                } else {
                    if (wd.select == (i + 1)) {
                        rr.getChildAt(1).setBackground(shapeRounded());
                        tvColor = paramC.selectTextColor;
                    }

                }
                TextView tv = (TextView) rr.getChildAt(2);
                if (wd.days[i] > 0) {
                    tv.setText(String.valueOf(wd.days[i]));
                } else {
                    tv.setText("");
                }
                switch (wd.typeDays[i]) {
                    case 0:
                        tv.setTextColor(tvColor);
                        break;
                    case 9:
                        tv.setTextColor(paramC.noWorkDayColor);
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private ShapeDrawable shapeRounded() {
        float r = paramC.selectTintDiam / 2f;
        float[] outR = new float[]{r, r, r, r, r, r, r, r};
        ShapeDrawable shape = new ShapeDrawable(new RoundRectShape(outR, null, null));
        shape.getPaint().setColor(paramC.selectTintColor);
        return shape;
    }

    private ShapeDrawable shapeRect() {
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(paramC.betweenColor);
        return shape;
    }

    public void setData(long min, long max) {
        Calendar c = new GregorianCalendar();
        if (paramC.rangeDate) {
            cleanSelect();
            notifyItemRangeChanged(selMin.pos, selMax.pos - selMin.pos + 1);
            c.setTimeInMillis(min);
            SelectDate sdMin = formSelectDate(c);
            selOld.pos = sdMin.pos;
            selOld.day = sdMin.day;
            c.setTimeInMillis(max);
            SelectDate sdMax = formSelectDate(c);
            selLast.pos = sdMax.pos;
            selLast.day = sdMax.day;
            setMinMaxSel();
            setNewDiapason();
            notifyItemRangeChanged(selMin.pos, selMax.pos - selMin.pos + 1);
            gotoListener();
        } else {
            c.setTimeInMillis(min);
            SelectDate sd = formSelectDate(c);
            if (sd != null) {
                select(sd.pos, sd.day);
            }
        }
    }

    public String getData() {
        WeekData wd;
        wd = dataList.get(selMin.pos);
        Calendar c = new GregorianCalendar(wd.year, wd.month, wd.days[selMin.day - 1]);
        if (paramC.rangeDate) {
            wd = dataList.get(selMax.pos);
            Calendar c1 = new GregorianCalendar(wd.year, wd.month, wd.days[selMax.day - 1]);
            return c.getTime().getTime() + "," + c1.getTime().getTime();
        } else {
            return String.valueOf(c.getTime().getTime());
        }
    }

    private SelectDate formSelectDate(Calendar c) {
        int y, m, d, dm;
        y = c.get(Calendar.YEAR);
        m = c.get(Calendar.MONTH);
        dm = c.get(Calendar.DAY_OF_MONTH);
        d = c.get(Calendar.DAY_OF_WEEK);
        d = (d == 1) ? 7 : --d;
        int ik = dataList.size();
        for (int i = 0; i < ik; i++) {
            WeekData wd = dataList.get(i);
            int beg = wd.days[0];
            if (wd.typeDays[0] >= 9) {
                beg = 0;
            }
            int end = wd.days[6];
            if (wd.typeDays[6] >= 9) {
                end = 32;
            }
            if (wd.type != 0 && y == wd.year && m == wd.month
                    && dm >= beg && dm <= end) {
                SelectDate res = new SelectDate(i, d);
                return res;
            }
        }
        return null;
    }

    private void cleanSelect() {
        WeekData wd;
        for (int i = selMin.pos; i <= selMax.pos; i++) {
            wd = dataList.get(i);
            for (int j = 0; j < 7; j++) {
                wd.typeRange[j] = 0;
            }
        }
    }

    private void setNewDiapason() {
        int minPD = selMin.pos * 100 + selMin.day;
        int maxPD = selMax.pos * 100 + selMax.day;
        WeekData wd;
        int j1;
        for (int i = selMin.pos; i <= selMax.pos; i++) {
            wd = dataList.get(i);
            if (wd.type != 0) {
                for (int j = 1; j < 8; j++) {
                    j1 = j - 1;
                    int cPD = i * 100 + j;
                    if (cPD > minPD && cPD < maxPD) {
                        wd.typeRange[j1] = 3;
                    } else if (cPD == minPD) {
                        if (minPD == maxPD) {
                            wd.typeRange[j1] = 4;
                        } else {
                            wd.typeRange[j1] = 1;
                        }
                    } else if (cPD == maxPD) {
                        wd.typeRange[j1] = 2;
                    }
                }
            }
        }
    }

    private void gotoListener() {
        WeekData wd;
        if (listener != null) {
            wd = dataList.get(selMin.pos);
            Calendar c = new GregorianCalendar(wd.year, wd.month, wd.days[selMin.day - 1]);
            wd = dataList.get(selMax.pos);
            Calendar c1 = new GregorianCalendar(wd.year, wd.month, wd.days[selMax.day - 1]);
            listener.setDateRange(c, c1);
        }
    }

    private void select(int pos, int day) {
        WeekData wd1 = dataList.get(pos);
        WeekData wd;
        if (wd1.typeDays[day - 1] == 0) {
            if (paramC.rangeDate) {
                int i1, j1;
                cleanSelect();
                int minPosOld = selMin.pos, maxPosOld = selMax.pos;
                selOld.pos = selLast.pos;
                selOld.day = selLast.day;
                selLast.pos = pos;
                selLast.day = day;
                setMinMaxSel();
                setNewDiapason();
                int beginNotif = minPosOld, endNotif = maxPosOld;
                if (selMin.pos < beginNotif) {
                    beginNotif = selMin.pos;
                } else if (selMax.pos > endNotif) {
                    endNotif = selMax.pos;
                }
                notifyItemRangeChanged(beginNotif, endNotif - beginNotif + 1);
                gotoListener();
            } else {
                if (selLast.pos >= 0) {
                    wd = dataList.get(selLast.pos);
                    wd.select = 0;
                    notifyItemChanged(selLast.pos);
                }
                selLast.pos = pos;
                wd1 = dataList.get(selLast.pos);
                wd1.select = day;
                notifyItemChanged(selLast.pos);
                if (listener != null) {
                    listener.setDate(wd1.year, wd1.month, wd1.days[day - 1]);
                }
            }
        }
    }

    private void setMinMaxSel() {
        int res = 0;
        if (selOld.pos > selLast.pos) {
            res = 1;
        } else if (selOld.pos < selLast.pos) {
            res = -1;
        } else {
            if (selOld.day > selLast.day) {
                res = 1;
            } else if (selOld.day < selLast.day) {
                res = -1;
            }
        }
        if (res < 0) {
            selMin = selOld;
            selMax = selLast;
        } else {
            selMin = selLast;
            selMax = selOld;
        }
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            if (((LinearLayout)itemView).getChildCount() > 1) {
                for (int i = 0; i < 7; i++) {
                    RelativeLayout rr = (RelativeLayout) ((LinearLayout) itemView).getChildAt(i);
                    rr.setTag(Integer.valueOf (i + 1));
                    rr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            select(getAdapterPosition(), (Integer) v.getTag());
                        }
                    });
                }
            }
        }
    }

    public class SelectDate {
        public int pos, day;
        public SelectDate(int pos, int day) {
            this.pos = pos;
            this.day = day;
        }
    }
}
