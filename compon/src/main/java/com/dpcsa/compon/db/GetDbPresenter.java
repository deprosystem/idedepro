package com.dpcsa.compon.db;

import java.util.Date;

import com.dpcsa.compon.base.BaseDB;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IDbListener;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.single.Injector;

public class GetDbPresenter {

    protected IBase iBase;
    protected IPresenterListener listener;
    protected ParamModel paramModel;
    protected long duration, newDate;
    protected String[] param;
    protected UpdateDB updateDB;
    protected BaseDB baseDB;

    public GetDbPresenter(IBase iBase, ParamModel paramModel, String[] param, IPresenterListener listener) {
        this.iBase = iBase;
        this.listener = listener;
        this.param = param;
        this.paramModel = paramModel;
        updateDB = Injector.getUpdateDB();
        baseDB = Injector.getBaseDB();
        duration = paramModel.duration;
        if (duration > 0) {
            newDate = new Date().getTime();
            long oldDate = updateDB.getDate(paramModel.updateTable);
            if ((newDate - oldDate) > duration) {
                new RemoteToLocale(iBase, paramModel.updateUrl, paramModel.updateTable, paramModel.updateAlias, dbListener);
            } else {
                Field f = null;
                if (paramModel.urlArray != null) {
                    if (paramModel.urlArrayIndex > -1) {
                        f = baseDB.get(iBase, paramModel, paramModel.urlArray[paramModel.urlArrayIndex + 1], param);
                    }
                } else {
                    f = baseDB.get(iBase, paramModel, paramModel.url, param);
                }
                listener.onResponse(f);
            }
        } else {
            Field f = null;
            if (paramModel.urlArray != null) {
                if (paramModel.urlArrayIndex > -1) {
                    f = baseDB.get(iBase, paramModel, paramModel.urlArray[paramModel.urlArrayIndex + 1], param);
                }
            } else {
                f = baseDB.get(iBase, paramModel, paramModel.url, param);
            }
            listener.onResponse(f);
        }
    }

    IDbListener dbListener = new IDbListener() {
        @Override
        public void onResponse(IBase iBase, ListRecords listRecords, String table, String nameAlias) {
            baseDB.insertListRecord(iBase, table, listRecords, nameAlias);
            updateDB.add(paramModel.updateTable, newDate);
            Field f = null;
            if (paramModel.urlArray != null) {
                if (paramModel.urlArrayIndex > -1) {
                    f = baseDB.get(iBase, paramModel, paramModel.urlArray[paramModel.urlArrayIndex + 1], param);
                }
            } else {
                f = baseDB.get(iBase, paramModel, paramModel.url, param);
            }
            listener.onResponse(f);
        }
    };
}
