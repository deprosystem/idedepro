package com.dpcsa.compon.components;

import java.util.Date;

import com.dpcsa.compon.db.UpdateDB;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BaseDB;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.db.RemoteToLocale;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IDbListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.single.Injector;

public class LoadDbComponent extends BaseComponent {
    ParamModel paramModel;
    protected long duration, newDate;
    private BaseDB baseDB;
    private UpdateDB updateDB;

    public LoadDbComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
        baseDB = Injector.getBaseDB();
        updateDB = Injector.getUpdateDB();
    }

    @Override
    public void initView() {
        paramModel = paramMV.paramModel;
    }

    @Override
    public void actual() {
        duration = paramModel.duration;
        if (duration > 0) {
            newDate = new Date().getTime();
            long oldDate = updateDB.getDate(paramModel.updateTable);
            if ((newDate - oldDate) > duration) {
                new RemoteToLocale(iBase, paramModel.updateUrl, paramModel.updateTable, paramModel.updateAlias, dbListener);
            }
        }
    }

    @Override
    public void changeData(Field field) {

    }

    IDbListener dbListener = new IDbListener() {
        @Override
        public void onResponse(IBase iBase, ListRecords listRecords, String table, String nameAlias) {
//            BaseDB baseDB = baseDB;
            baseDB.insertListRecord(iBase, table, listRecords, nameAlias);
            updateDB.add(paramModel.updateTable, newDate);
        }
    };
}
