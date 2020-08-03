package com.dpcsa.compon.components;

import android.util.Log;
import android.widget.CompoundButton;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.ISwitch;
import com.dpcsa.compon.interfaces_classes.Navigator;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

public class SwitchComponent extends BaseComponent {

    private ISwitch iSwitch;
    private int viewId;
    private String nameView;

    public SwitchComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        componentTag = "SWITCH_";
        viewId = paramMV.paramView.viewId;
        viewComponent = parentLayout.findViewById(viewId);
        nameView = "_" + activity.getResources().getResourceEntryName(viewId);
        iSwitch = (ISwitch) viewComponent;
        if (iSwitch == null) {
            iBase.log("Не найден SwitchCompat в " + multiComponent.nameComponent);
            return;
        }
        iSwitch.setOnChangeListener(listener);
        boolean startStatus = preferences.getNameBoolean(componentTag + multiComponent.nameComponent + nameView);
        iSwitch.setOnStatus(startStatus);
    }

    @Override
    public void changeData(Field field) {

    }

    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) { // ON
                if (paramMV.navigator != null) {
                    navigator = paramMV.navigator;
                    clickHandler(viewComponent, viewId);
                }
            } else {    // OFF
                if (paramMV.navigatorOff != null) {
                    navigator = paramMV.navigatorOff;
                    clickHandler(viewComponent, viewId);
                }
            }
            preferences.setNameBoolean(componentTag + multiComponent.nameComponent + nameView, isChecked);
        }
    };
}
