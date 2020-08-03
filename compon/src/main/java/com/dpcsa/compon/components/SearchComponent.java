package com.dpcsa.compon.components;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamModel;

public class SearchComponent extends BaseComponent {

    public View viewSearch;
    RecyclerComponent recycler;
    Handler handler = new Handler();
    public String nameSearch;
    private boolean isChangeText;
    private ParamModel modelNew;
    private String[] paramArray;
    private int delayMillis = 700;
    private int minLen = 3;

    public SearchComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        viewSearch = parentLayout.findViewById(paramMV.viewSearchId);
        nameSearch = activity.getResources().getResourceEntryName(paramMV.viewSearchId);
        isChangeText = true;
        if (viewSearch instanceof EditText){
            ((EditText) viewSearch).addTextChangedListener(new Watcher());
        } else {
            iBase.log("0006 View для SearchComponent должно быть EditText в " + multiComponent.nameComponent);
            return;
        }
        if (paramMV.paramView != null || paramMV.paramView.indicatorId != 0) {
            BaseComponent bc = multiComponent.getComponent(paramMV.paramView.indicatorId);
            if (bc != null) {
                recycler = (RecyclerComponent) bc;
            }
        }
        if (recycler == null) {
            iBase.log("0005 Для SearchComponent не найден RecyclerView в " + multiComponent.nameComponent);
            return;
        }
        delayMillis = paramMV.delayMillis;
        minLen = paramMV.minLen;
        modelNew = new ParamModel(paramMV.paramModel.method);
        if (paramMV.paramModel.param != null && paramMV.paramModel.param.length() > 0) {
            paramArray = paramMV.paramModel.param.split(",");
        }
    }

    @Override
    public void changeData(Field field) {

    }

    public class Watcher implements TextWatcher{

        private String searchString = "";
        String nameParam;

        private Runnable task = new Runnable() {
            @Override
            public void run() {
                if (searchString.length() <= minLen) return;;
                String stringParam = " ";
                if (modelNew.method < 10) {
                    componGlob.setParamValue(nameParam, searchString);
                    recycler.updateListData(paramMV.paramModel, paramMV.hide);
                } else if (modelNew.method == ParamModel.GET_DB) {
                    if (paramArray != null) {
                        String sep = "";
                        String[] searchArray = searchString.split(" ");
                        for (String st : paramArray) {
                            for (String stSearch : searchArray) {
                                stringParam += sep + st + " LIKE '%" + stSearch + "%' ";
                                sep = " OR ";
                            }
                        }
                        modelNew.url = paramMV.paramModel.url + stringParam;
                        recycler.updateData(modelNew);
                    }
                }
            }
        };

        public Watcher() {
            String[] stAr = paramMV.paramModel.param.split(",");
            nameParam = stAr[0];
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isChangeText) {
                searchString = s.toString();
                handler.removeCallbacks(task);
                handler.postDelayed(task, delayMillis);
            }
        }
    }

}
