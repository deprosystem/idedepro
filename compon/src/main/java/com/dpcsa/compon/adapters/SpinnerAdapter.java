package com.dpcsa.compon.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dpcsa.compon.base.BaseProvider;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;

public class SpinnerAdapter extends BaseAdapter {
    private ParamComponent mSpinner;
    private BaseProvider provider;
    private WorkWithRecordsAndViews modelToView;
    public String fieldType;

    public SpinnerAdapter(BaseProvider provider,
                          ParamComponent paramMV) {
        this.provider = provider;
        mSpinner = paramMV;
        fieldType = paramMV.paramView.fieldType;
        modelToView = new WorkWithRecordsAndViews();
    }

    @Override
    public int getCount() {
        return provider.getCount();
    }

    @Override
    public Record getItem(int position) {
        return provider.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        Context context = parent.getContext();
        Record rec = (Record) provider.get(position);
        int typeRec = getItemViewType(rec);
        if (view == null) view = LayoutInflater.from(context).inflate(mSpinner.paramView.layoutTypeId[typeRec], parent, false);
        modelToView.RecordToView(rec, view);
        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Context context = parent.getContext();
        Record rec = (Record) provider.get(position);
        int typeRec = getItemViewType(rec);
        if (typeRec >= mSpinner.paramView.layoutFurtherTypeId.length) {
            typeRec = mSpinner.paramView.layoutFurtherTypeId.length - 1;
        }
        if (view == null) view = LayoutInflater.from(context).inflate(mSpinner.paramView.layoutFurtherTypeId[typeRec], parent, false);
        modelToView.RecordToView(rec, view);
        return view;
    }

    public int getItemViewType(Record rec) {
        if (fieldType.length() == 0) {
            return 0;
        } else {
            Field f = rec.getField(fieldType);
            if (f == null) {
                return 0;
            }
            if (f.type == Field.TYPE_STRING) {
                return Integer.valueOf((String) f.value);
            } else {
                if (f.type == Field.TYPE_INTEGER) {
                    return (int) f.value;
                } else {
                    long ll = (Long) f.value;
                    return (int) ll;
                }
            }
        }
    }
}
