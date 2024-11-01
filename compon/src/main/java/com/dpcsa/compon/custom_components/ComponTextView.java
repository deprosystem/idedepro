package com.dpcsa.compon.custom_components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

import com.dpcsa.compon.R;
import com.dpcsa.compon.base.BaseActivity;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.IValidate;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.tools.Constants;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ComponTextView extends androidx.appcompat.widget.AppCompatTextView
        implements IComponent, IValidate, IAlias {
    private Context context;
    private String numberFormat, dateFormat, moneyFormat;
    private boolean dateMilisec;
    private Object data;
    private String alias;
    private String acceptNotif;
    private BroadcastReceiver setAcceptNotif;
    protected int typeValidate, idError;
    protected final int FILLED = 0, EMAIL = 1;
    public String textError = "";

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
        try {
            numberFormat = a.getString(R.styleable.Simple_numberFormat);
            moneyFormat = a.getString(R.styleable.Simple_moneyFormat);
            dateFormat = a.getString(R.styleable.Simple_dateFormat);
            acceptNotif = a.getString(R.styleable.Simple_acceptNotif);
            dateMilisec = a.getBoolean(R.styleable.Simple_dateMilisec, true);
            typeValidate = a.getInt(R.styleable.Simple_typeValidate, -1);
            textError = a.getString(R.styleable.Simple_textError);
            if (textError == null) {
                textError = "";
            }
            idError = a.getResourceId(R.styleable.Simple_idError, 0);
            alias = a.getString(R.styleable.Simple_alias);
        } finally {
            a.recycle();
        }
        if (acceptNotif != null && acceptNotif.length() > 0) {
            setAcceptNotif = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String txt = intent.getStringExtra(Constants.DATA_STR);
                    setText(txt);
                }
            };
            LocalBroadcastManager.getInstance(context).registerReceiver(setAcceptNotif,
                    new IntentFilter(acceptNotif));
        }
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
        if (data == null) return;
        if (data instanceof Record) {
            Record rec = (Record) data;
            String st = "";
            String sep = "";
            for (int i = 0; i < rec.size(); i++) {
                Field f = rec.get(i);
                if (f.value != null) {
                    String sti = String.valueOf(f.value);
                    if (sti != null && sti.length() > 0) {
                        st += sep + sti;
                        sep = ", ";
                    }
                }
            }
            setText(st);
            return;
        }
        if (dateFormat != null && dateFormat.length() > 0) {
            boolean isTZ = false;
            if (dateFormat.startsWith("tz_")) {
                dateFormat = dateFormat.substring(3);
                isTZ = true;
            }
            SimpleDateFormat df = new SimpleDateFormat(dateFormat);
            if (data instanceof String) {
                String stt = new String((String) data);
                Calendar cc = stringToDate(stt);
                Date dat;
                if (cc != null) {
                    dat = cc.getTime();
                    setText(df.format(dat));
                } else {
                    try {
                        long dd = Long.valueOf(stt);
                        dat = new Date(dd);
                        setText(df.format(dat));
                    } catch (NumberFormatException e) {
                        setText(stt);
                    }
                }
            } else if (data instanceof Long) {
                long d = (long)data;
                if ( ! dateMilisec) {
                    d = d * 1000;
                }
                if (isTZ) {
                    Date dd = new Date(d);
                    setText(df.format(dd));
                } else {
                    Calendar cc = new GregorianCalendar();
                    long off = new Date().getTimezoneOffset() * 60000;
                    cc.setTimeZone(TimeZone.getTimeZone("Etc/Greenwich"));
                    cc.setTimeInMillis(d + off);
                    Date dd = new Date(cc.get(Calendar.YEAR), cc.get(Calendar.MONTH), cc.get(Calendar.DAY_OF_MONTH));
                    setText(df.format(dd));
                }
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
        return data;
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        return getText().toString();
    }

    @Override
    public void clearData() {
        setText("");
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
        if (dd.indexOf("-") == -1) {
            return null;
        }
        String[] d = dd.split("-");
        if (tt.length() > 0) {
            String[] t = tt.split(":");
            c = new GregorianCalendar(Integer.valueOf(d[0]),
                    Integer.valueOf(d[1]) - 1,
                    Integer.valueOf(d[2]),
                    Integer.valueOf(t[0]), Integer.valueOf(t[1]), Integer.valueOf(t[2]));
        } else {
            if (d.length <= 1) {
                return null;
            }
            try {
                c = new GregorianCalendar(Integer.valueOf(d[0]),
                        Integer.valueOf(d[1]) - 1,
                        Integer.valueOf(d[2]));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return c;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (setAcceptNotif != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(setAcceptNotif);
            setAcceptNotif = null;
        }
    }

    @Override
    public boolean isValid() {
        boolean result = isValidFoc();
        if (result) {
            setErrorValid("");
        } else {
            setErrorValid(textError);
        }
        return result;
    }

    private void setErrorValid(String txt) {
        if (idError != 0) {
            TextView viewError = (TextView) getParenView().findViewById(idError);
            if (viewError != null) {
                viewError.setText(txt);
            }
        }
    }

    public boolean isValidFoc() {
        String st = getText().toString().trim();
        boolean result = false;
        switch (typeValidate) {
            case -1: return true;
            case FILLED :
                result = st != null && st.length() > 0;
                break;
            case EMAIL :
                result = android.util.Patterns.EMAIL_ADDRESS.matcher(st).matches();
                break;
        }
        return result;
    }

    private View getParenView() {
        ViewParent viewRoot = getParent();
        ViewParent view2 = viewRoot;
        ViewParent v = viewRoot.getParent();
        while (v != null) {
            view2 = viewRoot;
            viewRoot = v;
            v = viewRoot.getParent();
        }
        View vr = (View) view2;
        return vr;
    }
}
