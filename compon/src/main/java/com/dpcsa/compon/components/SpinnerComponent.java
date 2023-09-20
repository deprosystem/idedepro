package com.dpcsa.compon.components;

import android.util.Log;
import static com.dpcsa.compon.interfaces_classes.ViewHandler.TYPE.SET_LOCALE;

import android.view.View;
import android.widget.AdapterView;
//import android.widget.Spinner;

import com.dpcsa.compon.adapters.SpinnerAdapter;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BaseProvider;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.ComponSpinner;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.Navigator;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;

public class SpinnerComponent extends BaseComponent {
    ComponSpinner spinner;
    boolean first;
    private String nameView;
    @Override
    public void initView() {
        componentTag = "SPINNER_";
        first = true;
        spinner = (ComponSpinner) parentLayout.findViewById(paramMV.paramView.viewId);
        if (spinner == null) {
            iBase.log("0009 Не найден Spinner в " + multiComponent.nameComponent);
            return;
        }
        if (spinner instanceof ComponSpinner) {
            ((ComponSpinner) spinner).isActual = paramMV.startActual;
        }
        nameView = "_" + activity.getResources().getResourceEntryName(paramMV.paramView.viewId);
        spinner.setBackgroundColor(0x00000000);
    }

    @Override
    public void changeData(Field field) {
        listData = (ListRecords) field.value;
        BaseProvider provider = new BaseProvider(listData);
        SpinnerAdapter adapter = new SpinnerAdapter(provider, paramMV);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( ! (paramMV.startNoHandlers && first)) {
                    Record record = listData.get(position);
                    componGlob.setParam(record);
                    if (paramMV.navigator != null) {
                        clickHandler(spinner, 0);
                    }
                    iBase.sendEvent(paramMV.paramView.viewId);
                }
                preferences.setNameInt(componentTag + multiComponent.nameComponent + nameView, position);
                first = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        int pos = preferences.getNameInt(componentTag + multiComponent.nameComponent + nameView, 0);
        spinner.setSelection(pos);
    }

    public SpinnerComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }
}
