package com.dpcsa.compon.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BaseProvider;
import com.dpcsa.compon.custom_components.BaseStaticListAdapter;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;

public class StaticListAdapter extends BaseStaticListAdapter {

    private ParamComponent mvParam;
    private BaseProvider provider;
    private WorkWithRecordsAndViews modelToView;
    private Context context;
    private BaseComponent baseComponent;
    private LayoutInflater inflater;

    public StaticListAdapter(BaseComponent baseComponent) {
        this.baseComponent = baseComponent;
        this.provider = baseComponent.provider;
        context = baseComponent.iBase.getBaseActivity();
        inflater = LayoutInflater.from(context);
        mvParam = baseComponent.paramMV;
        modelToView = baseComponent.workWithRecordsAndViews;
    }
    @Override
    public int getCount() {
        return provider.getCount();
    }

    @Override
    public View getView(int position) {
        View view = inflater.inflate(mvParam.paramView.layoutTypeId[0], null);
        view.setTag(Integer.valueOf(position));
        modelToView.RecordToView(provider.get(position), view, baseComponent, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer) v.getTag();
                Record rec = (Record) provider.get(pos);
                baseComponent.clickAdapter1(v, v, 0, pos, rec);
            }
        });
        return view;
    }

    @Override
    public void onClickView(View view, View viewElrment, int position) {

    }
}
