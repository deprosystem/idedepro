package com.dpcsa.compon.interfaces_classes;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;

public interface ICustom {
    void customClick(int viewId, int position, Record record);
    void afterBindViewHolder(int viewId, int position, Record record, RecyclerView.ViewHolder holder);
    void beforeProcessingResponse(Field response, BaseComponent baseComponent);
    void clickView(View viewClick, View parentView,
                   BaseComponent baseComponent, Record rec, int position);
    void receiverWork(Intent intent);
//    void changeValue(int viewId, Field field);
    void changeValue(int viewId, Field field, BaseComponent baseComponent);
    void afterChangeData(BaseComponent baseComponent);
    void setPostParam(int viewId, Record rec);
}
