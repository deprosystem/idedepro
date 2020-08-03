package com.dpcsa.compon.custom_components;

import android.view.View;

public class BaseStaticListAdapter {
    public BaseComponList baseStaticList;

    public int getCount(){
        return 0;
    }
    public View getView(int position) {
        return null;
    }
    public void onClickView(View view, View viewElrment, int position) {

    }

    public void notifyDataSetChanged() {
        baseStaticList.formView();
    }
}
