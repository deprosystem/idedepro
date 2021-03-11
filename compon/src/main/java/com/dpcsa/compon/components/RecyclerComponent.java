package com.dpcsa.compon.components;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BaseProvider;
import com.dpcsa.compon.base.BaseProviderAdapter;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.Navigator;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.interfaces_classes.OnResumePause;
import com.dpcsa.compon.interfaces_classes.PushHandler;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.param.ParamView;
import com.dpcsa.compon.presenter.ListPresenter;

import static com.dpcsa.compon.interfaces_classes.PushHandler.TYPE.SELECT_RECYCLER;
import static com.dpcsa.compon.param.ParamModel.GLOBAL;

public class RecyclerComponent extends BaseComponent {
    public RecyclerView recycler;
    public BaseProviderAdapter adapter;
    private OnChangeStatusListener statusListener;

    public RecyclerComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        componentTag = "RECYCLER_";
        recycler = (RecyclerView) parentLayout.findViewById(paramMV.paramView.viewId);
        if (recycler == null) {
            iBase.log("0009 Не найден RecyclerView в " + multiComponent.nameComponent);
            return;
        }
        viewComponent = recycler;
        listData = new ListRecords();
        if (paramMV.paramView.selected) {
            if (navigator == null) {
                navigator = new Navigator();
            }
            navigator.add(new ViewHandler(0, ViewHandler.TYPE.SELECT));
            listPresenter = new ListPresenter(this);
        }
        provider = new BaseProvider(listData);

        ParamView pv = paramMV.paramView;
        LinearLayoutManager layoutManager;
        if (pv.spanCount < 2) {
            layoutManager = new LinearLayoutManager(activity);
            if (pv.horizontal) {
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            }
        } else {
            int c = pv.spanCount;
            if (c > 3) {
                c = 3;
            }
            layoutManager = new GridLayoutManager(activity, c);
        }
        if (paramMV.paramView.notify) {
            iBase.setResumePause(resumePause);
        }
        recycler.setLayoutManager(layoutManager);
        adapter = new BaseProviderAdapter(this);
        recycler.setAdapter(adapter);
    }

    @Override
    public void changeData(Field field) {
        if (listData == null) {
            if (statusListener != null) {
                checkValid();
            }
            return;
        }
        if (field == null || field.value == null || ! (field.value instanceof ListRecords)) {
            iBase.log("Не правильный тип данных в " + multiComponent.nameComponent);
            return;
        }
        int countOld = 0, countAdd = 0;
        if (paramMV.paramModel.pagination != null ) {
            countOld = listData.size();
            countAdd = ((ListRecords) field.value).size();
            if (countAdd < paramMV.paramModel.pagination.paginationPerPage) {
                paramMV.paramModel.pagination.isEnd = true;
            }
            paramMV.paramModel.pagination.paginationNumberPage ++;
        } else {
            listData.clear();
        }
        if (paramMV.paramModel.method == GLOBAL) {
            listData = (ListRecords) field.value;
            provider.setData(listData);
        } else {
            listData.addAll((ListRecords) field.value);
        }
        if (statusListener != null) {
            checkValid();
        }
        provider.setData(listData);
        if (listPresenter != null) {
            int selectStart = preferences.getNameInt(componentTag + multiComponent.nameComponent, -1);
            if (paramMV.paramView.typeValue != ParamView.TYPE_VALUE_SELECTED.NONE) {
                selectStart = -1;
            }
            int ik = listData.size();
            if (ik > 0) {
                if (selectStart == -1) {
                    if (paramMV.paramView.selectNameField.length() > 0) {
                        String nameField = paramMV.paramView.selectNameField;
                        int iNF = nameField.indexOf("=");
                        if (iNF > -1) {
                            nameField = nameField.substring(0, iNF);
                        }
                        String selVal = "";
                        switch (paramMV.paramView.typeValue) {
                            case NONE:
                                selVal = paramMV.paramView.selectValueField;
                                if (selVal.length() == 0) {
                                    selVal = getComponTools().getLocale();
                                }
                                break;
                            case PARAM:
                                selVal = componGlob.getParamValue(paramMV.paramView.selectNameField);
                                break;
                            case LOCALE:
                                selVal = getComponTools().getLocale();
                                break;
                        }

                        for (int i = 0; i < ik; i++) {
                            Record r = listData.get(i);
                            String sel = r.getString(nameField);
                            if (sel != null && sel.length() > 0 && sel.equals(selVal)) {
                                String fieldType = paramMV.paramView.fieldType;
                                if (fieldType != null && fieldType.length() > 0) {
                                    int typeRec = r.getInt(fieldType);
                                    if (typeRec < 2) {
                                        selectStart = i;
                                    }
                                } else {
                                    selectStart = i;
                                }
                                break;
                            }
                        }
                    } else {
                        for (int i = 0; i < ik; i++) {
                            Record r = listData.get(i);
                            int j = r.getInt(paramMV.paramView.fieldType);
                            if (j < 2) {
                                selectStart = i;
                                break;
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < ik; i++) {
                        Record r = listData.get(i);
                        Field f = r.getField(paramMV.paramView.fieldType);
                        if (f == null) {
                            f = new Field(paramMV.paramView.fieldType, Field.TYPE_INTEGER, 0);
                            r.add(f);
                        }
                        if (i == selectStart) {
                            f.value = 1;
                        } else {
                            if (r.fieldToInt(f) == 1) {
                                f.value = 0;
                            }
                        }
                    }
                }
                if (selectStart < 0) {
                    for (int i = 0; i < ik; i++) {
                        Record r = listData.get(i);
                        int j = r.getInt(paramMV.paramView.fieldType);
                        if (j < 2) {
                            selectStart = i;
                            break;
                        }
                    }
//                    selectStart = 0;
                } else if (selectStart >= ik && ik > 0) {
                    selectStart = ik - 1;
                }
                componGlob.setParam(listData.get(selectStart));
            }
            listPresenter.changeData(listData, selectStart);
        }
        iBase.itemSetValue(paramMV.paramView.viewId, listData.size());
        if (paramMV.paramModel.pagination != null ) {
            adapter.notifyItemRangeInserted(countOld, countAdd);
            adapter.isPaginationStart = false;
        } else {
            adapter.notifyDataSetChanged();
        }
        int[] splash = paramMV.paramView.splashScreenViewId;
        if (splash != null && splash.length > 0) {
            for (int vv: splash) {
                View v_splash = parentLayout.findViewById(vv);
                if (v_splash != null) {
                    if (listData.size() > 0) {
                        v_splash.setVisibility(View.GONE);
                    } else {
                        v_splash.setVisibility(View.VISIBLE);
                    }
                } else {
                    iBase.log("Не найден SplashView в " + multiComponent.nameComponent);
                }
            }
        }

        if (isPush && pushHandler != null) {
            scrollSelectPush(pushHandler.screen, pushHandler.handlerId);
        }
        iBase.sendEvent(paramMV.paramView.viewId);
    }

    @Override
    public void addRecord(Record rec) {
        int pos = listData.size();
        listData.add(rec);
        iBase.itemSetValue(paramMV.paramView.viewId, listData.size());
        adapter.notifyItemInserted(pos);
        if (moreWork != null) {
            moreWork.afterChangeData(this);
        }
    }

    @Override
    public void delRecord(int pos) {
        listData.remove(pos);
        iBase.itemSetValue(paramMV.paramView.viewId, listData.size());
        adapter.notifyItemRemoved(pos);
        if (moreWork != null) {
            moreWork.afterChangeData(this);
        }
    }

    OnResumePause resumePause = new OnResumePause() {
        @Override
        public void onResume() {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onPause() {

        }
    };

    public void selectItem(PushHandler push) {
        if (isChangeData) {
            scrollSelectPush(push.screen, push.handlerId);
        } else {
            isPush = true;
            pushHandler = push;
        }
    }

    private void scrollSelectPush(String nameField, int handlerId) {
        isPush = false;
        int ik = listData.size();
        String pushValue = preferences.getPushData();
        if (ik > 0 && pushValue.length() > 0) {
            Record record = null;
            int pos = -1;
            for (int i = 0; i < ik; i++) {
                record = listData.get(i);
                if (pushValue.equals(record.getString((nameField)))) {
                    pos = i;
                    break;
                }
            }
            if (pos > -1) {
                recycler.smoothScrollToPosition(pos);
                clickAdapter1(null, null, handlerId, pos, record);
//                RecyclerView.ViewHolder vh = recycler.findViewHolderForAdapterPosition(pos);
            }
        }
    }


//    public void selectItem(String nameField) {
//        if (listData != null && listData.size() > 0) {
//
//        } else {
//
//        }
//    }

    @Override
    public void setGlobalData(String name) {
        activity.setGlobalData(name, Field.TYPE_LIST_RECORD, listData);
    }

    @Override
    public void changeDataPosition(int position, boolean select) {
        if (paramMV.paramView.selected) {
            adapter.notifyItemChanged(position);
            if (paramMV.paramView.maxItemSelect == -1) {
                preferences.setNameInt(componentTag + multiComponent.nameComponent, position);
                if (select && selectViewHandler != null) {
                    Record record = listData.get(position);
                    componGlob.setParam(record);
//                    String st = record.getString(selectViewHandler.nameFragment);
                    String screen = (String) record.getField(selectViewHandler.nameFieldScreen).value;
                    if (screen != null && screen.length() > 0) {
                        iBase.startScreen(screen, true);
                    }
                }
            }
        }
    }

    public boolean isValid() {
        return ! (listData == null || listData.size() == 0);
    }

    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {
        this.statusListener = statusListener;
        checkValid();
    }

    public void updateListData(ParamModel paramModel, boolean hide) {
        if (hide) {
            listData.clear();
            adapter.notifyDataSetChanged();
        }
        updateData(paramModel);
    }

    public void checkValid() {
        if (isValid()) {
            setEvent(3);
        } else {
            setEvent(2);
        }
    }

    private void setEvent(int stat) {
        if (statusListener != null) {
            statusListener.changeStatus(recycler, stat);
        }
    }
}
