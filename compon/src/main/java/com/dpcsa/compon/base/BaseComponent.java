package com.dpcsa.compon.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dpcsa.compon.components.PagerVComponent;
import com.dpcsa.compon.custom_components.PlusMinus;
import com.dpcsa.compon.interfaces_classes.ActionsAfterError;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.ActivityResult;
import com.dpcsa.compon.interfaces_classes.Animate;
import com.dpcsa.compon.interfaces_classes.ISwitch;
import com.dpcsa.compon.interfaces_classes.OnResumePause;
import com.dpcsa.compon.interfaces_classes.PushHandler;
import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.components.RecyclerComponent;
import com.dpcsa.compon.interfaces_classes.AnimatePanel;
import com.dpcsa.compon.interfaces_classes.ICustom;
import com.dpcsa.compon.interfaces_classes.IValidate;
import com.dpcsa.compon.interfaces_classes.Param;
import com.dpcsa.compon.json_simple.JsonSyntaxException;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.interfaces_classes.MoreWork;
import com.dpcsa.compon.interfaces_classes.Navigator;
import com.dpcsa.compon.interfaces_classes.OnClickItemRecycler;
import com.dpcsa.compon.interfaces_classes.ParentModel;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.FieldBroadcast;
import com.dpcsa.compon.json_simple.JsonSimple;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.presenter.ListPresenter;
import com.dpcsa.compon.single.ComponTools;
import com.dpcsa.compon.single.Injector;
import com.dpcsa.compon.tools.Constants;
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.tools.phone_picker.GetCountryCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.dpcsa.compon.param.ParamModel.DEL_DB;
import static com.dpcsa.compon.param.ParamModel.GET_DB;
import static com.dpcsa.compon.param.ParamModel.POST_DB;
import static com.dpcsa.compon.param.ParamModel.UPDATE_DB;

public abstract class BaseComponent {
    public abstract void initView();
    public abstract void changeData(Field field);
    public View parentLayout;
    public BaseProvider provider;
    public ListPresenter listPresenter;
    public ParamComponent paramMV;
    public BaseActivity activity;
    public Navigator navigator;
    public MoreWork moreWork;
    public ListRecords listData;
    public Record recordComponent;
    public IBase iBase;
    public ICustom iCustom;
    public ViewHandler selectViewHandler;
    public View viewComponent;
    public Field argument;
    public Screen multiComponent;
    public ComponGlob componGlob;
    public BaseDB baseDB;
    public ComponPrefTool preferences;
    private ComponTools componTools;
    public ListRecords listRecords;
    public Field responseSave;
    public String componentTag;
    private String messageError;
    private BroadcastReceiver profileMessageReceiver = null;
    public boolean isChangeData, isPush;
    public PushHandler pushHandler;

    public WorkWithRecordsAndViews workWithRecordsAndViews = new WorkWithRecordsAndViews();

    public BaseComponent() {}

    public BaseComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent){
        initComponent(iBase, paramMV, multiComponent);
    }

    public void initComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        this.paramMV = paramMV;
        this.multiComponent = multiComponent;
        componGlob = Injector.getComponGlob();
        baseDB = Injector.getBaseDB();
        preferences = Injector.getPreferences();
        componTools = Injector.getComponTools();
        navigator = paramMV.navigator;
        paramMV.baseComponent = this;
        this.iBase = iBase;
        activity = iBase.getBaseActivity();
        isPush = false;
        pushHandler = null;
        this.parentLayout = iBase.getParentLayout();
        moreWork = paramMV.moreWork;
        if (paramMV.additionalWork != null) {
            try {
                moreWork = (MoreWork) paramMV.additionalWork.newInstance();
                moreWork.setParam(iBase, multiComponent);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void init() {
        initView();
        if (paramMV.nameReceiver != null) {
            LocalBroadcastManager.getInstance(activity)
                    .registerReceiver(startActual, new IntentFilter(paramMV.nameReceiver));
        }
        if (paramMV.paramModel != null
                && paramMV.paramModel.method == ParamModel.FIELD) {
            if (paramMV.paramModel.field instanceof FieldBroadcast) {
                LocalBroadcastManager.getInstance(iBase.getBaseActivity())
                        .registerReceiver(changeFieldValue, new IntentFilter(paramMV.paramModel.field.name));
            }
        }
        if (paramMV.mustValid != null) {
            iBase.addEvent(paramMV.mustValid, this);
        }
        if (paramMV.eventComponent == 0) {
            if (paramMV.startActual) {
                actual();
            }
        } else {
            iBase.addEvent(paramMV.eventComponent, this);
        }
    }

    public void updateData(ParamModel paramModel) {
        actualModel(paramModel);
    }

    private BroadcastReceiver startActual = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (iBase.isViewActive()) {
                        actual();
                    } else {
                        handler.postDelayed(this, 5);
                    }
                }
            }, 5);
        }
    };

    public void actualEvent(int sender, Object paramEvent) {
        actual();
    }

    public void actual() {
        actualModel(paramMV.paramModel);
    }

    private void actualModel(ParamModel paramModel) {
        isChangeData = false;
        if (paramModel != null) {
            switch (paramModel.method) {
                case ParamModel.PARENT :
                    ParentModel pm = iBase.getParentModel(paramModel.url);
                    if (pm.field == null) {
                        for (BaseComponent bc : pm.componentList) {
                            if (bc == this) {
                                return;
                            }
                        }
                        pm.componentList.add(this);
                    } else {
                        setParentData(pm.field);
                    }
                    break;
                case ParamModel.FIELD:
                    changeDataBase(paramModel.field);
                    break;
                case ParamModel.PROFILE :
                    profileMessageReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            changeDataBase(componGlob.profile);
                        }
                    };
                    iBase.setResumePause(resumePause);
                    LocalBroadcastManager.getInstance(activity).registerReceiver(profileMessageReceiver,
                            new IntentFilter(componGlob.profile.name));
                    changeDataBase(componGlob.profile);
                    break;
                case ParamModel.JSON:
                    Field ffJson = null;
                    try {
                        ffJson = componGlob.jsonSimple.jsonToModel(paramModel.url);
                    } catch (JsonSyntaxException e) {
                        iBase.log(e.getMessage());
                        e.printStackTrace();
                    }
                    if (ffJson != null) {
                        changeDataBase(ffJson);
                    }
                    break;
                case ParamModel.COUNTRY_CODE:
                    GetCountryCode gcc = new GetCountryCode();
                    gcc.getCountryCode(iBase, listener, paramMV.paramModel.url);
                    break;
                case ParamModel.GLOBAL:
//                    changeDataBase(componGlob.globalData.getField(paramModel.url));
                    listener.onResponse(componGlob.globalData.getField(paramModel.url));
                    break;
                case ParamModel.ARGUMENTS :
                    if (iBase.getBaseFragment() != null) {
                        argument = iBase.getBaseFragment().paramScreen;
                        changeDataBase(argument);
                    } else {
                        Intent intent = activity.getIntent();
                        String st = intent.getStringExtra(Constants.NAME_PARAM_FOR_SCREEN);
                        JsonSimple jsonSimple = new JsonSimple();
                        try {
                            argument = jsonSimple.jsonToModel(st);
                        } catch (JsonSyntaxException e) {
                            iBase.log(e.getMessage());
                            e.printStackTrace();
                        }
                        changeDataBase(argument);
                    }
                    break;
                case ParamModel.DATAFIELD :
                    if (paramModel.dataFieldGet != null) {
                        changeDataBase(paramModel.dataFieldGet.getField(this));
                    }
                    break;
                case GET_DB :
                    Record paramScreen = null; // ????? Параметри які передаються в Screen формує номер urlArrayIndex через параметри
                    if (paramModel.urlArray != null) {
                        Field f = iBase.getParamScreen();
                        if (f != null && f.type == Field.TYPE_RECORD) {
                            paramScreen = ((Record) f.value);
                            paramModel.urlArrayIndex = paramScreen.getInt(paramModel.urlArray[0]);
                            if (paramModel.urlArrayIndex < 0) {
                                paramModel.urlArrayIndex = 0;
                            }
                            int len = paramModel.urlArray.length - 1;
                            if (paramModel.urlArrayIndex > len) {
                                paramModel.urlArrayIndex = len;
                            }
                        }
                    }
                    baseDB.get(iBase, paramModel, setParam(paramModel.param, paramScreen), listener);
                    break;
                default: {
                    new BasePresenter(iBase, paramModel, null, null, listener);
                }
            }
        } else {
            changeDataBase(null);
        }
    }

    OnResumePause resumePause = new OnResumePause() {
        @Override
        public void onResume() {

        }

        @Override
        public void onPause() {

        }

        @Override
        public void onDestroy() {
            if (profileMessageReceiver != null) {
                LocalBroadcastManager.getInstance(activity).unregisterReceiver(profileMessageReceiver);
                profileMessageReceiver = null;
            }
        }
    };

    public BaseComponent getComponent(int id) {
        return multiComponent.getComponent(id);
    }

    public String[] setParam(String paramSt, Record rec) {
        if (paramSt == null) return null;
        String[] param = paramSt.split(",");
        int ik = param.length;
        for (int i = 0; i < ik; i++) {
            String par = param[i];
            String parValue = null;
            if (rec != null) {
                parValue = rec.getString(par);
            }
            if (parValue == null) {
                parValue = getGlobalParam(par);
            }
            if (parValue != null) {
                param[i] = parValue;
            } else {
                return null;
            }
        }
        return param;
    }

    private String getGlobalParam(String name) {
        String st = null;
        List<Param> paramV = componGlob.paramValues;
        for (Param par : paramV) {
            if (par.name.equals(name)) {
                st = par.value;
                break;
            }
        }
        return st;
    }

    public void setParentData(Field field) {
        if (field != null) {
            if (paramMV.paramModel.param.length() > 0) {
                Field f = ((Record) field.value).getField(paramMV.paramModel.param);
                if (f != null) {
                    changeDataBase(f);
                }
            } else {
                changeDataBase(field);
            }
        }
    }

    public void changeDataPosition(int position, boolean select) {
    }

    private BaseComponent getThis() {
        return this;
    }

    public void setGlobalData(String name) {
// У кожного типу компонету по своєму. Перевизначається
    }

    IPresenterListener listener = new IPresenterListener() {
        @Override
        public void onResponse(Field response) {
            if (response == null) return;
            responseSave = response;
            if (response.type == Field.TYPE_LIST_RECORD) {
                listRecords = (ListRecords) response.value;
            }
            if (iCustom != null) {
                iCustom.beforeProcessingResponse(response, getThis());
            } else if (moreWork != null) {
                moreWork.beforeProcessingResponse(response, getThis());
            }
            String fName = paramMV.paramModel.nameField;
            String addFieldName = paramMV.paramModel.nameAddField;
            if (fName != null || addFieldName != null) {
                String[] addField = null;
                if (addFieldName != null && addFieldName.length() > 0) {
                    addField = addFieldName.split(",");
                }
                String fNameTo = paramMV.paramModel.nameFieldTo;
                ListRecords listR = null;
                if (response.type == Field.TYPE_RECORD) {
                    Field ff = ((Record) response.value).get(0);
                    if (ff.type == Field.TYPE_RECORD) {
                        Field f = ((Record) ff.value).get(0);
                        listR = (ListRecords) f.value;
                    } else {
                        listR = (ListRecords) ff.value;
                    }
                } else {
                    if (response.type == Field.TYPE_LIST_RECORD) {
                        listR = (ListRecords) response.value;
                    }
                }
                int addFieldType = paramMV.paramModel.typeAddField;
                if (listR != null) {
                    for (Record record : listR) {
                        if (fName != null) {
                            Field f = record.getField(fName);
                            if (f != null) {
                                f.name = fNameTo;
                            }
                        }
                        if (addFieldName != null && addFieldName.length() > 0) {
                            for (String addSt : addField) {
                                Field ff = new Field(addSt, addFieldType, paramMV.paramModel.valueAddField);
                                record.add(ff);
                            }
                        }
                    }
                }
            }
            setFilterData();
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
            onErrorModel(statusCode, message, click);
        }
    };

    public void setFilterData() {
        ListRecords lr = null;
        Field resp = new Field(responseSave.name, responseSave.type, responseSave.value);
        if (paramMV.paramModel.filters != null) {
            lr = new ListRecords();
            ListRecords lrResp = (ListRecords) responseSave.value;
            for (Record rec : lrResp) {
                if (paramMV.paramModel.filters.isConditions(rec)) {
                    lr.add(rec);
                    if (lr.size() >= paramMV.paramModel.filters.maxSize) {
                        break;
                    }
                }
            }
            resp.value = lr;
        }
        // Реализация ParamModel обеспечивает получение данных из заданных источников в форме Field.
        // Если данные являются Record и задано nameTakeField, то результатом реализации ParamModel
        // будет Field с именем nameTakeField из этого Record
        if (paramMV.paramModel.nameTakeField == null) {
            changeDataBase(resp);
        } else {
            Record r = (Record) resp.value;
            changeDataBase(r.getField(paramMV.paramModel.nameTakeField));
        }
    }

    private void changeDataBase(Field field) {
        isChangeData = true;
        if (paramMV.paramModel != null
                && paramMV.paramModel.sortParam != null
                && paramMV.paramModel.sortParam.length() > 0) {
            sort(field);
        }
        if (paramMV.paramModel != null && paramMV.paramModel.addRecordBegining != null
                && ((ListRecords) field.value).size() > 0) {
            ((ListRecords) field.value).addAll(0, paramMV.paramModel.addRecordBegining);
        }
        if (iBase instanceof ICustom) {
            ((ICustom) iBase).changeValue(paramMV.paramView.viewId, field, this);
        }
        changeData(field);
        if (iCustom != null) {
            iCustom.afterChangeData(this);
        } else if (moreWork != null) {
            moreWork.afterChangeData(this);
        }
    }

    private void sort(Field field) {
        if (field.value instanceof ListRecords) {
            String[] param = paramMV.paramModel.sortParam.split(",");
            ListRecords listR = (ListRecords) field.value;
            Collections.sort(listR, new Comparator<Record>() {
                public int compare(Record o1, Record o2) {
                    int result = 0;
                    for (String par : param) {
                        Field f1 = o1.getField(par);
                        Field f2 = o2.getField(par);
                        switch (f1.type) {
                            case Field.TYPE_INTEGER:
                                int i1 = (Integer) f1.value;
                                int i2 = (Integer) f2.value;
                                if (i1 < i2) {
                                    return -1;
                                } else if (i1 > i2) {
                                    return 1;
                                }
                                break;
                            case Field.TYPE_LONG:
                                long l1 = (Long) f1.value;
                                long l2 = (Long) f2.value;
                                if (l1 < l2) {
                                    return -1;
                                } else if (l1 > l2) {
                                    return 1;
                                }
                                break;
                            case Field.TYPE_STRING:
                                String s1 = (String) f1.value;
                                String s2 = (String) f2.value;
                                int res = s1.compareTo(s2);
                                if (res != 0) return res;
                                break;
                        }
                    }
                    return result;
                }
            });
        }
    }

    private BroadcastReceiver changeFieldValue = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            changeDataBase(paramMV.paramModel.field);
        }
    };

    public View.OnClickListener clickView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (navigator == null) return;
            int vId = v.getId();
            if (v instanceof PlusMinus) {
                vId = 0;
            }
            clickHandler(v, vId);
        }
    };

    public void clickHandler(View v, int vId) {
        List<ViewHandler> viewHandlers = navigator.viewHandlers;
        View vv;
        Record param;
        for (ViewHandler vh : viewHandlers) {
            if (vId == vh.viewId || (vh.viewId == 0 && v == viewComponent)) {
                switch (vh.type) {
//                        case SEND_CHANGE_BACK :
//                            Record param = workWithRecordsAndViews.ViewToRecord(viewComponent, vh.paramModel.param);
//                            new BasePresenter(iBase, vh.paramModel, null, setRecord(param), listener_send_change);
//                            break;

                    case NAME_SCREEN:
                        if (recordComponent != null) {
                            componGlob.setParam(recordComponent);
                        }
                        int requestCode = -1;
                        if (vh.afterResponse != null) {
                            requestCode = activity.addForResult(vh.afterResponse, activityResult);
                        }
                        switch (vh.paramForScreen) {
                            case RECORD:
                                if (recordComponent != null) {
                                    iBase.startScreen(vh.screen, false, recordComponent, requestCode);
                                } else {
                                    if (vh.paramForSend != null && vh.paramForSend.length() > 0) {
                                        Record rec = workWithRecordsAndViews.ViewToRecord(viewComponent, vh.paramForSend);
                                        iBase.startScreen(vh.screen, false, rec, requestCode);
                                    }
                                }
                                break;
                            case RECORD_COMPONENT:
                                BaseComponent bc = getComponent(vh.componId);
                                if (bc != null) {
                                    componGlob.setParam(bc.recordComponent);
                                    iBase.startScreen(vh.screen, false, bc.recordComponent, requestCode);
                                }
                                break;
                            default:
                                iBase.startScreen(vh.screen, false, null, requestCode);
                                break;
                        }
                        break;
                    case YOUTUBE:
                        componGlob.setParam(recordComponent);
                        String stParYou = componGlob.getParamValue(vh.nameFieldWithValue);
                        if (stParYou != null && stParYou.length() > 0) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(stParYou)));
                        }
                        break;
                    case PREFERENCE_SET_VALUE:
                        switch (vh.typePref) {
                            case STRING:
                                preferences.setNameString(vh.namePreference, vh.pref_value_string);
                                break;
                            case BOOLEAN:
                                preferences.setNameBoolean(vh.namePreference, vh.pref_value_boolean);
                                break;
                        }
                        break;
                    case SET_PARAM:
                        componGlob.setParam(recordComponent);
                        break;
                    case BACK:
                        activity.onBackPressed();
                        break;
                    case EXIT:
                        activity.exitAccount();
                        break;
                    case CALL_UP:
                        if (v != null && v instanceof TextView) {
                            String st = ((TextView) v).getText().toString();
                            if (st != null && st.length() > 0) {
                                activity.callUp(st);
                            }
                        }
                        break;
                    case DIAL_UP:
                        if (v != null && v instanceof TextView) {
                            String st = ((TextView) v).getText().toString();
                            if (st != null && st.length() > 0) {
                                activity.startDialPhone(st);
                            }
                        }
                        break;
                    case SHOW_HIDE:
                        vv = parentLayout.findViewById(vh.showViewId);
                        if (vv != null) {
                            TextView tv = (TextView) v;
                            if (vv instanceof AnimatePanel) {
                                AnimatePanel ap = (AnimatePanel) vv;
                                if (ap.isShow()) {
                                    ap.hide();
                                    tv.setText(activity.getString(vh.textHideId));
                                } else {
                                    ap.show(iBase);
                                    tv.setText(activity.getString(vh.textShowId));
                                }
                            } else {
                                if (vv.getVisibility() == View.VISIBLE) {
                                    vv.setVisibility(View.GONE);
                                    tv.setText(activity.getString(vh.textHideId));
                                } else {
                                    vv.setVisibility(View.VISIBLE);
                                    tv.setText(activity.getString(vh.textShowId));
                                }
                            }
                        }
                        break;
                    case SHOW:
                        vv = parentLayout.findViewById(vh.showViewId);
                        if (vv != null) {
                            if (vv instanceof AnimatePanel) {
                                ((AnimatePanel) vv).show(iBase);
                            } else {
                                vv.setVisibility(View.VISIBLE);
                            }
                            if (vh.nameFieldWithValue != null && vh.nameFieldWithValue.length() > 0) {
                                workWithRecordsAndViews.RecordToView(paramToRecord(vh.nameFieldWithValue), vv);
                            }
                        }
                        break;
                    case HIDE:
                        vv = parentLayout.findViewById(vh.showViewId);
                        if (vv != null) {
                            if (vv instanceof AnimatePanel) {
                                ((AnimatePanel) vv).hide();
                            } else {
                                vv.setVisibility(View.GONE);
                            }
                        }
                        break;
                    case MODEL_PARAM:
                        selectViewHandler = vh;
                        ParamModel pm = vh.paramModel;
                        switch (pm.method) {
                            case DEL_DB:
                                WhereParam wp = setWhere(pm.param, null);
                                if (wp != null) {
                                    baseDB.deleteRecord(iBase, pm, wp.where, wp.param, listener_send_back_screen);
                                } else {
                                    iBase.log("deleteRecord ошибка в параметрах");
                                }
                                break;
                            case UPDATE_DB:
                                break;
                        }
                        break;
                    case CLICK_SEND :
                        boolean valid = true;
                        if (vh.mustValid != null) {
                            for (int i : vh.mustValid) {
                                vv = viewComponent.findViewById(i);
                                if (vv instanceof IValidate) {
                                    boolean validI = ((IValidate) vv).isValid();
                                    if (!validI) {
                                        valid = false;
                                    }
                                }
                            }
                        }
                        if (valid) {
                            selectViewHandler = vh;
                            param = workWithRecordsAndViews.ViewToRecord(viewComponent, vh.paramModel.param);
                            Record rec = setRecord(param);
                            for (Field f : rec) {
                                if (f.type == Field.TYPE_LIST_RECORD) {
                                    View vL = componGlob.findViewByName(viewComponent, f.name);
                                    if (vL != null) {
                                        BaseComponent bc = getComponent(vL.getId());
                                        if (bc != null) {
                                            String[] stParam = ((String) f.value).split(";");
                                            if (stParam.length > 0) {
                                                if (bc instanceof RecyclerComponent) {
                                                    ListRecords listRecParam = new ListRecords();
                                                    for (Record recList : ((RecyclerComponent) bc).listData) {
                                                        Record recParam = new Record();
                                                        for (String nameParam : stParam) {
                                                            Field fParam = recList.getField(nameParam);
                                                            if (fParam != null) {
                                                                recParam.add(fParam);
                                                            }
                                                        }
                                                        listRecParam.add(recParam);
                                                    }
                                                    f.value = listRecParam;
                                                }
                                            } else {
                                                iBase.log("1001 No data for parameter " + f.name + " in " + multiComponent.nameComponent);
                                                rec.remove(f);
                                            }
                                        } else {
                                            iBase.log("0010 Component " + f.name + " not found in " + multiComponent.nameComponent);
                                            rec.remove(f);
                                        }
                                    } else {
                                        iBase.log("0009 No item " + f.name + " in " + multiComponent.nameComponent);
                                        rec.remove(f);
                                    }
                                }
                            }
                            if (moreWork != null) {
                                moreWork.setPostParam(vh.viewId, rec);
                            }
                            componGlob.setParam(rec);
                            if (vh.paramModel.method == POST_DB) {
                                baseDB.insertRecord(vh.paramModel.url, rec, listener_send_back_screen);
                            } else {
                                new BasePresenter(iBase, vh.paramModel, null, rec, listener_send_back_screen);
                            }
                        }
                        break;
                    case GET_DATA:
                        selectViewHandler = vh;
                        param = workWithRecordsAndViews.ViewToRecord(viewComponent, vh.paramModel.param);
                        Record rec = setRecord(param);
                        componGlob.setParam(rec);
                        new BasePresenter(iBase, vh.paramModel, null, rec, listener_get_data);
                        break;
                    case SWITCH_ON:
                        if (vh.viewId == vId) {
                            ((ISwitch) v).setOn(vh.switchValue);
                        } else {
                            vv = parentLayout.findViewById(vh.viewId);
                            ((ISwitch) vv).setOn(vh.switchValue);
                        }
                        break;
                    case SWITCH_ON_STATUS:
                        if (vh.viewId == vId) {
                            ((ISwitch) v).setOnStatus(vh.switchValue);
                        } else {
                            vv = parentLayout.findViewById(vh.viewId);
                            ((ISwitch) vv).setOnStatus(vh.switchValue);
                        }
                        break;
                    case EXEC:
                        if (vh.execMethod != null) {
                            vh.execMethod.run(getThis());
                        }
                        break;
                    case NEXT_SCREEN_SEQUENCE:
                        int isc = preferences.getSplashScreen();
                        if (isc < 2) {
                            isc ++;
                            preferences.setSplashScreen(isc);
                            String stSc = preferences.getSplashNameScreen();
                            if (stSc.length() > 0) {
                                String[] stAr = stSc.split(",");
                                iBase.startScreen(stAr[isc], false);
                                activity.finish();
                            }
                        } else {
                            activity.finish();
                        }
                        break;
                    case PAGER_PLUS:
                        if (getThis() instanceof PagerVComponent) {
                            ((PagerVComponent) getThis()).pagerPlusItem();
                        }
                        break;

                    default:
                        specificComponentClick(vh);
                        break;
                }
            }
        }
    }

    private void responseOrError(int error, ParamModel paramModel, IPresenterListener listener) {
        if (messageError.length() == 0) {
            Field ff = null;
            try {
                ff = componGlob.jsonSimple.jsonToModel("{\"result\":\"ok\"}");
            } catch (JsonSyntaxException e) {
                iBase.log(e.getMessage());
                e.printStackTrace();
            }
            listener.onResponse(ff);
        } else {
            if (paramModel.errorShowView == 0) {
                if (paramModel.viewErrorDialog == null || paramModel.viewErrorDialog) {
                    iBase.showDialog(error, "subscribe failed " + messageError, null);
                }
            }
            listener.onError(error, "subscribe failed " + messageError, null);
        }
    }

    ActivityResult activityResult  = new ActivityResult() {
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data, ActionsAfterResponse afterResponse) {
            if (resultCode == activity.RESULT_OK) {
                String json = data.getStringExtra(Constants.RECORD);
                JsonSimple jsonSimple = new JsonSimple();
                Field ff = null;
                try {
                    ff = jsonSimple.jsonToModel(json);
                } catch (JsonSyntaxException e) {
                    iBase.log(e.getMessage());
                    e.printStackTrace();
                }
                afterHandler(ff, afterResponse.viewHandlers);
            }
        }
    };

    public void specificComponentClick(ViewHandler viewHandler) {

    }

    public void clickAdapter1(RecyclerView.ViewHolder holder, View view, int id, int position, Record record) {
        if (navigator != null) {
            for (ViewHandler vh : navigator.viewHandlers) {
                if (vh.viewId == id) {
                    switch (vh.type) {
                        case SELECT:
                            if (listPresenter != null) {
                                listPresenter.ranCommand(ListPresenter.Command.SELECT,
                                        position, null);
                                componGlob.setParam(record);
                            }
                            break;
                        case SET_PARAM:
                            componGlob.setParam(record);
                            break;
                        case YOUTUBE:
                            componGlob.setParam(record);
                            String stParYou = componGlob.getParamValue(vh.nameFieldWithValue);
                            if (stParYou != null && stParYou.length() > 0) {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(stParYou)));
                            }
                            break;
                        case FIELD_WITH_NAME_SCREEN:
                            if (listPresenter != null) {
                                listPresenter.ranCommand(ListPresenter.Command.SELECT,
                                        position, null);
                            } else {
                                componGlob.setParam(record);
                                iBase.startScreen((String) record.getValue(vh.nameFieldScreen), false);
                            }
                            break;
                        case RESULT_RECORD :
                            Intent intent = new Intent();
                            intent.putExtra(Constants.RECORD, record.toString());
                            activity.setResult(Activity.RESULT_OK, intent);
                            activity.finishActivity();
                            break;
                        case RESULT_PARAM :
                            componGlob.setParam(record);
                            activity.setResult(Activity.RESULT_OK);
                            activity.finishActivity();
                            break;
                        case PREFERENCE_SET_VALUE:
                            switch (vh.typePref) {
                                case STRING:
                                    preferences.setNameString(vh.namePreference, vh.pref_value_string);
                                    break;
                                case BOOLEAN:
                                    preferences.setNameBoolean(vh.namePreference, vh.pref_value_boolean);
                                    break;
                            }
                            break;
                        case MODEL_PARAM:
                            selectViewHandler = vh;
                            ParamModel pm = vh.paramModel;
                            switch (pm.method) {
                                case DEL_DB:
                                    WhereParam wp = setWhere(pm.param, record);
                                    if (pm.updateSet != null) {
                                        wp.where = pm.updateSet;
                                    }
                                    if (wp != null) {
                                        baseDB.deleteRecord(iBase, pm, wp.where, wp.param, listener_send_back_screen);
                                    } else {
                                        iBase.logDB("2001 deleteRecord failed in " + multiComponent.nameComponent);
                                    }
                                    break;
                                case UPDATE_DB:
                                    ContentValues cv = setCV(pm.updateSet, record);
                                    wp = setWhere(pm.param, record);
                                    if (pm.updateWhere != null) {
                                        wp.where = pm.updateWhere;
                                    }
                                    if (wp != null && cv != null) {
                                        baseDB.updateRecord(iBase, pm, cv, wp.where, wp.param, listener_send_back_screen);
                                    } else {
                                        iBase.logDB("2001 updateRecord failed in " + multiComponent.nameComponent);
                                    }
                                    break;
                            }
                            break;
                        case ACTUAL:
                            actual();
                            break;
                        case DEL_RECYCLER:
                            listData.remove(position);
                            if (this instanceof RecyclerComponent) {
                                ((RecyclerComponent) this).adapter.notifyItemRemoved(position);
                            }
                            break;
                        case NAME_SCREEN:
                            componGlob.setParam(record);
                            int requestCode = -1;
                            if (vh.afterResponse != null) {
                                requestCode = activity.addForResult(vh.afterResponse, activityResult);
                            }
                            if (vh.paramForScreen == ViewHandler.TYPE_PARAM_FOR_SCREEN.RECORD) {
                                iBase.startScreen(vh.screen, false, record, requestCode);
                            } else {
                                iBase.startScreen(vh.screen, false, requestCode);
                            }
                            break;
                        case CLICK_VIEW:
                            if (holder != null) {
                                if (iCustom != null) {
                                    iCustom.clickView(view, holder.itemView, this, record, position);
                                } else if (moreWork != null) {
                                    moreWork.clickView(view, holder.itemView, this, record, position);
                                }
                            }
                            break;
                        case BACK:
                            iBase.backPressed();
                            break;
                        case EXIT:
                            activity.exitAccount();
                            break;
                        case CLICK_CUSTOM:
                            if (iCustom != null) {
                                iCustom.customClick(paramMV.paramView.viewId, position, record);
                            }
                            break;
                        case BROADCAST:
                            Intent intentBroad = new Intent(vh.nameFieldWithValue);
                            intentBroad.putExtra(Constants.RECORD, record.toString());
                            LocalBroadcastManager.getInstance(activity).sendBroadcast(intentBroad);
                            break;
                        case NEXT_SCREEN_SEQUENCE:
                            int isc = preferences.getSplashScreen();
                            if (isc < 2) {
                                isc ++;
                                preferences.setSplashScreen(isc);
                                String stSc = preferences.getSplashNameScreen();
                                if (stSc.length() > 0) {
                                    String[] stAr = stSc.split(",");
                                    iBase.startScreen(stAr[isc], false);
                                    activity.finish();
                                }
                            } else {
                                activity.finish();
                            }
                            break;
                        case PAGER_PLUS:
                            if (getThis() instanceof PagerVComponent) {
                                ((PagerVComponent) getThis()).pagerPlusItem();
                            }
                            break;
                    }
                }
            }
        }
    }

    public void clickAdapter(RecyclerView.ViewHolder holder, View view, int position, Record record) {
        int id = view == null ? 0 : view.getId();
        clickAdapter1(holder, view, id, position, record);
    }

    private ContentValues setCV(String paramSt, Record rec) {
        ContentValues cv = new ContentValues();
        if (paramSt == null || paramSt.length() == 0) {
            iBase.logDB("2002 no set parameters in " + multiComponent.nameComponent);
            return null;
        }
        String[] param = paramSt.split(",");
        int ik = param.length;
        for (int i = 0; i < ik; i++) {
            String par = param[i];
            String parValue = null;
            if (rec != null) {
                parValue = rec.getString(par);
            }
            if (parValue == null) {
                parValue = getGlobalParam(par);
            }
            if (parValue != null) {
                cv.put(par, parValue);
            } else {
                iBase.logDB("2002 wrong set parameter " + par + " in " + multiComponent.nameComponent);
                return null;
            }
        }
        return cv;
    }

    private WhereParam setWhere(String paramSt, Record rec) {
        WhereParam whereParam = new WhereParam();
        whereParam.where = "";
        whereParam.param = null;
        String sep = "";
        if (paramSt == null || paramSt.length() == 0) {
            iBase.logDB("2002 no where parameters in " + multiComponent.nameComponent);
            return null;
        }
        whereParam.param = paramSt.split(",");
        int ik = whereParam.param.length;
        for (int i = 0; i < ik; i++) {
            String par = whereParam.param[i];
            whereParam.where += sep + par + " = ?";
            sep = ", ";
            String parValue = null;
            if (rec != null) {
                parValue = rec.getString(par);
            }
            if (parValue == null) {
                parValue = getGlobalParam(par);
            }
            if (parValue != null) {
                whereParam.param[i] = parValue;
            } else {
                iBase.logDB("2002 wrong where parameter " + par + " in " + multiComponent.nameComponent);
                return null;
            }
        }
        return whereParam;
    }

    public OnClickItemRecycler clickItem = new OnClickItemRecycler() {
        @Override
        public void onClick(RecyclerView.ViewHolder holder, View view, int position, Record record) {
            clickAdapter(holder, view, position, record);
        }
    };

    public Record setRecord(Record paramRecord) {
        Record rec = new Record();
        for (Field f : paramRecord) {
            if (f.value == null) {
                String st = componGlob.getParamValue(f.name);
                if (st.length() > 0) {
                    rec.add(new Field(f.name, Field.TYPE_STRING, st));
                }
            } else {
                rec.add(new Field(f.name, f.type, f.value));
            }
        }
        return rec;
    }

    IPresenterListener listener_get_data = new IPresenterListener() {
        @Override
        public void onResponse(Field response) {
            if (selectViewHandler != null && selectViewHandler.afterResponse != null) {
                ParamModel parModel = selectViewHandler.paramModel;

                String fName = parModel.nameField;
                String addFieldName = parModel.nameAddField;
                ListRecords listR = null;                   // Вибірка данних під дурню в структурі даних в СМС склад
                if (response.type == Field.TYPE_RECORD) {
                    Field ff = ((Record) response.value).get(0);
                    if (ff.type == Field.TYPE_RECORD) {
                        Field f = ((Record) ff.value).get(0);
                        listR = (ListRecords) f.value;
                    } else {
                        listR = (ListRecords) ff.value;
                    }
                } else {
                    if (response.type == Field.TYPE_LIST_RECORD) {
                        listR = (ListRecords) response.value;
                    }
                }
                if (fName != null || addFieldName != null) {
                    String[] addField = null;
                    if (addFieldName != null && addFieldName.length() > 0) {
                        addField = addFieldName.split(",");
                    }
                    String fNameTo = parModel.nameFieldTo;
                    int addFieldType = parModel.typeAddField;
//                    int addFieldIntValue = parModel.valueAddField;
                    if (listR != null) {
                        for (Record record : listR) {
                            if (fName != null) {
                                Field f = record.getField(fName);
                                if (f != null) {
                                    f.name = fNameTo;
                                }
                            }
                            if (addFieldName != null && addFieldName.length() > 0) {
                                for (String addSt : addField) {
                                    Field ff = new Field(addSt, addFieldType, parModel.valueAddField);
                                    record.add(ff);
                                }
                            }
                        }
                    }
                }
                afterHandler(new Field("", Field.TYPE_LIST_RECORD , listR), selectViewHandler.afterResponse.viewHandlers);
            }
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
            onErrorModel(statusCode, message, click);
        }
    };

    private void onErrorModel(int statusCode, String message, View.OnClickListener click) {
        Record rec = componGlob.formErrorRecord(iBase, statusCode, message);
        if (selectViewHandler != null && selectViewHandler.afterError != null) {
            afterHandler(new Field("", Field.TYPE_RECORD, rec), selectViewHandler.afterError.viewHandlers);
        }
        if (paramMV != null && paramMV.paramModel != null && paramMV.paramModel.errorShowView != 0) {
            View v = parentLayout.findViewById(paramMV.paramModel.errorShowView);
            if (v != null) {
                if (v instanceof AnimatePanel) {
                    ((AnimatePanel) v).show(iBase);
                } else {
                    v.setVisibility(View.VISIBLE);
                }
                workWithRecordsAndViews.RecordToView(rec, v, this, click);
            }
        }
    }

    IPresenterListener listener_send_back_screen = new IPresenterListener() {
        @Override
        public void onResponse(Field response) {
            if (selectViewHandler != null && selectViewHandler.afterResponse != null) {
                afterHandler(response, selectViewHandler.afterResponse.viewHandlers);
            }
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
            onErrorModel(statusCode, message, click);
        }
    };

    public void afterHandler(Field response, List<ViewHandler> viewHandlers) {
        Record rec;
        String st;
        View vv;
        for (ViewHandler vh : viewHandlers) {
            switch (vh.type) {
                case NAME_SCREEN:
                    iBase.startScreen(vh.screen, false);
                    break;
                case SET_TOKEN:
                    if (response.value != null) {
                        rec = ((Record) response.value);
                        st = rec.getString(vh.nameFieldWithValue);
                    } else {
                        st = "";
                        iBase.log("1002 Invalid Token");
                    }
                    if (st != null) {
                        componGlob.token.setValue(new String(st), 0, activity);
                        preferences.setSessionToken(st);
                    }
                    break;
                case SET_PROFILE:
                    if (response.value != null) {
                        rec = ((Record) response.value);
                        if (vh.nameFieldWithValue == null || vh.nameFieldWithValue.length() == 0) {
                            ((Record) componGlob.profile.value).clear();
                            componGlob.profile.setValue(rec, 0, activity);
                            preferences.setProfile(rec.toString());
                        } else {
                            Record prof = (Record) rec.getValue(vh.nameFieldWithValue);
                            if (prof != null) {
//                                componGlob.profile = new FieldBroadcast("profile", Field.TYPE_RECORD, prof);
                                ((Record) componGlob.profile.value).clear();
                                componGlob.profile.setValue(prof, 0, activity);
                                preferences.setProfile(prof.toString());
                            }
                        }
                    }
                    break;
                case RESULT_RECORD :
                    Intent intent = new Intent();
                    if (response.value != null) {
                        intent.putExtra(Constants.RECORD, ((Record) response.value).toString());
                    }
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finishActivity();
                    break;
                case PREFERENCE_SET_NAME:
                    if (response.value != null) {
                        rec = ((Record) response.value);
                        st = rec.getString(vh.nameFieldWithValue);
                        if (st != null) {
                            preferences.setNameString(vh.nameFieldWithValue, st);
                        }
                    }
                    break;
                case SHOW:
                    vv = parentLayout.findViewById(vh.showViewId);
                    if (vv != null) {
                        if (vv instanceof AnimatePanel) {
                            ((AnimatePanel) vv).show(iBase);
                        } else {
                            vv.setVisibility(View.VISIBLE);
                        }
                        if (vh.nameFieldWithValue != null && vh.nameFieldWithValue.length() > 0) {
                            workWithRecordsAndViews.RecordToView(paramToRecord(vh.nameFieldWithValue), vv);
                        }
                    }
                    break;
                case HIDE:
                    vv = parentLayout.findViewById(vh.showViewId);
                    if (vv != null) {
                        if (vv instanceof AnimatePanel) {
                            ((AnimatePanel) vv).hide();
                        } else {
                            vv.setVisibility(View.GONE);
                        }
                    }
                    break;
                case MODEL_PARAM:
                    selectViewHandler = vh;
                    ParamModel pm = vh.paramModel;
                    if (pm.method == DEL_DB) {
                        WhereParam wp = setWhere(pm.param, null);
                        if (wp != null) {
                            baseDB.deleteRecord(iBase, pm, wp.where, wp.param, listener_send_back_screen);
                        } else {
                            iBase.logDB("2001 deleteRecord failed in " + multiComponent.nameComponent);
                        }
//                        baseDB.deleteRecord(iBase, pm, setParam(pm.param, null), null);
                    }
                    break;
                case ACTUAL:
                    if (vh.showViewId == 0) {
                        actual();
                    } else {
                        BaseComponent bc = getComponent(vh.showViewId);
                        if (bc != null) {
                            bc.actual();
                        } else {
                            String stN = activity.getResources().getResourceEntryName(vh.showViewId);
                            iBase.log("0004 Нет компонента с id " + stN + " для актуализации в " + multiComponent.nameComponent);
                        }
                    }
                    break;
                case BACK:
                    iBase.backPressed();
                    break;
                case NEXT_SCREEN_SEQUENCE:
                    int isc = preferences.getSplashScreen();
                    if (isc < 2) {
                        isc ++;
                        preferences.setSplashScreen(isc);
                        String stSc = preferences.getSplashNameScreen();
                        if (stSc.length() > 0) {
                            String[] stAr = stSc.split(",");
                            iBase.startScreen(stAr[isc], false);
                            activity.finish();
                        }
                    } else {
                        activity.finish();
                    }
                    break;
                case UPDATE_DATA:
                    multiComponent.getComponent(vh.viewId).updateData(vh.paramModel);
                    break;
                case SET_GLOBAL:
                    if (response.value != null) {
                        activity.setGlobalData(vh.nameFieldWithValue, Field.TYPE_LIST_RECORD, response.value);
                    }
                    break;
                case ASSIGN_VALUE:
                    vv = parentLayout.findViewById(vh.viewId);
                    if (vv != null) {
                        if (response != null) {
                            workWithRecordsAndViews.RecordToView((Record) response.value, vv);
                        }
                    }
                    break;
                case SWITCH_ON:
                    vv = parentLayout.findViewById(vh.viewId);
                    ((ISwitch) vv).setOn(vh.switchValue);
                    break;
                case SWITCH_ON_STATUS:
                    vv = parentLayout.findViewById(vh.viewId);
                    ((ISwitch) vv).setOnStatus(vh.switchValue);
                    break;
            }
        }
    }

    private Record paramToRecord(String param) {
        Record rec = new Record();
        String[] par = param.split(",");
        if (par.length > 0) {
            for (String nameField : par) {
                String value = componGlob.getParamValue(nameField);
                if (value.length() > 0) {
                    rec.add(new Field(nameField, Field.TYPE_STRING, value));
                }
            }
        }
        return rec;
    }

    IPresenterListener listener_send_change =new IPresenterListener() {
        @Override
        public void onResponse(Field response) {
            if (paramMV.paramModel.nameTakeField == null) {
                paramMV.paramModel.field.value = response.value;
            } else {
                if (response.type == Field.TYPE_RECORD) {
                    paramMV.paramModel.field.setValue(
                            ((Record) response.value).getField(paramMV.paramModel.nameTakeField).value,
                            paramMV.paramView.viewId, activity);
                } else {
                    paramMV.paramModel.field.setValue(response.value, paramMV.paramView.viewId, activity);
                }
            }
            iBase.backPressed();
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
            onErrorModel(statusCode, message, click);
        }
    };

    public ComponTools getComponTools() {
        return componTools;
    }

    private class WhereParam {
        public String where;
        public String[] param;
    }
}
