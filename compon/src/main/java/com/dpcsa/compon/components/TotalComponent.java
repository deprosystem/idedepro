package com.dpcsa.compon.components;

import android.util.Log;
import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.Visibility;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;

import static com.dpcsa.compon.json_simple.Field.TYPE_DOUBLE;
import static com.dpcsa.compon.json_simple.Field.TYPE_FLOAT;
import static com.dpcsa.compon.json_simple.Field.TYPE_INTEGER;
import static com.dpcsa.compon.json_simple.Field.TYPE_LONG;

public class TotalComponent extends BaseComponent {
    public View totalView;
    Record record;
    String[] nameFields;

    public TotalComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.paramView != null && paramMV.paramView.viewId != 0) {
            totalView = parentLayout.findViewById(paramMV.paramView.viewId);
        }
        if (totalView == null) {
            iBase.log("0009 Не найден TotalView в " + multiComponent.nameComponent);
            return;
        }
        record = new Record();
        nameFields = paramMV.paramView.nameFields;
        BaseComponent bc = multiComponent.getComponent(paramMV.paramView.viewIdWithList);
        if (bc != null) {
            listData = bc.listData;
        } else {
            listData = null;
            String nn = activity.getResources().getResourceEntryName(paramMV.paramView.viewIdWithList);
            iBase.log("0010 Component " + nn + " not found in " + multiComponent.nameComponent);
        }
        if (listData != null) {
            if (listData.size() > 0) {
                total();
            }
            iBase.addEvent(paramMV.paramView.viewIdWithList, this);
        } else {
            iBase.log("1001 Нет данных для TotalView в " + multiComponent.nameComponent);
        }
        if (paramMV.paramView.visibilityArray != null) {
            for (Visibility vis : paramMV.paramView.visibilityArray) {
                iBase.log("Visibility id=" + vis.viewId + " tt=" + vis.typeShow + " NN=" + vis.nameField);
            }
        }
    }

    @Override
    public void actual() {
        total();
    }

    @Override
    public void changeData(Field field) {
        total();
    }

    private void total() {
        if (nameFields == null || nameFields.length == 0) return;
        if (listData.size() > 0) {
            record.clear();
            for (Record recList : listData) {
                for (String name : nameFields) {
                    Field fRecList = recList.getField(name);
                    if (fRecList == null) continue;
                    Field fRecord = record.getField(name);
                    if (fRecord == null) {
                        Field ff = new Field(name, fRecList.type, null);
                        switch (fRecList.type) {
                            case TYPE_INTEGER :
                                ff.value = new Integer((Integer) fRecList.value);
                                break;
                            case TYPE_LONG :
                                ff.value = new Long ((Long) fRecList.value);
                                break;
                            case TYPE_DOUBLE :
                                ff.value = new Double ((Double) fRecList.value);
                                break;
                            case TYPE_FLOAT :
                                ff.value = new Float ((Float) fRecList.value);
                                break;
                        }
                        record.add(ff);
                    } else {
                        switch (fRecord.type) {
                            case TYPE_INTEGER :
                                fRecord.value = (Integer) fRecord.value + (Integer) fRecList.value;
                                break;
                            case TYPE_LONG :
                                if (fRecList.value instanceof Long) {
                                    fRecord.value = (Long) fRecord.value + (Long) fRecList.value;
                                } else {
                                    fRecord.value = (Long) fRecord.value + (Integer) fRecList.value;
                                }
                                break;
                            case TYPE_DOUBLE :
                                fRecord.value = (Double) fRecord.value + (Double) fRecList.value;
                                break;
                            case TYPE_FLOAT :
                                fRecord.value = (Float) fRecord.value + (Float) fRecList.value;
                                break;
                        }
                    }
                }
            }
            workWithRecordsAndViews.RecordToView(record, totalView, this, null);
        }
    }
}
