package com.dpcsa.compon.interfaces_classes;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.dpcsa.compon.base.BaseActivity;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;

public class MoreWork implements ICustom {
    public IBase iBase;
    public Screen screen;
    public BaseActivity activity;
    public View parentLayout;

    public void setParam(IBase iBase, Screen screen) {
        this.iBase = iBase;
        this.screen = screen;
        activity = iBase.getBaseActivity();
        if (iBase.getBaseFragment() != null)  {
            parentLayout = iBase.getBaseFragment().getParentLayout();
        } else {
            parentLayout = activity.getParentLayout();
        }
    }

    @Override
    public void customClick(int viewId, int position, Record record) {

    }

    @Override
    public void afterBindViewHolder(int viewId, int position, Record record, RecyclerView.ViewHolder holder) {

    }

    @Override
    public void beforeProcessingResponse(Field response, BaseComponent baseComponent) {

    }

    @Override
    public void clickView(View viewClick, View parentView, BaseComponent baseComponent, Record rec, int position) {

    }

    @Override
    public void receiverWork(Intent intent) {

    }

    @Override
    public void changeValue(int viewId,  Field field, BaseComponent baseComponent) {

    }

    @Override
    public void afterChangeData(BaseComponent baseComponent) {

    }

    @Override
    public void setPostParam(int viewId, Record rec) {
    }

    public void startScreen() {

    }

    public void stopScreen() {

    }
}
