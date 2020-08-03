package com.dpcsa.compon.components;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.dpcsa.compon.adapters.SpinnerAdapter;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BaseProvider;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;

public class SpinnerComponent extends BaseComponent {
    Spinner spinner;

    @Override
    public void initView() {
        spinner = (Spinner) parentLayout.findViewById(paramMV.paramView.viewId);
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
                Record record = listData.get(position);
                componGlob.setParam(record);
                iBase.sendEvent(paramMV.paramView.viewId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public SpinnerComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }
}
