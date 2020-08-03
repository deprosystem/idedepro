package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.JsonSimple;
import com.dpcsa.compon.json_simple.JsonSyntaxException;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;

public class ComponList extends BaseComponList implements IComponent, IAlias {
    protected ListRecords items;
    protected JsonSimple jsonSimple;

    public ComponList(Context context) {
        super(context);
    }

    public ComponList(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    @Override
    public void setData(Object data) {
        if (data instanceof String) {
//            Log.d("QWERT","setData data="+data);
            jsonSimple = new JsonSimple();
            try {
                Field ff = jsonSimple.jsonToModel((String) data);
                items = (ListRecords) ff.value;
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        } else {
            items = (ListRecords) data;
        }
        if (items == null) {
            items = new ListRecords();
        }
        setAdapter(adapter, false);
    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        return null;
    }

    BaseStaticListAdapter adapter = new BaseStaticListAdapter() {
        WorkWithRecordsAndViews modelToView = new WorkWithRecordsAndViews();

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public View getView(int position) {
            View v;
            Record record = items.get(position);
            record.add(new Field("position", Field.TYPE_INTEGER, position + 1));
            v = ((LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE)).inflate(ITEM_LAYOUT_ID, null);
            if (evenColor != 0 && (position % 2) == 0) {
                v.setBackgroundColor(evenColor);
            }
            modelToView.RecordToView(record, v);
            return v;
        }

        @Override
        public void onClickView(View view, View viewElrment, int position) {

        }
    };
}
