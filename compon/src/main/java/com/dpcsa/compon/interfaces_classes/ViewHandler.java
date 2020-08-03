package com.dpcsa.compon.interfaces_classes;

import android.util.Log;

import com.dpcsa.compon.param.ParamModel;

public class ViewHandler {
    public int viewId;
    public enum TYPE {NAME_SCREEN, CLOSE_DRAWER, OPEN_DRAWER, MODEL_PARAM, KEY_BACK, SET_TOKEN,
        BACK, BACK_MES, PREFERENCE_SET_VALUE, PAGER_PLUS, PREFERENCE_SET_NAME, FINISH, SET_PUSH_TOKEN,
        FIELD_WITH_NAME_SCREEN, SELECT, SET_PARAM, EXEC, UPDATE_DATA, RESULT_PARAM, NEXT_SCREEN_SEQUENCE,
        RESULT_RECORD, ASSIGN_VALUE, SET_VALUE, SET_LOCALE, SET_GLOBAL, SET_MENU, SET_MENU_DEF, EXIT,
        CLICK_VIEW, MAP_ROUTE, SHOW, HIDE, SHOW_HIDE, BROADCAST, RECEIVER, CLICK_CUSTOM, DEL_RECYCLER,
        CLICK_SEND, SEND_UPDATE, SWITCH_ON, SWITCH_ON_STATUS, ANIMATE, YOUTUBE, SUBSCRIBE_PUSH,
        ACTUAL, GET_DATA, CALL_UP, DIAL_UP, SET_PROFILE}
    public TYPE type;
    public String nameFieldScreen;
    public String screen;
    public ParamModel paramModel;
    public SendAndUpdate sendAndUpdate;
    public enum TYPE_PREFER {BOOLEAN, STRING};
    public TYPE_PREFER typePref;
    public enum TYPE_PARAM_FOR_SCREEN {NONE, RECORD, LIST_RECORD, RECORD_COMPONENT};
    public TYPE_PARAM_FOR_SCREEN paramForScreen = TYPE_PARAM_FOR_SCREEN.NONE;
    public boolean pref_value_boolean;
    public String pref_value_string;
    public String namePreference;
    public int[] mustValid;
    public int showViewId, idString, componId;
    public boolean changeEnabled, switchValue;
    public boolean[] validArray;
    public String nameFieldWithValue, paramForSend;
    public ActionsAfterResponse afterResponse;
    public ActionsAfterError afterError;
    public ExecMethod execMethod;
    public boolean onActivity;
    public boolean addFragment, blocked;
    public int textShowId, textHideId;
    public Animate animate;

    public ViewHandler(String nameField) {
        type = TYPE.FIELD_WITH_NAME_SCREEN;
        this.viewId = 0;
        this.nameFieldScreen = nameField;
    }

    public ViewHandler(TYPE type) {
        this.type = type;
        this.viewId = 0;
    }

    public ViewHandler(int viewId, String screen) {
        type = TYPE.NAME_SCREEN;
        paramForScreen = TYPE_PARAM_FOR_SCREEN.NONE;
        this.viewId = viewId;
        this.screen = screen;
    }

    public ViewHandler(int viewId, String screen, ActionsAfterResponse afterResponse) {
        type = TYPE.NAME_SCREEN;
        paramForScreen = TYPE_PARAM_FOR_SCREEN.NONE;
        this.afterResponse = afterResponse;
        this.viewId = viewId;
        this.screen = screen;
    }

    public ViewHandler(int viewId, ExecMethod execMethod) {
        type = TYPE.EXEC;
        paramForScreen = TYPE_PARAM_FOR_SCREEN.NONE;
        this.viewId = viewId;
        this.execMethod = execMethod;
    }

    public ViewHandler(int viewId, String screen, TYPE_PARAM_FOR_SCREEN paramForScreen) {
        type = TYPE.NAME_SCREEN;
        this.paramForScreen = paramForScreen;
        this.viewId = viewId;
        this.screen = screen;
    }

    public ViewHandler(int viewId, String screen, TYPE_PARAM_FOR_SCREEN paramForScreen, int componId) {
        type = TYPE.NAME_SCREEN;
        this.paramForScreen = paramForScreen;
        this.viewId = viewId;
        this.componId = componId;
        this.screen = screen;
    }

    public ViewHandler(int viewId, String screen, TYPE_PARAM_FOR_SCREEN paramForScreen,
                       ActionsAfterResponse afterResponse) {
        type = TYPE.NAME_SCREEN;
        this.paramForScreen = paramForScreen;
        this.afterResponse = afterResponse;
        this.viewId = viewId;
        this.screen = screen;
    }

    public ViewHandler(int viewId, String screen, TYPE_PARAM_FOR_SCREEN paramForScreen,
                       String param, ActionsAfterResponse afterResponse) {
        type = TYPE.NAME_SCREEN;
        this.paramForScreen = paramForScreen;
        this.afterResponse = afterResponse;
        this.viewId = viewId;
        paramForSend = param;
        this.screen = screen;
    }

    public ViewHandler(int viewId, String screen, TYPE_PARAM_FOR_SCREEN paramForScreen, String param) {
        type = TYPE.NAME_SCREEN;
        this.paramForScreen = paramForScreen;
        this.viewId = viewId;
        paramForSend = param;
        this.screen = screen;
    }

    public ViewHandler(int viewId, ParamModel paramModel) {
        type = TYPE.MODEL_PARAM;
        this.viewId = viewId;
        this.paramModel = paramModel;
    }

    public ViewHandler(int viewId, TYPE type, int showViewId) {
        this.type = type;
        this.viewId = viewId;
        this.showViewId = showViewId;
    }

    public ViewHandler(int viewId, TYPE type, int showViewId, int textShowId, int textHideId) {
        this.type = type;
        this.viewId = viewId;
        this.showViewId = showViewId;
        this.textShowId = textShowId;
        this.textHideId = textHideId;
    }

    public ViewHandler(int viewId, TYPE type, int showViewId, boolean onActivity) {
        this.type = type;
        this.viewId = viewId;
        this.onActivity = onActivity;
        this.showViewId = showViewId;
    }

    public ViewHandler(int viewId, TYPE type, boolean value) {
        this.type = type;
        this.viewId = viewId;
        switchValue = value;
    }

    public ViewHandler(int viewId, TYPE type, int idTextV, int idString) {
        this.type = type;
        this.viewId = viewId;
        this.showViewId = idTextV;
        this.idString = idString;
    }

    public ViewHandler(int viewId, TYPE type, int idCompon, String name) {
        this.type = type;
        this.viewId = viewId;
        this.componId = idCompon;
        this.nameFieldWithValue = name;
    }

    public ViewHandler(int viewId, TYPE type, ParamModel paramModel) {
        this.type = type;
        this.viewId = viewId;
        this.paramModel = paramModel;
    }

    public ViewHandler(int viewId, TYPE type, ParamModel paramModel, String screen) {
        this.type = type;
        this.viewId = viewId;
        this.screen = screen;
        this.paramModel = paramModel;
    }

    public ViewHandler(int viewId, TYPE type, ParamModel paramModel,
                       String screen, boolean changeEnabled, int... mustValid) {
        this.type = type;
        this.viewId = viewId;
        this.screen = screen;
        this.mustValid = mustValid;
        this.changeEnabled = changeEnabled;
        this.paramModel = paramModel;
    }

    public ViewHandler(int viewId, TYPE type, ParamModel paramModel,
                       ActionsAfterResponse afterResponse, boolean changeEnabled, int... mustValid) {
        this.type = type;
        this.viewId = viewId;
        this.afterResponse = afterResponse;
        this.mustValid = mustValid;
        this.changeEnabled = changeEnabled;
        this.paramModel = paramModel;
    }

    public ViewHandler(int viewId, TYPE type, ParamModel paramModel,
                       ActionsAfterResponse afterResponse, ActionsAfterError afterError,
                       boolean changeEnabled, int... mustValid) {
        this.type = type;
        this.viewId = viewId;
        this.afterResponse = afterResponse;
        this.afterError = afterError;
        this.mustValid = mustValid;
        this.changeEnabled = changeEnabled;
        if (afterError != null) {
            paramModel.viewErrorDialog = afterError.viewErrorDialog;
        }
        this.paramModel = paramModel;
    }

    public ViewHandler(int viewId, String namePreference, boolean value) {
        this.type = TYPE.PREFERENCE_SET_VALUE;
        this.viewId = viewId;
        this.namePreference = namePreference;
        typePref = TYPE_PREFER.BOOLEAN;
        pref_value_boolean = value;
    }

    public ViewHandler(int viewId, String namePreference, String value) {
        this.type = TYPE.PREFERENCE_SET_VALUE;
        this.viewId = viewId;
        this.namePreference = namePreference;
        typePref = TYPE_PREFER.STRING;
        pref_value_string = value;
    }

    public ViewHandler(int viewId, TYPE type) {
        this.type = type;
        this.viewId = viewId;
        this.paramModel = null;
    }

    public ViewHandler(int viewId, TYPE type, String nameFieldWithValue) {
        this.type = type;
        this.viewId = viewId;
        this.nameFieldWithValue = nameFieldWithValue;
        this.paramModel = null;
    }

    public ViewHandler(int viewId, TYPE type, String nameParam, String valueParam) {
        this.type = type;
        this.viewId = viewId;
        this.nameFieldWithValue = nameParam;
        this.pref_value_string = valueParam;
    }

    public ViewHandler(int viewId, SendAndUpdate sendAndUpdate) {
        type = TYPE.SEND_UPDATE;
        this.viewId = viewId;
        this.sendAndUpdate = sendAndUpdate;
    }

    public ViewHandler(int viewId, Animate animate) {
        this.animate = animate;
        this.viewId = viewId;
        type = TYPE.ANIMATE;
    }
}
