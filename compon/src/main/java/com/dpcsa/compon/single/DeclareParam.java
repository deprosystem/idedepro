package com.dpcsa.compon.single;

import android.content.Context;
import android.util.Log;

import com.dpcsa.compon.R;
import com.dpcsa.compon.base.BaseDB;
import com.dpcsa.compon.base.DeclareScreens;
import com.dpcsa.compon.param.AppParams;
import com.dpcsa.compon.param.ParamModel;

public class DeclareParam {

    private static DeclareParam dp;
    private Context context;
    private ComponGlob componGlob;
    private ComponPrefTool preferences;
    private ComponTools componTools;

    public DeclareParam(Context context) {
        this.context = context;
        Injector.initInjector(context);
        componGlob = Injector.getComponGlob();
        preferences = Injector.getPreferences();
        componTools = Injector.getComponTools();
    }
    
    public static DeclareParam build(Context context) {
        if (dp == null) {
            dp = new DeclareParam(context);
        }
        return dp;
    }

    public DeclareParam setAppParams(AppParams params) {
        componGlob.appParams = params;
        if (params.defaultMethod != ParamModel.GET) {
            ParamModel.setDefaultMethod(params.defaultMethod);
        }
        if (params.initialLanguage.length() > 0) {
            setLocale(params.initialLanguage);
        }
        if (params.nameTokenPush != null && params.nameTokenPush.length() > 0) {
            componGlob.addParam(params.nameTokenPush);
            componGlob.setParamValue(params.nameTokenPush, preferences.getPushToken());
        }
        if (params.errorDialogViewId == 0 && params.errorDialogLayoutId == 0 && params.classErrorDialog == null) {
            params.errorDialogLayoutId = R.layout.dialog_error_def;
            params.errorDialogNegativeId = R.id.cancel;
            params.errorDialogPositiveId = R.id.exit;
        }
        if (params.nameTokenPushInHeader == null) {
            params.nameTokenPushInHeader = "";
        }
        return this;
    }

    public DeclareParam setDeclareScreens(DeclareScreens declareScreens) {
        declareScreens.initScreen(context);
        return this;
    }

    public DeclareParam addParam(String name, String value) {
        componGlob.addParamValue(name, value);
        return this;
    }

    public void setLocale(String locale) {
        String loc = preferences.getLocale();
        if (loc.length() == 0 ) {
            loc = locale;
            preferences.setLocale(locale);
        }
        componGlob.addParamValue(componGlob.appParams.nameLanguageInParam, loc);
    }

    public DeclareParam setDB(BaseDB baseDB) {
        Injector.setBaseDB(baseDB);
        return this;
    }

    public ComponTools getComponTools() {
        return componTools;
    }
}
