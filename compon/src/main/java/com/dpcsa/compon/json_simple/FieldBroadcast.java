package com.dpcsa.compon.json_simple;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dpcsa.compon.interfaces_classes.IBase;

public class FieldBroadcast extends Field{
    public String SIMPLE_ViewId = "SIMPLE_ViewId";
    public String SIMPLE_Value = "SIMPLE_Value";

    public FieldBroadcast(String name, int type, Object value) {
        super(name, type, value);
    }

    @Override
    public void setValue(Object value, int viewId, Context context) {
        this.value = value;
        Intent intentBroad = new Intent(name);
        intentBroad.putExtra(SIMPLE_ViewId, viewId);
        switch (type) {
            case TYPE_INTEGER:
                intentBroad.putExtra(SIMPLE_Value, (Integer) value);
                break;
            case TYPE_STRING:
                intentBroad.putExtra(SIMPLE_Value, (String) value);
                break;

        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentBroad);
    }
}
