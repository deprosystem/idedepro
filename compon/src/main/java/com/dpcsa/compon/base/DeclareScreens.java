package com.dpcsa.compon.base;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
//import android.graphics.Color;

import androidx.annotation.NonNull;

import com.dpcsa.compon.interfaces_classes.ActionsAfterError;
import com.dpcsa.compon.interfaces_classes.Animate;
import com.dpcsa.compon.interfaces_classes.Channel;
import com.dpcsa.compon.interfaces_classes.DataFieldGet;
import com.dpcsa.compon.interfaces_classes.ExecMethod;
import com.dpcsa.compon.interfaces_classes.FilterParam;
import com.dpcsa.compon.interfaces_classes.ItemSetValue;
import com.dpcsa.compon.interfaces_classes.ModifierTool;
import com.dpcsa.compon.interfaces_classes.Navigator;
import com.dpcsa.compon.interfaces_classes.Notice;
import com.dpcsa.compon.interfaces_classes.PushHandler;
import com.dpcsa.compon.interfaces_classes.SendAndUpdate;
import com.dpcsa.compon.interfaces_classes.ToolMenu;
import com.dpcsa.compon.param.ParamView;
import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.interfaces_classes.Visibility;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.single.Injector;
import com.dpcsa.compon.tools.Constants;

import java.util.ArrayList;
import java.util.Map;

import static com.dpcsa.compon.interfaces_classes.ItemSetValue.TYPE_SOURCE.GROUPP_PARAM;
import static com.dpcsa.compon.interfaces_classes.PushHandler.TYPE.DRAWER;
import static com.dpcsa.compon.interfaces_classes.PushHandler.TYPE.NULLIFY;
import static com.dpcsa.compon.interfaces_classes.PushHandler.TYPE.SELECT_MENU;
import static com.dpcsa.compon.interfaces_classes.PushHandler.TYPE.SELECT_PAGER;
import static com.dpcsa.compon.interfaces_classes.PushHandler.TYPE.SELECT_RECYCLER;
import static com.dpcsa.compon.interfaces_classes.ViewHandler.TYPE.SET_VALUE;
import static com.dpcsa.compon.interfaces_classes.ViewHandler.TYPE.SET_VALUE_PARAM;

public abstract class DeclareScreens<T>{
    protected ParamComponent.TC TC;
    protected Constants.AnimateScreen AS;
    protected ViewHandler.TYPE VH;
    protected ItemSetValue.TYPE_SOURCE TS;
    protected FilterParam.Operation FO;
    protected ViewHandler.TYPE_PARAM_FOR_SCREEN PS;
    protected int IS_TOKEN = Integer.MAX_VALUE;
    protected int IS_PUSH_TOKEN = IS_TOKEN - 1;

    public abstract void declare();
    protected int GET = ParamModel.GET, POST = ParamModel.POST, JSON = ParamModel.JSON, PROFILE = ParamModel.PROFILE,
            GET_DB = ParamModel.GET_DB, POST_DB = ParamModel.POST_DB, UPDATE_DB = ParamModel.UPDATE_DB,
            INSERT_DB = ParamModel.INSERT_DB, DEL_DB = ParamModel.DEL_DB, PARENT = ParamModel.PARENT,
            FIELD = ParamModel.FIELD, ARGUMENTS = ParamModel.ARGUMENTS, COUNTRY_CODE = ParamModel.COUNTRY_CODE,
            STRINGARRAY = ParamModel.STRINGARRAY, DATAFIELD = ParamModel.DATAFIELD, GLOBAL = ParamModel.GLOBAL;

    private Map<String, Screen> MapScreen;
    protected ComponGlob componGlob;
    protected Context context;

    public DeclareScreens() {
        componGlob = Injector.getComponGlob();
        MapScreen = componGlob.MapScreen;
    }

    protected String getString(int stringId) {
        return context.getResources().getString(stringId);
    }

    protected int getColor(int colorId) {
        return context.getResources().getColor(colorId);
    }

    public void initScreen(Context context) {
        this.context = context;
        declare();
        for (Screen value : MapScreen.values()) {
            String par = value.getParamModel();
            if (par != null && par.length() > 0) {
                String[] param = par.split(Constants.SEPARATOR_LIST);
                int ik = param.length;
                for (int i = 0; i < ik; i++) {
                    componGlob.addParam(param[i]);
                }
            }
        }
        if (componGlob.channels != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) componGlob.context.getSystemService(Context.NOTIFICATION_SERVICE);
            for (Channel chan : componGlob.channels) {
                NotificationChannel channel = new NotificationChannel(chan.id, chan.name, chan.importance);
                if (chan.description != null && chan.description.length() > 0) {
                    channel.setDescription(chan.description);
                }
                channel.enableLights(chan.enableLights);
                if (chan.lightColor != 0) {
                    channel.setLightColor(chan.lightColor);
                }
                channel.enableVibration(chan.enableVibration);
                if (chan.vibrationPattern != null && chan.vibrationPattern.length > 0) {
                    channel.setVibrationPattern(chan.vibrationPattern);
                }
                channel.setShowBadge(chan.sBadge);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public Field getProfile() {
        return Injector.getComponGlob().profile;
    }

    protected Screen fragment(String name, int layoutId, int title) {
        Screen mc = new Screen(name, layoutId);
        mc.typeView = Screen.TYPE_VIEW.FRAGMENT;
        mc.titleId = title;
        mc.args = "";
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen fragment(String name, int layoutId, int title, String formatParams) {
        Screen mc = new Screen(name, layoutId);
        mc.typeView = Screen.TYPE_VIEW.FRAGMENT;
        mc.titleId = title;
        mc.args = formatParams;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen fragment(String name, int layoutId, String title, String formatParams) {
        Screen mc = new Screen(name, layoutId, title, formatParams);
        mc.typeView = Screen.TYPE_VIEW.FRAGMENT;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen fragment(String name, int layoutId, Class<T> additionalWork) {
        Screen mc = new Screen(name, layoutId);
        mc.typeView = Screen.TYPE_VIEW.FRAGMENT;
        mc.additionalWork = additionalWork;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen fragment(String name, int layoutId) {
        Screen mc = new Screen(name, layoutId);
        mc.typeView = Screen.TYPE_VIEW.FRAGMENT;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen fragment(String name, Class customFragment) {
        Screen mc = new Screen(name, customFragment);
        mc.typeView = Screen.TYPE_VIEW.CUSTOM_FRAGMENT;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen activity(String name, Class customActivity) {
        Screen mc = new Screen(name, customActivity);
        mc.typeView = Screen.TYPE_VIEW.CUSTOM_ACTIVITY;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen activity(String name, int layoutId, String title, String formatParams) {
        Screen mc = new Screen(name, layoutId, title, formatParams);
        mc.typeView = Screen.TYPE_VIEW.ACTIVITY;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen activity(String name, int layoutId, int title) {
        Screen mc = new Screen(name, layoutId);
        mc.typeView = Screen.TYPE_VIEW.ACTIVITY;
        mc.titleId = title;
        mc.args = "";
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen activity(String name, int layoutId, int title, String formatParams) {
        Screen mc = new Screen(name, layoutId);
        mc.typeView = Screen.TYPE_VIEW.ACTIVITY;
        mc.titleId = title;
        mc.args = formatParams;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen activity(String name, int layoutId) {
        Screen mc = new Screen(name, layoutId);
        mc.typeView = Screen.TYPE_VIEW.ACTIVITY;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen activity(String name, int layoutId, Class<T> additionalWork) {
        Screen mc = new Screen(name, layoutId);
        mc.typeView = Screen.TYPE_VIEW.ACTIVITY;
        mc.additionalWork = additionalWork;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Channel channel(String idChannel, String name, int importance, Class<T> screen, Notice[] notices) {
        if (componGlob.channels == null) {
            componGlob.channels = new ArrayList<>();
        }
        if (componGlob.notices == null) {
            componGlob.notices = new ArrayList<>();
        }
        int id = componGlob.channels.size();
        for (Notice notice : notices) {
            notice.idChannelInt = id;
            notice.idChannel = idChannel;
            notice.idNotice = componGlob.notices.size();
            notice.screen = screen;
            componGlob.notices.add(notice);
        }
        Channel channel = new Channel(idChannel, name, importance, screen, notices);
        componGlob.channels.add(channel);
        return channel;
    }

    public Notice[] notices(Notice ... notice) {
        return notice;
    }

    public Notice notice(String type) {
        Notice notice = new Notice(type, context);
        return notice;
    }

    public void initialSettings(ViewHandler ... handlers) {
        componGlob.initSettings = handlers;
    }

    public ActionsAfterResponse after(ViewHandler ... handlers) {
        return new ActionsAfterResponse(handlers);
    }

    public ActionsAfterError afterError(Boolean viewErrorDialog, ViewHandler ... handlers) {
        return new ActionsAfterError(viewErrorDialog, handlers);
    }

    public static Visibility[] showManager(Visibility ... args) {
        return args;
    }

    public static Visibility visibility(int viewId, String nameField) {
        return new Visibility(0, viewId, nameField);
    }

    public static Visibility enabled(int viewId, String nameField) {
        return new Visibility(1, viewId, nameField);
    }

    public ParamModel model() {
        return new ParamModel();
    }

    public ParamModel model(int method) {
        return new ParamModel(method);
    }

    public ParamModel model(String url) {
        if (url.equals("PROFILE")) {
            return new ParamModel(PROFILE);
        } else {
            return new ParamModel(url);
        }
    }

    public ParamModel model(int method, String urlOrNameParent) {
        return new ParamModel(method, urlOrNameParent);
    }

    public ParamModel model(int method, String[] urlArray, String param) {
        return new ParamModel(method, urlArray, param);
    }

    public ParamModel model(Field field) {
        return new ParamModel(field);
    }

    public ParamModel model(DataFieldGet dataFieldGet) {
        return new ParamModel(dataFieldGet);
    }

    public ParamModel model(String url, String param) {
        return new ParamModel(url, param);
    }

    public ParamModel model(int method, String table, String set, String param) {
        return new ParamModel(method, table, set, param);
    }

    public ParamModel model(int method, String table, String set, String where, String param) {
        return new ParamModel(method, table, set, where, param);
    }

    public ParamModel model(String url, String param, long duration) {
        return new ParamModel(url, param, duration);
    }

    public ParamModel model(String url, long duration) {
        return new ParamModel(url, "", duration);
    }

    public ParamModel model(int method, String url, String param, long duration) {
        return new ParamModel(method, url, param, duration);
    }

    public ParamModel model(int method, String urlOrNameParent, String paramOrField) {
        return new ParamModel(method, urlOrNameParent, paramOrField);
    }

    public ParamView view(int viewId) {
        return new ParamView(viewId);
    }

    public ParamView view(int viewId, int layoutItemId) {
        return new ParamView(viewId, layoutItemId);
    }

    public ParamView view(int viewId, int layoutItemId, int layoutFurtherId) {
        return new ParamView(viewId, layoutItemId, layoutFurtherId);
    }

    public ParamView view(int viewId, int[] layoutTypeId) {
        return new ParamView(viewId, layoutTypeId);
    }

    public ParamView view(int viewId, String fieldType, int[] layoutTypeId) {
        return new ParamView(viewId, fieldType, layoutTypeId);
    }

    public ParamView view(int viewId, String fieldType, int style) {
        return new ParamView(viewId, fieldType, style);
    }

    public ParamView view(int viewId, String ... screens) {
        return new ParamView(viewId, screens);
    }

    public ParamView view(int viewId, String[] screens, int[] containerId) {
        return new ParamView(viewId, screens, containerId);
    }

    public ParamView view(int viewId, String fieldType, int[] layoutTypeId, int[] layoutFurtherTypeId) {
        return new ParamView(viewId, fieldType, layoutTypeId, layoutFurtherTypeId);
    }

    public int[] images(int ... args) {
        return args;
    }

    public int[] listId(int ... args) {
        return args;
    }

    public Navigator navigator() {
        return new Navigator();
    }

    public Navigator navigator(ViewHandler ... handlers) {
        return new Navigator(handlers);
    }

    public ViewHandler handler(String fieldNameFragment) {
        return new ViewHandler(fieldNameFragment);
    }

    public ViewHandler exit(int viewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.EXIT);
    }

    public ViewHandler start(int viewId, String screen) {
        return new ViewHandler(viewId, screen);
    }

    public ViewHandler start(int viewId, String screen, ActionsAfterResponse afterResponse) {
        ViewHandler vh = new ViewHandler(viewId, screen, afterResponse);
        return vh;
    }

    public ViewHandler start(int viewId, String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen) {
        return new ViewHandler(viewId, screen, paramForScreen);
    }

    public ViewHandler start(String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen) {
        return new ViewHandler(0, screen, paramForScreen);
    }

    public ViewHandler start(String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen, int componId) {
        return new ViewHandler(0, screen, paramForScreen, componId);
    }

    public ViewHandler start(String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen, ActionsAfterResponse afterResponse) {
        return new ViewHandler(0, screen, paramForScreen, afterResponse);
    }

    public ViewHandler start(String screen) {
        return new ViewHandler(0, screen);
    }

    public ViewHandler start(int viewId, String screen, boolean blocked) {
        ViewHandler vh = new ViewHandler(viewId, screen);
        vh.blocked = blocked;
        return vh;
    }

    public ViewHandler setValue(ItemSetValue ... item) {
        ViewHandler vh = new ViewHandler(SET_VALUE);
        vh.itemSetValues = item;
        return vh;
    }

    public ViewHandler setValueParam(int viewId) {
        ViewHandler vh = new ViewHandler(viewId, SET_VALUE_PARAM);
        return vh;
    }

    public ViewHandler addScreen(int viewId, String screen) {
        ViewHandler vh = new ViewHandler(viewId, screen);
        vh.addFragment = true;
        return vh;
    }

    public ViewHandler setMenu(int viewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SET_MENU);
    }

    public ViewHandler setMenu(int viewId, String screen) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SET_MENU, screen);
    }

    public ViewHandler setMenu(int viewId, int position) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SET_MENU, position);
    }

    public ViewHandler setToken() {
        return new ViewHandler(0, ViewHandler.TYPE.SET_TOKEN, "token");
    }

    public ViewHandler setToken(String token) {
        return new ViewHandler(0, ViewHandler.TYPE.SET_TOKEN, token);
    }

    public ViewHandler setProfile() {
        return new ViewHandler(0, ViewHandler.TYPE.SET_PROFILE, "");
    }

    public ViewHandler setProfile(String profile) {
        return new ViewHandler(0, ViewHandler.TYPE.SET_PROFILE, profile);
    }

    public ViewHandler subscribePush(String url) {
        ViewHandler vh = new ViewHandler(0, ViewHandler.TYPE.SUBSCRIBE_PUSH, url);
        vh.blocked = false;
        return vh;
    }

    public ViewHandler subscribePush(String url, boolean auth) {
        ViewHandler vh = new ViewHandler(0, ViewHandler.TYPE.SUBSCRIBE_PUSH, url);
        vh.blocked = auth;
        return vh;
    }

    public ViewHandler handler(int viewId, String screen, ActionsAfterResponse afterResponse) {
        return new ViewHandler(viewId, screen, afterResponse);
    }

    public ViewHandler handler(int viewId, String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen) {
        return new ViewHandler(viewId, screen, paramForScreen);
    }

    public ViewHandler handler(int viewId, String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen, ActionsAfterResponse afterResponse) {
        return new ViewHandler(viewId, screen, paramForScreen, afterResponse);
    }

    public ViewHandler handler(int viewId, String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen,
                               String param, ActionsAfterResponse afterResponse) {
        return new ViewHandler(viewId, screen, paramForScreen, param, afterResponse);
    }

    public ViewHandler handler(int viewId, String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen,
                               String param) {
        return new ViewHandler(viewId, screen, paramForScreen, param);
    }

    public ViewHandler handler(int viewId, String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen, int componId) {
        return new ViewHandler(viewId, screen, paramForScreen, componId);
    }

    public ViewHandler handler(int viewId, ParamModel paramModel) {
        return new ViewHandler(viewId, paramModel);
    }

    public ViewHandler handler(int viewId, ParamModel paramModel, ActionsAfterResponse afterResponse) {
        return new ViewHandler(viewId, ViewHandler.TYPE.MODEL_PARAM, paramModel, afterResponse, false, null);
    }

    public ViewHandler handler(ParamModel paramModel) {
        return new ViewHandler(0, paramModel);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel) {
        return new ViewHandler(viewId, type, paramModel);
    }

// В screen передается Record (в paramModel тип )  ??????????????? уточнить
//    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel, String screen) {
//        return new ViewHandler(viewId, type, paramModel, screen);
//    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel,
                               String screen, boolean changeEnabled, int... mustValid) {
        return new ViewHandler(viewId, type, paramModel, screen, changeEnabled, mustValid);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel,
                               ActionsAfterResponse afterResponse) {
        return new ViewHandler(viewId, type, paramModel, afterResponse, false, null);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel,
                               ActionsAfterResponse afterResponse, boolean changeEnabled, int... mustValid) {
        return new ViewHandler(viewId, type, paramModel, afterResponse, changeEnabled, mustValid);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel,
                               ActionsAfterResponse afterResponse, ActionsAfterError afterError) {
        return new ViewHandler(viewId, type, paramModel, afterResponse, afterError, false, null);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel,
                               ActionsAfterResponse afterResponse, ActionsAfterError afterError, boolean changeEnabled, int... mustValid) {
        return new ViewHandler(viewId, type, paramModel, afterResponse, afterError, changeEnabled, mustValid);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel,
                               ActionsAfterError afterError) {
        return new ViewHandler(viewId, type, paramModel, null, afterError, false, null);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel,
                               ActionsAfterError afterError, boolean changeEnabled, int... mustValid) {
        return new ViewHandler(viewId, type, paramModel, null, afterError, changeEnabled, mustValid);
    }

    public ViewHandler handler(int viewId, ExecMethod execMethod) {
        return new ViewHandler(viewId, execMethod);
    }

    public ViewHandler handler(int viewId, String namePreference, boolean value) {
        return new ViewHandler(viewId, namePreference, value);
    }

    public ViewHandler handler(int viewId, String namePreference, String value) {
        return new ViewHandler(viewId, namePreference, value);
    }

    public ViewHandler back(int viewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.BACK);
    }

    public ViewHandler backOk(int viewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.BACK_OK);
    }

    public ViewHandler springScale(int animViewId, int velocity, int repeatTime) {
        return new ViewHandler(ViewHandler.TYPE.SPR_SCALE, animViewId, velocity, repeatTime);
    }

    public ViewHandler springY(int animViewId, int velocity, int repeatTime) {
        return new ViewHandler(ViewHandler.TYPE.SPR_Y, animViewId, velocity, repeatTime);
    }

    public ViewHandler finishDialog(@NonNull int titleId, @NonNull int messageId) {
        return new ViewHandler(0, ViewHandler.TYPE.FINISH, titleId, messageId);
    }

    public ViewHandler keyBack(int viewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.KEY_BACK);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type) {
        return new ViewHandler(viewId, type);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, String nameParam, String valueParam) {
        return new ViewHandler(viewId, type, nameParam, valueParam);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, int idCompon) {
        return new ViewHandler(viewId, type, idCompon, "");
    }

    public ViewHandler handler(ViewHandler.TYPE type) {
        return new ViewHandler(type);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, String nameFieldWithValue) {
        return new ViewHandler(viewId, type, nameFieldWithValue);
    }

    public ViewHandler handler(int viewId, Animate animate) {
        return new ViewHandler(viewId, animate);
    }

    public ViewHandler assignValue(int viewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.ASSIGN_VALUE);
    }

    public ViewHandler handler(int viewId, SendAndUpdate sendAndUpdate) {
        return new ViewHandler(viewId, sendAndUpdate);
    }

    public ViewHandler startYoutube(int viewId, String param) {
        componGlob.addParam(param);
        return new ViewHandler(viewId, ViewHandler.TYPE.YOUTUBE, param);
    }

    public ViewHandler show(int showViewId) {
        return new ViewHandler(0, ViewHandler.TYPE.SHOW, showViewId);
    }

    public ViewHandler show(int viewId, int showViewId, boolean onActivity) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SHOW, showViewId, onActivity);
    }

    public ViewHandler show(int viewId, int showViewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SHOW, showViewId, false);
    }

    public ViewHandler hide(int showViewId) {
        return new ViewHandler(0, ViewHandler.TYPE.HIDE, showViewId, false);
    }

    public ViewHandler hide(int viewId, int showViewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.HIDE, showViewId, false);
    }

    public ViewHandler actual(int viewId, int showViewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.ACTUAL, showViewId, false);
    }

    public ViewHandler switchOn(int viewId, boolean value) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SWITCH_ON, value);
    }

    public ViewHandler switchOnStatus(int viewId, boolean value) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SWITCH_ON_STATUS, value);
    }

    public ViewHandler actual(int showViewId) {
        return new ViewHandler(0, ViewHandler.TYPE.ACTUAL, showViewId, false);
    }

    public ViewHandler actual() {
        return new ViewHandler(0, ViewHandler.TYPE.ACTUAL);
    }

    public ViewHandler showHide(int viewId, int showViewId, int textShowId, int textHideId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SHOW_HIDE, showViewId, textShowId, textHideId);
    }

    public ViewHandler handler(int viewId, int idTextV, int idString) {
        return new ViewHandler(viewId, SET_VALUE, idTextV, idString);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, int idCompon, String name) {
        return new ViewHandler(viewId, type, idCompon, name);
    }

    public Animate alpha(int viewId, float par2, int duration) {
        return new Animate(viewId, Animate.TYPE.ALPHA, par2, duration);
    }

    public Animate scale(int viewId, float par1, float par2, int duration) {
        return new Animate(viewId, Animate.TYPE.SCALE, par1, par2, duration);
    }

    public Animate translation(int viewId, float par1, float par2, int duration) {
        return new Animate(viewId, Animate.TYPE.TRANSL, par1, par2, duration);
    }

    public Animate rotate(int viewId, float par2, int duration) {
        return new Animate(viewId, Animate.TYPE.ROTATE, par2, duration);
    }

    public Animate set(Animate ... animates) {
        return new Animate(Animate.TYPE.SET, animates);
    }

    public ItemSetValue item(int viewId, ItemSetValue.TYPE_SOURCE source) {
        return new ItemSetValue(viewId, source);
    }

    public ItemSetValue item(int viewId, ItemSetValue.TYPE_SOURCE source, String name) {
        return new ItemSetValue(viewId, source, name);
    }

    public ItemSetValue item(int viewId, ItemSetValue.TYPE_SOURCE source, int componId) {
        return new ItemSetValue(viewId, source, componId);
    }

    public ItemSetValue itemParam(int viewId) {
        return new ItemSetValue(viewId, GROUPP_PARAM);
    }

    public FilterParam filter(String nameField, FilterParam.Operation oper, Object value) {
        return new FilterParam(nameField, oper, value);
    }

    public PushHandler drawer() {
        return new PushHandler(0, DRAWER, null);
    }

    public PushHandler drawer(String[] pushName) {
        return new PushHandler(0, DRAWER, pushName);
    }

    public PushHandler selectMenu(int viewId, String pushType, String screen) {
        return new PushHandler(viewId, SELECT_MENU, pushType, screen);
    }

    public PushHandler selectMenu(int viewId, String pushType, String screen, boolean continuePush) {
        return new PushHandler(viewId, SELECT_MENU, pushType, screen, continuePush);
    }

    public PushHandler selectPager(int viewId, String pushType, String screen, boolean continuePush) {
        return new PushHandler(viewId, SELECT_PAGER, pushType, screen, continuePush);
    }

    public PushHandler selectRecycler(int viewId, String pushType, String nameField, int handlerId, boolean continuePush) {
        return new PushHandler(viewId, SELECT_RECYCLER, pushType, nameField, handlerId, continuePush);
    }

    public PushHandler nullifyCountPush(String pushType) {
        return new PushHandler(0, NULLIFY, pushType, "", 0, false);
    }

    public ModifierTool visible(int ... args) {
        return new ModifierTool(ModifierTool.TYPE_MODIF.VISIBLE, args);
    }

    public ModifierTool unVisible(int ... args) {
        return new ModifierTool(ModifierTool.TYPE_MODIF.UN_VISIBLE, args);
    }

    public ModifierTool addMenu(ToolMenu toolMenu) {
        return new ModifierTool(toolMenu);
    }
}
