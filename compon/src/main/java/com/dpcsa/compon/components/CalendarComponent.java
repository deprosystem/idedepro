package com.dpcsa.compon.components;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.ICalendar;
import com.dpcsa.compon.interfaces_classes.OnCalendarClick;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

public class CalendarComponent extends BaseComponent {

    public ICalendar calendar;
    public CalendarComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.paramView != null || paramMV.paramView.viewId != 0) {
            calendar = (ICalendar) parentLayout.findViewById(paramMV.paramView.viewId);
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

    public OnCalendarClick cClick = new OnCalendarClick() {
        @Override
        public void onChangeDate(long newDate, int weekday) {
            if (paramMV.paramView.selectNameField != null && paramMV.paramView.selectNameField.length() > 0) {
                componGlob.setParamValue(paramMV.paramView.selectNameField, String.valueOf(newDate));
            }
            iBase.sendEvent(paramMV.paramView.viewId);
        }
    };
}
