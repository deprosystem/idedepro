package com.dpcsa.compon.components;

import androidx.drawerlayout.widget.DrawerLayout;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

public class DrawerComponent extends BaseComponent {

    DrawerLayout drawer;

    public DrawerComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        drawer = (DrawerLayout)parentLayout.findViewById(paramMV.paramView.viewId);
        if (drawer == null) {
            iBase.log( "0009 Не найден DrawerLayout в " + multiComponent.nameComponent);
            return;
        }
        activity.drawer = drawer;
        iBase.setFragmentsContainerId(paramMV.paramView.layoutTypeId[0]);
        String screen = paramMV.paramView.screens[0];
        if (screen != null && screen.length() > 0) {
            iBase.startScreen(screen, true);
        }
        iBase.startDrawerFragment(paramMV.paramView.screens[1], paramMV.paramView.layoutTypeId[1]);
    }

    @Override
    public void changeData(Field field) {

    }

}
