package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ComponTextView extends androidx.appcompat.widget.AppCompatTextView
        implements IComponent, IAlias {
    private Context context;
    private String numberFormat, dateFormat, moneyFormat;
    private boolean dateMilisec;
    private Object data;
    private String alias;

    public ComponTextView(Context context) {
        super(context);
        this.context = context;
    }

    public ComponTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttrs(attrs);
    }

    public ComponTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttrs(attrs);
    }

    private void setAttrs(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
        numberFormat = a.getString(R.styleable.Simple_numberFormat);
        moneyFormat = a.getString(R.styleable.Simple_moneyFormat);
        dateFormat = a.getString(R.styleable.Simple_dateFormat);
        dateMilisec = a.getBoolean(R.styleable.Simple_dateMilisec, true);
        alias = a.getString(R.styleable.Simple_alias);
        a.recycle();
    }

    public String getNumberFormat() {
        return numberFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
        if (dateFormat != null && dateFormat.length() > 0) {
            SimpleDateFormat df = new SimpleDateFormat(dateFormat);
            if (data instanceof String) {
                String stt = new String((String) data);
                Date dat = stringToDate(stt).getTime();
                setText(df.format(dat));
            } else if (data instanceof Long) {
                long d = (long)data;
                if ( ! dateMilisec) {
                    d = d * 1000;
                }
                Date dd = new Date(d);
                setText(df.format(dd));
            } else if (data instanceof Date) {

            }
        } else if (numberFormat != null && numberFormat.length() > 0) {
            if (data instanceof Long || data instanceof Double || data instanceof Float || data instanceof Integer) {
                DecimalFormat df = new DecimalFormat(numberFormat);
                setText(df.format(data));
            }
        } else if (moneyFormat != null && moneyFormat.length() > 0){
            if (data instanceof Long || data instanceof Integer) {
                DecimalFormat df = new DecimalFormat(moneyFormat);
                float fl = 0;
                if (data instanceof Long) {
                    fl = ((Long) data) / 100f;
                } else {
                    fl = ((Integer) data) / 100f;
                }

                setText(df.format(fl));
            } else if (data instanceof Double || data instanceof Float) {
                DecimalFormat df = new DecimalFormat(moneyFormat);
                setText(df.format(data));
            }
        } else {
            setText(String.valueOf(data));
        }
    }

    @Override
    public String getAlias() {
        return alias;
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
        return getText().toString();
    }

    public Calendar stringToDate(String st) {
        Calendar c;
        String dd = "";
        String tt = "";
        String[] datTime;
        if (st.indexOf("T") > 0) {
            datTime = st.split("T");
            dd = datTime[0];
            tt = datTime[1].split("-")[0];
        } else {
            dd = st;
        }
        String[] d = dd.split("-");
        if (tt.length() > 0) {
            String[] t = tt.split(":");
            c = new GregorianCalendar(Integer.valueOf(d[0]),
                    Integer.valueOf(d[1]) - 1,
                    Integer.valueOf(d[2]),
                    Integer.valueOf(t[0]), Integer.valueOf(t[1]), Integer.valueOf(t[2]));
        } else {
            c = new GregorianCalendar(Integer.valueOf(d[0]),
                    Integer.valueOf(d[1]) - 1,
                    Integer.valueOf(d[2]));
        }
        return c;
    }
}
