package com.dpcsa.compon.components;

import com.dpcsa.compon.adapters.StaticListAdapter;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BaseProvider;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.ComponList;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.param.ParamComponent;

public class StaticListComponent extends BaseComponent {
    ComponList staticList;
    StaticListAdapter adapter;

    public StaticListComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.paramView != null || paramMV.paramView.viewId != 0) {
            staticList = (ComponList) parentLayout.findViewById(paramMV.paramView.viewId);
        }
        if (staticList == null) {
            iBase.log("0009 Не найден StaticList в " + multiComponent.nameComponent);
            return;
        }
        listData = new ListRecords();
        provider = new BaseProvider(listData);
        adapter = new StaticListAdapter(this);
        staticList.setAdapter(adapter, false);
    }

    @Override
    public void changeData(Field field) {
        listData.clear();
        listData.addAll((ListRecords) field.value);
        provider.setData(listData);
        adapter.notifyDataSetChanged();
    }
}
