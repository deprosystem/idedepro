package com.dpcsa.compon.components;

import android.util.Log;
import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.DateDiapason;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

import java.text.SimpleDateFormat;

public class DateDiapasonComponent extends BaseComponent{

    public DateDiapason dateDiapason;
    public DateDiapasonComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.paramView != null || paramMV.paramView.viewId != 0) {
            dateDiapason = (DateDiapason) parentLayout.findViewById(paramMV.paramView.viewId);
        }
        if (dateDiapason == null) {
            iBase.log("Не найден DateDiapason в " + multiComponent.nameComponent);
            return;
        }
        dateDiapason.setListenerOk(clickOk);
    }

    View.OnClickListener clickOk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            long from, before;
            String parFrom, parBefore;
            if (dateDiapason.dateLast > dateDiapason.dateBeforeLast) {
                from = dateDiapason.dateBeforeLast;
                before = dateDiapason.dateLast;
            } else {
                before = dateDiapason.dateBeforeLast;
                from = dateDiapason.dateLast;
            }
            parFrom = activity.getResources().getResourceEntryName(dateDiapason.viewFromId);
            parBefore = activity.getResources().getResourceEntryName(dateDiapason.viewBeforeId);
            componGlob.setParamValue(parFrom, String.valueOf(from));
            componGlob.setParamValue(parBefore, String.valueOf(before));
            iBase.sendEvent(paramMV.paramView.viewId);
        }
    };

    @Override
    public void changeData(Field field) {

    }
}
