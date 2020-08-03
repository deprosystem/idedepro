package com.dpcsa.compon.components;

import android.util.Log;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.Calendar;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.Param;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

public class CalendarComponent extends BaseComponent {

    public Calendar calendar;
    public CalendarComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.paramView != null || paramMV.paramView.viewId != 0) {
            calendar = (Calendar) parentLayout.findViewById(paramMV.paramView.viewId);
        }
        if (calendar == null) {
            iBase.log("Не найден Calendar в " + multiComponent.nameComponent);
            return;
        }
        calendar.setListenerOk(cClick);
    }

    @Override
    public void changeData(Field field) {

    }

    public Calendar.CalendarClick cClick = new Calendar.CalendarClick() {
        @Override
        public void onChangeDate(long newDate, int weekday) {
            componGlob.setParamValue(paramMV.paramView.selectNameField, String.valueOf(newDate));
            iBase.sendEvent(paramMV.paramView.viewId);
        }
    };
}
