package com.dpcsa.compon.custom_components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.single.Injector;
import com.dpcsa.compon.tools.Constants;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class TextViewNumberGrammar extends androidx.appcompat.widget.AppCompatTextView
        implements IComponent, IAlias {
    private Context context;
    private int stringArray;
    private String [] textArray;
    private String alias, value, prefix;
    private Object data;
    private boolean zeroNotView;
    private BroadcastReceiver setAcceptNotif;
    private String acceptNotif;

    public TextViewNumberGrammar(Context context) {
        super(context);
        this.context = context;
    }

    public TextViewNumberGrammar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttrs(attrs);
    }

    public TextViewNumberGrammar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttrs(attrs);
    }

    private void setAttrs(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
        alias = a.getString(R.styleable.Simple_alias);
        value = a.getString(R.styleable.Simple_value);
        prefix = a.getString(R.styleable.Simple_prefix);
        stringArray = a.getResourceId(R.styleable.Simple_stringArray, 0);
        zeroNotView = a.getBoolean(R.styleable.Simple_zeroNotView, false);
        acceptNotif = a.getString(R.styleable.Simple_acceptNotif);
        if (prefix == null) {
            prefix = "";
        }
        a.recycle();
        textArray = null;
        if (stringArray != 0) {
            textArray = getResources().getStringArray(stringArray);
        }
        if (value != null && value.length() > 0) {
            setData(value);
        }
        if (acceptNotif != null && acceptNotif.length() > 0) {
            setAcceptNotif = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String txt = intent.getStringExtra(Constants.DATA_STR);
                    setData(txt);
                }
            };
            LocalBroadcastManager.getInstance(context).registerReceiver(setAcceptNotif,
                    new IntentFilter(acceptNotif));
        }
    }

    @Override
    public void setData(Object data) {
        this.data = data;
        int num = 0;
        String std = "";
        boolean isStr = false;
        if (data instanceof String) {
            try {
                num = Integer.valueOf((String) data);
            } catch (NumberFormatException e) {
                std = (String) data;
                int in = std.length() - 1;
                num = 0;
                int res = 0;
                int j = 0;
                for (int i = in; i > -1; i --) {
                    char c = std.charAt(i);
                    if (c >= '0' && c <= '9') {
                        res = c - 48;
                        isStr = true;
                        j++;
                        if (j > 1) {
                            num += (res * 10);
                            break;
                        } else {
                            num = res;
                        }
                    }
                }
                if (! isStr) {
                    setText(std);
                    return;
                }
            }
        } else {
            if (data instanceof Long) {
                long lon = (Long) data;
                num = (int) lon;
            } else if (data instanceof Integer) {
                num = (Integer) data;
            } else {
                return;
            }
        }
        if (zeroNotView && num == 0) {
            setText("");
        } else {
            String st = "";
            if (textArray != null) {
                st = " " + Injector.getComponGlob().TextForNumbet(num, textArray[0],
                        textArray[1], textArray[2]);
            }
            if (isStr) {
                setText(prefix + std + st);
            } else {
                setText(prefix + num + st);
            }
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
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (setAcceptNotif != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(setAcceptNotif);
            setAcceptNotif = null;
        }
    }
}
