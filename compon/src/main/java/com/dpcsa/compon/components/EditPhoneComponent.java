package com.dpcsa.compon.components;

import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.EditPhone;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

public class EditPhoneComponent extends BaseComponent {

    EditPhone editPhone;

    public EditPhoneComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        editPhone = (EditPhone) parentLayout.findViewById(paramMV.paramView.viewId);
        if (editPhone == null) {
            iBase.log( "Не найден EditPhone в " + multiComponent.nameComponent);
            return;
        }
        editPhone.setOnChangeStatusListener(statusListener);
    }

    OnChangeStatusListener statusListener = new OnChangeStatusListener() {
        @Override
        public void changeStatus(View v, Object status) {
            iBase.sendActualEvent(paramMV.paramView.viewId, status);
        }
    };

    @Override
    public void changeData(Field field) {

    }
}
