package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.ISwitch;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

public class ComponCheckBox extends AppCompatCheckBox implements ISwitch, IAlias {

    private OnCheckedChangeListener checListener;
    private boolean callListener;
    Context context;
    String alias;

    public ComponCheckBox(Context context) {
        super(context);
        init(context, null);
    }

    public ComponCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ComponCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        callListener = true;
        setOnCheckedChangeListener(listener);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
        alias = a.getString(R.styleable.Simple_alias);
        a.recycle();
    }

    OnCheckedChangeListener listener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (checListener != null) {
                if (callListener) {
                    checListener.onCheckedChanged(buttonView, isChecked);
                }
            } else {
                if (isChecked) {
                    buttonView.setTag(true);
                } else {
                    buttonView.setTag(false);
                }
            }
        }
    };

    @Override
    public void setOn(boolean checked) {
        setChecked(checked);
    }

    //  меняет статус без вызова листенера
    @Override
    public void setOnStatus(boolean checked) {
        callListener = false;
        setChecked(checked);
        callListener = true;
    }

    @Override
    public boolean isOn() {
        return false;
    }

    @Override
    public void change() {
        toggle();
    }

    @Override
    public void changeStatus() {
        callListener = false;
        toggle();
        callListener = true;
    }

    @Override
    public void setOnChangeListener(@Nullable OnCheckedChangeListener listener) {
        checListener = listener;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
