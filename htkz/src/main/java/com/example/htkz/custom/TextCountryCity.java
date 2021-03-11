package com.example.htkz.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.dpcsa.compon.custom_components.ComponEditText;
import com.dpcsa.compon.json_simple.Record;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextCountryCity extends ComponEditText {

    public TextCountryCity(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public TextCountryCity(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TextCountryCity(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);
        setBackground(null);
        setFocusable(false);
        setClickable(false);
        setMovementMethod(null);
        setKeyListener(null);
    }

//    @Override
//    public void setData(Object data) {
//        setErrorValid(null);
//        if (data == null) {
//            setText("");
//        } else {
//            Record rec = (Record) data;
//            String country = rec.getString("country_name");
//            if (country != null && country.length() > 0) {
//                String city = rec.getString("city_name");
//                if (city != null && city.length() > 0) {
//                    country += ", " + city;
//                }
//            }
//            setText(country);
//        }
//    }
}
