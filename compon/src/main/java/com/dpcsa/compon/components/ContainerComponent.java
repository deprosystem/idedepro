package com.dpcsa.compon.components;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

public class ContainerComponent extends BaseComponent {

    public ContainerComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        iBase.setFragmentsContainerId(paramMV.fragmentsContainerId);
        if (paramMV.startScreen != null) {
            iBase.startScreen(paramMV.startScreen, false);
        }
    }

    @Override
    public void changeData(Field field) {

    }
}
