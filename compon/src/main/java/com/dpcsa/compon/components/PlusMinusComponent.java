package com.dpcsa.compon.components;

import android.util.Log;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.PlusMinus;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;

public class PlusMinusComponent extends BaseComponent {
    PlusMinus plusMinus;

    public PlusMinusComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        componentTag = "PLUS_MINUS_";
        plusMinus = (PlusMinus) parentLayout.findViewById(paramMV.paramView.viewId);
        if (plusMinus != null) {
            plusMinus.setParam(parentLayout, new Record(), this);
            viewComponent = plusMinus;
        }
    }

    @Override
    public void changeData(Field field) {

    }
}
