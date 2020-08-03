package com.dpcsa.compon.base;

import android.content.ContentValues;

import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamModel;

public abstract class BaseDB {
    public abstract void insertRecord(String sql, Record record, IPresenterListener listener);
    public abstract void insertCV(String nameTable, ContentValues cv);
    public abstract void insertListRecord(IBase iBase, String sql, ListRecords listRecords, String nameAlias);
    public abstract void deleteRecord(IBase iBase, ParamModel paramModel, String where, String[] param, IPresenterListener listener);
//    public abstract void insertListRecord(IBase iBase, String sql, ListRecords listRecords);
    public abstract Field get(IBase iBase, ParamModel paramModel, String sql, String[] param);
    public abstract void get(IBase iBase, ParamModel paramModel, String[] param, IPresenterListener listener);
    public abstract void remoteToLocale(IBase iBase, String url, String table, String nameAlias);
    public abstract void updateRecord(IBase iBase, ParamModel paramModel, ContentValues cv,
                                      String where, String[] param, IPresenterListener listener);
}
