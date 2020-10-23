package com.example.htkz.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.dpcsa.compon.single.Injector;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class TextDepartDate extends AppCompatTextView {

    boolean isNigth;
    public TextDepartDate(@NonNull Context context) {
        super(context);
        init();
    }

    public TextDepartDate(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextDepartDate(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        String st = Injector.getComponGlob().getParamValue("depart_date");
        if (st != null && st.length() > 0) {
            setText(st);
        } else {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat df = new SimpleDateFormat("dd.MM");
            setText(df.format(c.getTime().getTime()));
        }
    }
}
