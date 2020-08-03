package com.dpcsa.compon.presenter;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;

public class ListPresenter {

    private ListRecords listData;
    private int selectOld;
    private BaseComponent baseComponent;
    public enum Command {SELECT};
    private String nameSelectField;
    private int maxItemSelect, factItemSelect;

    public ListPresenter(BaseComponent baseComponent) {
        this.baseComponent = baseComponent;
        nameSelectField = baseComponent.paramMV.paramView.fieldType;
        maxItemSelect = baseComponent.paramMV.paramView.maxItemSelect;
        factItemSelect = 0;
        selectOld = -1;
    }

    public void setNameSelectField(String name) {
        nameSelectField = name;
    }

    public void changeData(ListRecords listData, int selectStart) {
        this.listData = listData;
        selectOld = selectStart;
        if (selectStart > -1 && listData.size() > 0 && maxItemSelect < 0) {
            Record record = listData.get(selectStart);
            Field ff = record.getField(nameSelectField);
            if (ff == null) {
                ff = new Field(nameSelectField, Field.TYPE_INTEGER, 0);
                record.add(ff);
            }
            ff.value = new Integer(1);
            baseComponent.changeDataPosition(selectStart, true);
        }
    }

    public void ranCommand(Command com, int position, Field field) {
        switch (com) {
            case SELECT:
                Record record = listData.get(position);
                Field ff;
                if (maxItemSelect < 0) {
                    int ii = record.getInt(nameSelectField);
                    if (ii < 2) {
                            if (selectOld > -1) {
                                record = listData.get(selectOld);
                                ff = record.getField(nameSelectField);
                                ff.value = new Integer(0);
                                baseComponent.changeDataPosition(selectOld, false);
                            }
                            selectOld = position;
                            record = listData.get(selectOld);
                            ff = record.getField(nameSelectField);
                            if (ff == null) {
                                ff = new Field(nameSelectField, Field.TYPE_INTEGER, 0);
                                record.add(ff);
                            }
                            ff.value = new Integer(1);
                            baseComponent.changeDataPosition(selectOld, true);
                    }
                } else {
                    ff = record.getField(nameSelectField);
                    if (ff == null) {
                        ff = new Field(nameSelectField, Field.TYPE_INTEGER, 0);
                        record.add(ff);
                    }
                    if (ff.type == Field.TYPE_BOOLEAN) {
                        if ((boolean) ff.value) {
                            ff.value = false;
                            if (factItemSelect > 0) {
                                factItemSelect--;
                            }
                        } else {
                            if (factItemSelect < maxItemSelect) {
                                ff.value = true;
                                factItemSelect ++;
                            }
                        }
                    } else {
                        if (record.fieldToInt(ff) == 0) {
                            if (factItemSelect < maxItemSelect) {
                                ff.value = 1;
                                factItemSelect ++;
                            }
                        } else {
                            ff.value = 0;
                            if (factItemSelect > 0) {
                                factItemSelect--;
                            }
                        }
                    }
                    baseComponent.changeDataPosition(position, false);
                }
                break;
        }
    }

}
