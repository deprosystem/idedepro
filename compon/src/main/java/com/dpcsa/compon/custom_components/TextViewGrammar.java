package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.single.Injector;

public class TextViewGrammar extends androidx.appcompat.widget.AppCompatTextView
        implements IComponent, IAlias {
    private Context context;
    private int stringArray;
    private String [] textArray;
    private String alias;
    private Object data;
    private boolean zeroNotView;

    public TextViewGrammar(Context context) {
        super(context);
        this.context = context;
    }

    public TextViewGrammar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttrs(attrs);
    }

    public TextViewGrammar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttrs(attrs);
    }

    private void setAttrs(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
        alias = a.getString(R.styleable.Simple_alias);
        stringArray = a.getResourceId(R.styleable.Simple_stringArray, 0);
        zeroNotView = a.getBoolean(R.styleable.Simple_zeroNotView, false);
        a.recycle();
        textArray = null;
        if (stringArray != 0) {
            textArray = getResources().getStringArray(stringArray);
        }
    }

    @Override
    public void setData(Object data) {
        if (textArray != null) {
            int num = 0;
            if (data instanceof Long) {
                long lon = (Long) data;
                num = (int) lon;
            } else if (data instanceof Integer) {
                num = (Integer) data;
            } else if (data instanceof String){
                try {
                    num = Integer.valueOf((String) data);
                } catch (NumberFormatException e) {
                    Log.e("","");
                }
            } else {
                return;
            }
            if (zeroNotView && num == 0) {
                setText("");
            } else {
                String st = Injector.getComponGlob().TextForNumbet(num, textArray[0],
                        textArray[1], textArray[2]);
                setText(st);
            }
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

    @Override
    public void clearData() {

    }
}
