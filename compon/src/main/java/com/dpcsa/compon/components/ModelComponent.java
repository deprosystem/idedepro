package com.dpcsa.compon.components;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.ParentModel;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

public class ModelComponent extends BaseComponent {

    @Override
    public void initView() {

    }

    @Override
    public void changeData(Field field) {
        ParentModel pm = iBase.getParentModel(paramMV.name);
        pm.field = field;
        for (BaseComponent bc : pm.componentList) {
            bc.setParentData(field);
        }
    }

    public ModelComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }
}
