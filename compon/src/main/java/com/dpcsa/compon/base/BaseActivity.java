package com.dpcsa.compon.base;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import jp.wasabeef.glide.transformations.BlurTransformation;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dpcsa.compon.components.MenuBComponent;
import com.dpcsa.compon.components.MenuBottomComponent;
import com.dpcsa.compon.components.MenuComponent;
import com.dpcsa.compon.components.PagerFComponent;
import com.dpcsa.compon.components.RecyclerComponent;
import com.dpcsa.compon.components.ToolBarComponent;
import com.dpcsa.compon.custom_components.ComponImageView;
import com.dpcsa.compon.dialogs.ErrorDialog;
import com.dpcsa.compon.dialogs.ProgressDialog;
import com.dpcsa.compon.glide.GlideApp;
import com.dpcsa.compon.glide.GlideRequest;
import com.dpcsa.compon.interfaces_classes.Animate;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.ItemSetValue;
import com.dpcsa.compon.interfaces_classes.PushHandler;
import com.dpcsa.compon.interfaces_classes.SingleSetting;
import com.dpcsa.compon.interfaces_classes.SpringScale;
import com.dpcsa.compon.interfaces_classes.SubscribePush;
import com.dpcsa.compon.param.AppParams;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.single.DeclareParam;
import com.dpcsa.compon.single.Injector;
import com.google.android.gms.common.api.GoogleApiClient;

import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.R;
import com.dpcsa.compon.dialogs.DialogTools;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.ActivityResult;
import com.dpcsa.compon.interfaces_classes.AnimatePanel;
import com.dpcsa.compon.interfaces_classes.EventComponent;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.ICustom;
import com.dpcsa.compon.interfaces_classes.OnResumePause;
import com.dpcsa.compon.interfaces_classes.Param;
import com.dpcsa.compon.interfaces_classes.ParentModel;
import com.dpcsa.compon.interfaces_classes.PermissionsResult;
import com.dpcsa.compon.interfaces_classes.RequestActivityResult;
import com.dpcsa.compon.interfaces_classes.RequestPermissionsResult;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.JsonSimple;
import com.dpcsa.compon.json_simple.JsonSyntaxException;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.json_simple.SimpleRecordToJson;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.tools.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.view.View.inflate;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.circleCropTransform;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;
import static com.dpcsa.compon.interfaces_classes.ItemSetValue.TYPE_SOURCE.GROUPP_PARAM;
import static com.dpcsa.compon.interfaces_classes.ItemSetValue.TYPE_SOURCE.PARAM;

public abstract class BaseActivity extends FragmentActivity implements IBase {

    public Map<String, Screen> mapFragment;
    public List<BaseInternetProvider> listInternetProvider;
    public List<EventComponent> listEvent;
    public View parentLayout;
    public Screen mComponent;
    public int containerFragmentId;
    public List<ParentModel> parentModelList;
    public DrawerLayout drawer;
    public BaseFragment drawerFragment;
    public ComponGlob componGlob;
    public String TAG, TAG_DB, TAG_NET;
    public List<RequestActivityResult> activityResultList;
    public List<RequestPermissionsResult> permissionsResultList;
    public Field paramScreen;
    public WorkWithRecordsAndViews workWithRecordsAndViews;
    public Record paramScreenRecord;
    public List<OnResumePause> resumePauseList;
    public MenuComponent menuDraw;
    public MenuBComponent menuBottom;
    public ToolBarComponent toolBar;

//    public boolean isFullScreen = false;
    private DialogFragment progressDialog;
    private int countProgressStart;
    private boolean isActive;
    private Bundle savedInstanceState;
    private GoogleApiClient googleApiClient;
    private List<AnimatePanel> animatePanelList;
    private ComponPrefTool preferences;
    private JsonSimple jsonSimple = new JsonSimple();
    private List<String> nameGlobalData;
    private String phoneDial;
    private final int CALL_PHONE_REQUEST = 10101;
    private ViewHandler vhFinish;
    private String nameScreen;
    private Intent intent;
    private List<SingleSetting> singleSettingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;
        parentModelList = new ArrayList<>();
        preferences = Injector.getPreferences();
        componGlob = Injector.getComponGlob();
        if (componGlob == null) {
            DeclareParam.build(this)
                    .setAppParams(new AppParams() {
                        @Override
                        public void setParams() {

                        }
                    });
            componGlob = Injector.getComponGlob();
            preferences = Injector.getPreferences();
            TAG = componGlob.appParams.NAME_LOG_APP;
            log("0001 Library is not initialized.");
            return;
        } else {
            TAG = componGlob.appParams.NAME_LOG_APP;
            TAG_DB = componGlob.appParams.NAME_LOG_DB;
            TAG_NET = componGlob.appParams.NAME_LOG_NET;
        }

        mapFragment = componGlob.MapScreen;
        nameGlobalData = new ArrayList<>();
        animatePanelList = new ArrayList<>();
        activityResultList = null;
        permissionsResultList = null;
        countProgressStart = 0;
        listInternetProvider = new ArrayList<>();
        listEvent = new ArrayList<>();

        workWithRecordsAndViews = new WorkWithRecordsAndViews();
        String paramJson = intent.getStringExtra(Constants.NAME_PARAM_FOR_SCREEN);
        if (paramJson != null && paramJson.length() >0) {
            try {
                paramScreen = jsonSimple.jsonToModel(paramJson);
                paramScreenRecord = (Record) paramScreen.value;
            } catch (JsonSyntaxException e) {
                log(e.getMessage());
                e.printStackTrace();
            }
        }
        String st = componGlob.appParams.nameLanguageInHeader;
        if ((st != null && st.length() > 0) || componGlob.appParams.nameLanguageInURL) {
            setLocale();
        }
        if (componGlob.initSettings != null && componGlob.countSettings < componGlob.initSettings.length) {
            initSettings();
        }
        nameScreen = getNameScreen();
        if (nameScreen == null) {
            nameScreen = intent.getStringExtra(Constants.NAME_MVP);
        }
        String nameScreenPush = getNameScreenPush();
        if (nameScreenPush != null && nameScreenPush.length() > 0 && ! nameScreenPush.equals(nameScreen)) {
            String typePush = intent.getStringExtra(Constants.PUSH_TYPE);
            Record rec = new Record();
            rec.add(new Field(Constants.SMPL_PUSH_TYPE, Field.TYPE_STRING, typePush));
            String dd = intent.getStringExtra(Constants.PUSH_DATA);
            if (dd != null && dd.length() > 0) {
                rec.add(new Field(Constants.SMPL_PUSH_DATA, Field.TYPE_STRING, dd));
            }
            Field ff = new Field("intent", Field.TYPE_RECORD, rec);
            startScreen(nameScreenPush, false, ff);
            handlerStart.postDelayed(fin, 100);
        } else {
            if (nameScreen != null && nameScreen.length() > 0) {
                mComponent = getComponent(nameScreen);
                if (mComponent == null) {
                    log("0002 No description of the activity " + nameScreen);
                    return;
                }
                if (mComponent.isFullScreen) {
                    setFullScreen();
                }
                if (mComponent.typeView == Screen.TYPE_VIEW.CUSTOM_ACTIVITY) {
                    parentLayout = inflate(this, getLayoutId(), null);
                } else {
                    parentLayout = inflate(this, mComponent.fragmentLayoutId, null);
                }
            } else {    // Такого не може бути ???????
                int layoutId = getLayoutId();
                if (layoutId != 0) {
                    parentLayout = inflate(this, layoutId, null);
                } else {
                    log("getLayoutId = 0 " + nameScreen);
                    return;
                }
            }
            if (nameScreen != null) {
                setContentView(parentLayout);
                if (mComponent.navigator != null) {
                    for (ViewHandler vh : mComponent.navigator.viewHandlers) {
                        if (vh.viewId != 0) {
                            View v = findViewById(vh.viewId);
                            if (v != null) {
                                v.setOnClickListener(navigatorClick);
                            }
                        }
                        if (vh.type == ViewHandler.TYPE.FINISH) {
                            vhFinish = vh;
                        }
                    }
                }
                mComponent.initComponents(this);
                if (mComponent.moreWork != null) {
                    mComponent.moreWork.startScreen();
                }
            }

            if (this instanceof ICustom) {
                mComponent.setCustom((ICustom) this);
            }
            if (mComponent.title != null || mComponent.titleId != 0) {
                setTitle();
            }

            if (toolBar != null) {
//                stackChanged.onBackStackChanged();
                getSupportFragmentManager().addOnBackStackChangedListener(stackChanged);
                toolBar.showView(getSupportFragmentManager().getBackStackEntryCount() <= 1);
            }
            isActive = true;
            initView();
            setValue();
            ifPush(intent);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mComponent.startNavig != null) {
            clickNavigat(null, 0, mComponent.startNavig.viewHandlers);
        }
    }

    public void setFullScreen() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public boolean isDrawerComponent() {
        return mComponent.isComponentByType(ParamComponent.TC.DRAWER);
    }

    Handler handlerStart = new Handler();

    Runnable fin = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    private void initSettings() {
        if (singleSettingList == null) {
            singleSettingList = new ArrayList<>();
        } else {
            singleSettingList.clear();
        }
        componGlob.countSettings = 0;
        for (ViewHandler vh : componGlob.initSettings) {
            SingleSetting ss = new SubscribePush(this, vh);
            ss.set();
            singleSettingList.add(ss);
        }
    }

    public void setTitle() {
        String txtTit;
        if (mComponent.titleId != 0) {
            mComponent.title = getResources().getString(mComponent.titleId);
        }
        if (mComponent.args != null && mComponent.args.length() > 0) {
            txtTit = String.format(mComponent.title, setFormatParam(mComponent.args.split(",")));
        } else {
            txtTit = mComponent.title;
        }
        setTitle(txtTit);
    }

    public void setTitle(String  txtTit) {
        if (toolBar != null) {
            toolBar.setTitle(txtTit);
        } else {
            TextView titV = (TextView) componGlob.findViewByName(parentLayout, "title");
            if (titV != null) {
                titV.setText(txtTit);
            }
        }
    }

    private String getNameScreenPush() {
        if (nameScreen != null && nameScreen.length() > 0) {
            String typePush = intent.getStringExtra(Constants.PUSH_TYPE);
            if (typePush != null && typePush.length() > 0) {
                return componGlob.getNameScreenNotice(typePush);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ifPush(intent);
    }

    private void ifPush(Intent intent_1) {
        String type = intent_1.getStringExtra(Constants.SMPL_PUSH_TYPE);
        Bundle b = intent_1.getExtras();
        if (type != null && type.length() > 0) {
            String dat = intent_1.getStringExtra(Constants.SMPL_PUSH_DATA);
            if (dat == null) {
                dat = "";
            }
            preferences.setPushData(dat);
            preferences.setPushType(type);
            processingPush(type, dat);
        }
    }

    public void processingPush(String typePush, String dataPush) {
        if (mComponent != null && mComponent.pushNavigator != null) {
            for (PushHandler push : mComponent.pushNavigator.pushHandlers) {
                switch (push.type) {
                    case DRAWER:
                        if (push.pushName == null || inType(typePush, push.pushName)) {
                            if (drawer != null) {
                                drawerFragment.runPush();
                            }
                        }
                        break;
                    case SELECT_PAGER:
                        if (push.pushType.equals(typePush)) {
                            BaseComponent bc = mComponent.getComponent(push.viewId);
                            PagerFComponent pc = null;
                            if (bc != null && bc instanceof PagerFComponent) {
                                pc = (PagerFComponent) bc;
                                if (!push.continuePush) {
                                    preferences.setPushType("");
                                }
                                pc.selectFragment(push.screen);
                            } else {
                                log("Компонент не PagerFComponent в " + mComponent.nameComponent);
                            }
                        }
                        break;
                    case SELECT_MENU:
                        if (push.pushType.equals(typePush)) {
                            BaseComponent bc = mComponent.getComponent(push.viewId);
                            MenuComponent mc = null;
                            if (bc != null && bc instanceof MenuComponent) {
                                mc = (MenuComponent) bc;
                                if (!push.continuePush) {
                                    preferences.setPushType("");
                                }
                                mc.selectPunct(push.screen);
                            } else {
                                if (bc != null && bc instanceof MenuBottomComponent) {
                                    MenuBottomComponent mbc = (MenuBottomComponent) bc;
                                    if (!push.continuePush) {
                                        preferences.setPushType("");
                                    }
                                    mbc.selectPunct(push.screen);
                                } else {
                                    log("Компонент не MenuComponent в " + mComponent.nameComponent);
                                }
                            }
                        }
                        break;
                    case SELECT_RECYCLER:
                        if (push.pushType.equals(typePush)) {
                            BaseComponent bc = mComponent.getComponent(push.viewId);
                            RecyclerComponent rc = null;
                            if (bc != null && bc instanceof RecyclerComponent) {
                                rc = (RecyclerComponent) bc;
                                if (!push.continuePush) {
                                    preferences.setPushType("");
                                }
                                rc.selectItem(push);
                            } else {
                                log("Компонент не RecyclerComponent в " + mComponent.nameComponent);
                            }
                        }
                        break;
                    case NULLIFY:
                        componGlob.nullifyValue(push.pushType);
                        break;
                }
            }
        }
    }

    @Override
    public PushHandler getPusHandler(PushHandler.TYPE pushType, int viewId) {
        String pushPref = preferences.getPushType();
        if (mComponent.pushNavigator != null && pushPref != null && pushPref.length() > 0) {
            for (PushHandler push : mComponent.pushNavigator.pushHandlers) {
                if (push.type == pushType && viewId == push.viewId) {
                    return push;
                }
            }
        }
        return null;
    }

    @Override
    public void startPush() {

    }

    private boolean inType(String type, String[] names) {
        for (String st : names) {
            if (type.equals(st)) {
                return true;
            }
        }
        return false;
    }

    public int getCountParam(String name) {
        int l = name.length();
        int i = 0;
        int j = name.indexOf("%");
        while (j > -1) {
            i++;
            j++;
            if (j < l) {
                j = name.indexOf("%", j);
            } else {
                j = -1;
            }
        }
        return i;
    }

    FragmentManager.OnBackStackChangedListener stackChanged = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            toolBar.showView(getSupportFragmentManager().getBackStackEntryCount() <= 1);
        }
    };

    public void setValue() {
        if (mComponent != null && mComponent.itemSetValues != null) {
            for (ItemSetValue sv : mComponent.itemSetValues) {
                if (sv.type == GROUPP_PARAM) {
                    setValueParam(sv.viewId);
                    continue;
                }
                View v = parentLayout.findViewById(sv.viewId);
                if (v != null && v instanceof TextView) {
                    switch (sv.type) {
                        case PARAM:
                            String name = sv.name;
                            if (name == null) {
                                name = getResources().getResourceEntryName(sv.viewId);
                            }
                            if (v instanceof TextView) {
                                ((TextView) v).setText(componGlob.getParamValue(name));
                            } else if (v instanceof IComponent) {
                                ((IComponent) v).setData(componGlob.getParamValue(name));
                            }
                            break;
                        case LOCALE:
                            if (v instanceof TextView) {
                                ((TextView) v).setText(preferences.getLocale());
                            } else if (v instanceof IComponent) {
                                ((IComponent) v).setData(preferences.getLocale());
                            }
                            break;
                    }
                } else if (v != null && v instanceof ImageView) {
                    if (sv.type == PARAM) {
                        String name = sv.name;
                        if (name == null) {
                            name = getResources().getResourceEntryName(sv.viewId);
                        }
                        GlideRequest gr = GlideApp.with(this).load(componGlob.getParamValue(name));
                        if (v instanceof ComponImageView) {
                            ComponImageView simg = (ComponImageView) v;
                            if (simg.getBlur() > 0) {
                                gr.apply(bitmapTransform(new BlurTransformation(simg.getBlur())));
                            }
                            if (simg.getPlaceholder() > 0) {
                                gr.apply(placeholderOf(simg.getPlaceholder()));
                            }
                            if (simg.isOval()) {
                                gr.apply(circleCropTransform());
                            }
                        }
                        gr.into((ImageView) v);
                    }
                }
            }
        }
    }

    @Override
    public void setToolBar(ToolBarComponent toolBar) {
        this.toolBar = toolBar;
    }

    @Override
    public void itemSetValue(int viewId, Object value) {
        if (mComponent.itemSetValues != null) {
            for (ItemSetValue sv : mComponent.itemSetValues) {
                if (sv.componId == viewId) {
                    switch (sv.type) {
                        case SIZE:
                            View v = parentLayout.findViewById(sv.viewId);
                            if (v != null) {
                                if (v instanceof IComponent) {
                                    ((IComponent) v).setData(value);
                                } else if (v instanceof TextView){
                                    ((TextView) v).setText((Integer) value);
                                }
                            }
                            break;
                    }
                }
            }
        }
    }

    public void setLocale() {
        String loc = preferences.getLocale();
        if (loc.length() == 0) {
            loc = "uk";
        }
        if (loc.equals(Locale.getDefault().getLanguage())) return;
        Locale myLocale = new Locale(loc);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    @Override
    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    public String getNameScreen() {
        return null;
    }

    public Screen getScreen() {
        return null;
    }

    public int getLayoutId() {
        return 0;
    }

    public void initView() {

    }

    public void addPermissionsResult(int requestCode, PermissionsResult permissionsResult) {
        if (permissionsResultList == null) {
            permissionsResultList = new ArrayList<>();
        }
        permissionsResultList.add(new RequestPermissionsResult(requestCode, permissionsResult));
    }

    public int addForResult(ActionsAfterResponse afterResponse, ActivityResult activityResult) {
        int rc = 0;
        if (activityResultList != null) {
            rc = activityResultList.size();
        }
        addForResult(rc, afterResponse, activityResult);
        return rc;
    }

    public void addForResult(int requestCode, ActionsAfterResponse afterResponse, ActivityResult activityResult) {
        if (activityResultList == null) {
            activityResultList = new ArrayList<>();
        }
        activityResultList.add(new RequestActivityResult(requestCode, afterResponse, activityResult));
    }

    public void addForResult(int requestCode, ActivityResult activityResult) {
        if (activityResultList == null) {
            activityResultList = new ArrayList<>();
        }
        activityResultList.add(new RequestActivityResult(requestCode, activityResult));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CALL_PHONE_REQUEST) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCallPhone();
                }
            }
        } else {
            if (permissionsResultList != null) {
                int ik = permissionsResultList.size();
                int j = -1;
                for (int i = 0; i < ik; i++) {
                    RequestPermissionsResult rpr = permissionsResultList.get(i);
                    if (requestCode == rpr.request) {
                        rpr.permissionsResult.onPermissionsResult(requestCode, permissions, grantResults);
                        j = i;
                        break;
                    }
                    if (j > -1) {
                        permissionsResultList.remove(j);
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activityResultList != null) {
            int ik = activityResultList.size();
            int j = -1;
            for (int i = 0; i < ik; i++) {
                RequestActivityResult rar = activityResultList.get(i);
                if (requestCode == rar.request) {
                    j = i;
                    rar.activityResult.onActivityResult(requestCode, resultCode, data, rar.afterResponse);
                    break;
                }
            }
            if (j > -1) {
                RequestActivityResult rar = activityResultList.get(j);
                rar.request = -1;
                rar.activityResult = null;
                rar.afterResponse = null;
            }
        }
    }

    ActivityResult activityResult  = new ActivityResult() {
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data, ActionsAfterResponse afterResponse) {
            if (resultCode == RESULT_OK) {
                for (ViewHandler vh : afterResponse.viewHandlers) {
                    switch (vh.type) {
                        case SET_VALUE:
                            setValue();
                            break;
                        case SET_VALUE_PARAM:
                            setValueParam(vh.viewId);
                            break;
                        case UPDATE_DATA:
                            mComponent.getComponent(vh.viewId).updateData(vh.paramModel);
                            break;
                    }
                }
            }
        }
    };

    public View.OnClickListener navigatorClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mComponent.navigator == null) {
                return;
            }
            int id = view.getId();
            clickNavigat(view, id, mComponent.navigator.viewHandlers);
        }
    };

    public void clickNavigat(View view, int id, List<ViewHandler> viewHandlers) {
        for (ViewHandler vh : viewHandlers) {
            if (vh.viewId == id) {
                switch (vh.type) {
                    case NAME_SCREEN:
                        int requestCode = -1;
                        if (vh.afterResponse != null) {
                            requestCode = addForResult(vh.afterResponse, activityResult);
                        }
                        if (vh.blocked) {
                            if (view != null) {
                                view.setEnabled(false);
                            }
                        }
                        switch (vh.paramForScreen) {
                            case RECORD:
                                startScreen(vh.screen, false, paramScreenRecord, requestCode);
                                break;
                            case RECORD_COMPONENT:
                                BaseComponent bc = mComponent.getComponent(vh.componId);
                                if (bc != null) {
                                    componGlob.setParam(bc.recordComponent);
                                    startScreen(vh.screen, false, bc.recordComponent, requestCode);
                                }
                                break;
                            default:
                                if (vh.addFragment) {
                                    startScreen(vh.screen, false, null, requestCode, vh.addFragment);
                                } else {
                                    startScreen(vh.screen, false, null, requestCode);
                                }
//                                    startScreen(vh.screen, false, null, requestCode);
                                break;
                        }
                        break;
                    case YOUTUBE:
                        if (paramScreenRecord != null) {
                            componGlob.setParam(paramScreenRecord);
                        }
                        String stParYou = componGlob.getParamValue(vh.nameFieldWithValue);
                        if (stParYou != null && stParYou.length() > 0) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(stParYou)));
                        }
                        break;
                    case BACK:
                        onBackPressed();
                        break;
                    case BACK_OK:
                        Intent intentOk = new Intent();
                        intentOk.putExtra(Constants.RECORD, "{}");
                        setResult(RESULT_OK, intentOk);
                        finishActivity();
                        break;
                    case EXIT:
                        exitAccount();
                        break;
                    case CLICK_VIEW:
                        if (mComponent.iCustom != null) {
                            mComponent.iCustom.clickView(view, parentLayout, null, null, -1);
                        } else if (mComponent.moreWork != null) {
                            mComponent.moreWork.clickView(view, parentLayout, null, null, -1);
                        }
                        break;
                    case SHOW:
                        View showView = parentLayout.findViewById(vh.showViewId);
                        if (showView != null) {
                            if (showView instanceof AnimatePanel) {
                                ((AnimatePanel) showView).show(BaseActivity.this);
                            } else {
                                showView.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    case HIDE:
                        showView = parentLayout.findViewById(vh.showViewId);
                        if (showView != null) {
                            if (showView instanceof AnimatePanel) {
                                ((AnimatePanel) showView).hide();
                            } else {
                                showView.setVisibility(View.GONE);
                            }
                        }
                        break;
                    case SHOW_HIDE:
                        if (view == null) break;
                        View vv = parentLayout.findViewById(vh.showViewId);
                        if (vv != null) {
                            TextView tv = (TextView) view;
                            if (vv instanceof AnimatePanel) {
                                AnimatePanel ap = (AnimatePanel) vv;
                                if (ap.isShow()) {
                                    ap.hide();
                                    tv.setText(getString(vh.textHideId));
                                } else {
                                    ap.show(BaseActivity.this);
                                    tv.setText(getString(vh.textShowId));
                                }
                            } else {
                                if (vv.getVisibility() == View.VISIBLE) {
                                    vv.setVisibility(View.GONE);
                                    tv.setText(getString(vh.textHideId));
                                } else {
                                    vv.setVisibility(View.VISIBLE);
                                    tv.setText(getString(vh.textShowId));
                                }
                            }
                        }
                        break;
                    case OPEN_DRAWER:
                        openDrawer();
                        break;
                    case SET_VALUE_PARAM:
                        setValueParam(vh.viewId);
                        break;
                    case SET_VALUE:
                        if (mComponent != null && mComponent.itemSetValues != null) {
                            setValue();
                        } else {
                            View showV = parentLayout.findViewById(vh.showViewId);
                            if (showV != null) {
                                if (showV instanceof TextView) {
                                    ((TextView) showV).setText(getString(vh.idString));
                                }
                            }
                        }
                        break;
                    case CALL_UP:
                        if (view == null) break;
                        if (view instanceof TextView) {
                            String st = ((TextView) view).getText().toString();
                            if (st != null && st.length() > 0) {
                                callUp(st);
                            }
                        }
                        break;
                    case DIAL_UP:
                        if (view == null) break;
                        if (view instanceof TextView) {
                            String st = ((TextView) view).getText().toString();
                            if (st != null && st.length() > 0) {
                                startDialPhone(st);
                            }
                        }
                        break;
                    case RESULT_RECORD :
                        Intent intent = new Intent();
                        if (vh.nameFieldWithValue != null && vh.nameFieldWithValue.length() > 0) {
                            Record record = workWithRecordsAndViews.ViewToRecord(parentLayout, vh.nameFieldWithValue);
                            intent.putExtra(Constants.RECORD, record.toString());
                        } else {
                            intent.putExtra(Constants.RECORD, "{}");
                        }
                        setResult(RESULT_OK, intent);
                        finishActivity();
//                        if (vh.nameFieldWithValue != null && vh.nameFieldWithValue.length() > 0) {
//                            Record record = workWithRecordsAndViews.ViewToRecord(parentLayout, vh.nameFieldWithValue);
//                            Intent intent = new Intent();
//                            intent.putExtra(Constants.RECORD, record.toString());
//                            setResult(RESULT_OK, intent);
//                        }
//                        finishActivity();
                        break;
                    case SET_LOCALE:
                        preferences.setLocale(componGlob.getParamValue(componGlob.appParams.nameLanguageInParam));
                        recreate();
                        break;
                    case SET_GLOBAL:
                        BaseComponent bc = mComponent.getComponent(vh.componId);
                        bc.setGlobalData(vh.nameFieldWithValue);
                        break;
                    case RECEIVER:
                        LocalBroadcastManager.getInstance(BaseActivity.this).registerReceiver(broadcastReceiver,
                                new IntentFilter(vh.nameFieldWithValue));
                        break;
                    case RESULT_PARAM :
                        Record record = workWithRecordsAndViews.ViewToRecord(parentLayout, vh.nameFieldWithValue);
                        if (record != null) {
                            componGlob.setParam(record);
                        }
                        setResult(RESULT_OK);
                        finishActivity();
                        break;
                    case ANIMATE:
                        procesAnimate(vh.animate);
                        break;
                    case SPR_SCALE:
                        SpringScale scale = new SpringScale(parentLayout.findViewById(vh.showViewId), vh.velocity, vh.repeatTime);
                        scale.startAnim();
                        break;

                }
            }
        }
    }

    public void setValueParam(int viewId) {
        View view = parentLayout.findViewById(viewId);
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            int ik = vg.getChildCount();
            for (int i = 0; i < ik; i++) {
                setOneViewValue(vg.getChildAt(i));
            }
        } else {
            setOneViewValue(view);
        }
    }

    public void setOneViewValue(View view) {
        int id = view.getId();
        if (id <= 0) return;
        String name = getResources().getResourceEntryName(id);
        if (name == null) return;
        String value = componGlob.getParamValueIfIs(name);
        if (value == null) return;
        if (view instanceof TextView) {
            if (view instanceof IComponent) {
                ((IComponent) view).setData(value);
            } else {
                ((TextView) view).setText(value);
            }
        } else if (view instanceof ImageView) {
            GlideRequest gr = GlideApp.with(this).load(value);
            if (view instanceof ComponImageView) {
                ComponImageView simg = (ComponImageView) view;
                if (simg.getBlur() > 0) {
                    gr.apply(bitmapTransform(new BlurTransformation(simg.getBlur())));
                }
                if (simg.getPlaceholder() > 0) {
                    gr.apply(placeholderOf(simg.getPlaceholder()));
                }
                if (simg.isOval()) {
                    gr.apply(circleCropTransform());
                }
            }
            gr.into((ImageView) view);
        }
    }

    public void procesAnimate(Animate animate) {
        if (animate.type == Animate.TYPE.SET) {
            for (Animate an : animate.setAnimate) {
                oneAnimate(an);
            }
        } else {
            oneAnimate(animate);
        }
    }

    public void oneAnimate(Animate animate) {
        View v = parentLayout.findViewById(animate.viewId);
        if (v != null) {
            switch (animate.type) {
                case ALPHA:
                    if (animate.onePar) {
                        float al = v.getAlpha();
                        v.animate().alphaBy(al).alpha(animate.par2).setDuration(animate.duration).start();
                    }
                    break;
                case SCALE:
                    v.animate().scaleXBy(animate.par1).scaleYBy(animate.par2).setDuration(animate.duration).start();
                    break;
                case TRANSL:
                    float p1, p2, den = getResources().getDisplayMetrics().density;
                    p1 = den * animate.par1;
                    p2 = den * animate.par2;
                    v.animate().translationXBy(p1).translationYBy(p2).setDuration(animate.duration).start();
                    break;
                case ROTATE:
                    v.animate().rotationBy(animate.par2).setDuration(animate.duration).start();
                    break;
            }
        } else {
            log("0009 Нет View для анимации в " + mComponent.nameComponent);
        }
    }

    public void exitAccount() {
        componGlob.profile.setValue(new Record(), 0, getBaseActivity());
        preferences.setProfile("{}");
        componGlob.token.setValue("", 0, getBaseActivity());
        preferences.setSessionToken("");
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mComponent.moreWork != null) {
                mComponent.moreWork.receiverWork(intent);
            }
            LocalBroadcastManager.getInstance(BaseActivity.this).unregisterReceiver(broadcastReceiver);
        }
    };

    public Screen getComponent(String name) {
        return componGlob.MapScreen.get(name);
    }

    @Override
    public void setFragmentsContainerId(int id) {
        containerFragmentId = id;
    }

    @Override
    protected void onStop() {
        if (mComponent.stopNavig != null) {

        }
        if (listInternetProvider != null) {
            for (BaseInternetProvider provider : listInternetProvider) {
                provider.cancel();
            }
        }
        super.onStop();
    }

    @Override
    public void setResumePause(OnResumePause resumePause) {
        if (resumePauseList == null) {
            resumePauseList = new ArrayList<>();
        }
        resumePauseList.add(resumePause);
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive = true;
        int statusBarColor = preferences.getStatusBarColor();
        if (statusBarColor != 0) {
            setStatusBarColor(statusBarColor);
        }
        if (resumePauseList != null) {
            for (OnResumePause rp : resumePauseList) {
                rp.onResume();
            }
        }
        if (countProgressStart <= 0 && progressDialog != null) {
            countProgressStart = 0;
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onPause() {
        if (resumePauseList != null) {
            for (OnResumePause rp : resumePauseList) {
                rp.onPause();
            }
        }
        isActive = false;
        super.onPause();
    }

    @Override
    public void log(String msg) {
        Log.i(TAG, msg);
    }

    @Override
    public void logNet(String msg) {
        Log.i(TAG_NET, msg);
    }

    @Override
    public void logDB(String msg) {
        Log.i(TAG_DB, msg);
    }

    public void setStatusColor(int color) {
        preferences.setStatusBarColor(color);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resumePauseList != null) {
            for (OnResumePause rp : resumePauseList) {
                rp.onDestroy();
            }
        }
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

        if (singleSettingList != null) {
            for (SingleSetting ss : singleSettingList) {
                ss.close();
            }
            singleSettingList = null;
        }
    }

    @Override
    public Field getProfile() {
        return componGlob.profile;
    }

    @Override
    public void backPressed() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if ( ! canBackPressed()) {
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        int countFragment = fm.getBackStackEntryCount();
//        int countFragment = stackFragments.size();
        if (countFragment > 0) {
            Fragment fragment = topFragment(fm);
            if (fragment != null && fragment instanceof BaseFragment) {
                if (((BaseFragment) fragment).canBackPressed()) {
                    if (((BaseFragment) fragment).noKeyBack()) {
                        if (countFragment == 1) {
//                      finish();
                            finishActivity();
                        } else {
//                            delFragmentStack();
                            super.onBackPressed();
                        }
                    }
                }
            } else {
                if (countFragment == 1) {
                    if (canBackPressed()) {
                        finishActivity();
                    }
                } else {
//                    delFragmentStack();
                    super.onBackPressed();
                }
            }
        } else {
            finishActivity();
        }
    }

//    private void addFragmentStack(String tag) {
//        stackFragments.add(tag);
//    }
//
//    private void delFragmentStack() {
//        int ik = stackFragments.size();
//        if (ik > 0) {
//            stackFragments.remove(ik - 1);
//        }
//    }

    @Override
    public void finish() {
        if (nameGlobalData.size() > 0) {
            for (String name : nameGlobalData) {
                componGlob.delGlobalData(name);
            }
        }
        super.finish();
    }

    public void finishActivity() {
        if (vhFinish != null) {
            String message = getString(vhFinish.idString);
            Record rec = new Record();
            rec.add(new Field("title", Field.TYPE_STRING, getString(vhFinish.showViewId)));
            rec.add(new Field("message", Field.TYPE_STRING, message));
            showDialog(rec, message, listenerFinishDialog, 3);
        } else {
            finishActivityFinal();
        }
    }

    public void finishActivityFinal() {
        finish();
        if (mComponent != null && mComponent.animateScreen != null) {
            switch (mComponent.animateScreen) {
                case TB :
                    overridePendingTransition(R.anim.bt_in, R.anim.bt_out);
                    break;
                case BT :
                    overridePendingTransition(R.anim.tb_in, R.anim.tb_out);
                    break;
                case LR :
                    overridePendingTransition(R.anim.rl_in, R.anim.rl_out);
                    break;
                case RL :
                    overridePendingTransition(R.anim.lr_in, R.anim.lr_out);
                    break;
            }
        }
    }

    View.OnClickListener listenerFinishDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == componGlob.appParams.errorDialogPositiveId) {
                finishActivityFinal();
            }
        }
    };

    private Fragment topFragment(FragmentManager fm) {
        List<Fragment> fragmentList = fm.getFragments();
        Fragment fragment = null;
        for (Fragment fragm : fragmentList) {
            if (fragm != null) {
                fragment = fragm;
            }
        }
        return fragment;
    }

    @Override
    public boolean isHideAnimatePanel() {
        int pos = animatePanelList.size();
        if (pos > 0) {
            animatePanelList.get(pos - 1).hide();
            return false;
        } else {
            return true;
        }
    }

    public boolean canBackPressed() {
        return isHideAnimatePanel();
    }

    @Override
    public BaseFragment getBaseFragment() {
        return null;
    }

    @Override
    public boolean isViewActive() {
        return isActive;
    }

//    @Override

    public void startActivitySimple(String nameMVP, Object object) {
        Screen mc = mapFragment.get(nameMVP);
        if (mc != null) {
            startActivitySimple(nameMVP, mc, object, -1);
        } else {
            log("0003 No screen with name " + nameMVP);
        }
    }

    public void startActivitySimple(String nameMVP, Screen mc, Object object, int forResult) {
        Intent intent;
        if (mc.customFragment == null) {
            intent = new Intent(this, ComponBaseStartActivity.class);
        } else {
            intent = new Intent(this, mc.customFragment);
        }
        intent.putExtra(Constants.NAME_MVP, nameMVP);
        String tTypePush = preferences.getPushType();
        if (tTypePush.length() > 0) {
            intent.putExtra(Constants.SMPL_PUSH_TYPE, tTypePush);
        }
        if (object != null) {
            SimpleRecordToJson recordToJson = new SimpleRecordToJson();
            Field f = new Field();
            f.value = object;
            if (object instanceof Record) {
                f.type = Field.TYPE_RECORD;
                intent.putExtra(Constants.NAME_PARAM_FOR_SCREEN, recordToJson.modelToJson(f));
            } else if (object instanceof ListRecords) {
                f.type = Field.TYPE_LIST_RECORD;
                intent.putExtra(Constants.NAME_PARAM_FOR_SCREEN, recordToJson.modelToJson(f));
            } else if (object instanceof Field) {
                Record rec = (Record) ((Field) object).value;
                for (Field ff : rec) {
                    intent.putExtra(ff.name, (String) ff.value);
                }
            }
        }
        if (forResult > -1) {
            startActivityForResult(intent, forResult);
        } else {
            startActivity(intent);
        }
        if (mc.animateScreen != null) {
            switch (mc.animateScreen) {
                case TB :
                    overridePendingTransition(R.anim.tb_in, R.anim.tb_out);
                    break;
                case BT :
                    overridePendingTransition(R.anim.bt_in, R.anim.bt_out);
                    break;
                case LR :
                    overridePendingTransition(R.anim.lr_in, R.anim.lr_out);
                    break;
                case RL :
                    overridePendingTransition(R.anim.rl_in, R.anim.rl_out);
                    break;
            }
        }
    }

    public void closeDrawer() {
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public void openDrawer() {
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        }
    }

    public ParentModel getParentModel(String name) {
        if (parentModelList.size() > 0) {
            for (ParentModel pm : parentModelList) {
                if (pm.nameParentModel.equals(name)) {
                    return pm;
                }
            }
        }
        ParentModel pm = new ParentModel(name);
        parentModelList.add(pm);
        return pm;
    }

    public void showDialog(String title, String message, View.OnClickListener click) {
        int id = componGlob.appParams.errorDialogViewId;
        Record rec = new Record();
        rec.add(new Field("title", Field.TYPE_STRING, title));
        rec.add(new Field("body", Field.TYPE_STRING, message));
        if (id != 0) {
            View viewErrorDialog = parentLayout.findViewById(id);
            if (viewErrorDialog instanceof AnimatePanel) {
                ((AnimatePanel) viewErrorDialog).show(this);
                workWithRecordsAndViews.RecordToView(rec, viewErrorDialog);
            }
        } else {
            if (componGlob.appParams.errorDialogLayoutId != 0) {
                showErrorDialog(rec, click, 0);
            } else {
                DialogTools.showDialog(this, title, message, click, 0);
            }
        }
    }

    @Override
    public void showDialog(int statusCode, String message, View.OnClickListener click) {
        showDialog(componGlob.formErrorRecord(this, statusCode, message), message, click);
    }

    public void showDialog(Record rec, String message, View.OnClickListener click) {
        showDialog(rec, message, click, 0);
    }

    public void showDialog(Record rec, String message, View.OnClickListener click, int viewClick) {
        int id = componGlob.appParams.errorDialogViewId;
        if (id != 0) {
            View viewErrorDialog = parentLayout.findViewById(id);
            if (viewErrorDialog == null) {
                log("0004 Нет view для ErrorDialog в " + mComponent.nameComponent);
            } else {
                if (viewErrorDialog instanceof AnimatePanel) {
                    ((AnimatePanel) viewErrorDialog).show(this);
                    setErrorDialogParam(rec, viewErrorDialog, click, viewClick);
                } else {
                    viewErrorDialog.setVisibility(View.VISIBLE);
                    setErrorDialogParam(rec, viewErrorDialog, click, viewClick);
                }
            }
        } else {
            if (componGlob.appParams.errorDialogLayoutId != 0) {
                showErrorDialog(rec, click, viewClick);
            } else {
                DialogTools.showDialog(this, "", message, click, viewClick);
            }
        }
    }

    private void setErrorDialogParam(Record rec, View viewErrorDialog, View.OnClickListener click, int viewClick) {
        workWithRecordsAndViews.RecordToView(rec, viewErrorDialog);
        AppParams appParams = componGlob.appParams;
        if (appParams.errorDialogNegativeId != 0) {
            parentLayout.findViewById(appParams.errorDialogNegativeId).setOnClickListener(click);
        }
        if (appParams.errorDialogPositiveId != 0) {         // Positive
            View viewPositive = parentLayout.findViewById(appParams.errorDialogPositiveId);
            if ((viewClick & 2) > 0) {
                viewPositive.setOnClickListener(click);
                viewPositive.setVisibility(View.VISIBLE);
            } else {
                viewPositive.setVisibility(View.GONE);
            }
        }
    }

    public void showErrorDialog(Record rec, View.OnClickListener click, int viewClick) {
        if ( ! isActive) return;
        ErrorDialog ed = new ErrorDialog();
        ed.setParam(rec, click, viewClick);
        ed.show(getFragmentManager(), "dialog");
    }

    @Override
    public void progressStart() {
        if (componGlob.appParams.classProgress != null) {
            if (progressDialog == null) {
                try {
                    progressDialog = (DialogFragment) componGlob.appParams.classProgress.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (countProgressStart <= 0) {
                countProgressStart = 0;
                progressDialog.show(getFragmentManager(), "MyProgressDialog");
            }
            countProgressStart++;
        } else if (componGlob.appParams.progressLayoutId != 0) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog();
            }
            if (countProgressStart <= 0) {
                countProgressStart = 0;
                progressDialog.show(getFragmentManager(), "MyProgressDialog");
            }
            countProgressStart++;
        }
    }

    @Override
    public void progressStop() {
        countProgressStart--;
        if (countProgressStart <= 0 && progressDialog != null && isActive) {
            try {
                progressDialog.dismiss();
                progressDialog = null;
                countProgressStart = 0;
            } catch (NullPointerException e) {
                progressDialog = null;
                countProgressStart = 0;
            }
        }
    }

    private void resetProgress() {
        countProgressStart = 0;
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void startDrawerFragment(String nameMVP, int containerFragmentId) {
        Screen model = mapFragment.get(nameMVP);
        drawerFragment = new BaseFragment();
        drawerFragment.setModel(model);
        Bundle bundle =new Bundle();
        bundle.putString(Constants.NAME_MVP, model.nameComponent);
        drawerFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerFragmentId, drawerFragment, model.nameComponent);
        transaction.commit();
    }

    @Override
    public void startScreen(String screen, boolean startFlag) {
        startScreen(screen, startFlag, null, -1);
    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    @Override
    public void startScreen(String screen, boolean startFlag, Object object) {
        startScreen(screen, startFlag, object, -1);
    }

    @Override
    public void startScreen(String nameMVP, boolean startFlag, Object object, int forResult) {
        startScreen(nameMVP, startFlag, object, forResult, false);
    }

    String SaveNameMVP;
    boolean SaveStartFlag;
    Object SaveObject;
    int SaveForResult;
    boolean SaveAddFragment;

    @Override
    public void startScreen(String nameMVP, boolean startFlag, Object object, int forResult, boolean addFragment) {
        SaveNameMVP = nameMVP;
        SaveStartFlag = startFlag;
        SaveObject = object;
        SaveForResult = forResult;
        SaveAddFragment = addFragment;
        if (isActive) {
            startScreenIsActive();
        } else {
            handler.postDelayed(startScr, 20);
        }
    }

    Handler handler = new Handler();

    Runnable startScr = new Runnable() {
        @Override
        public void run() {
            if (isActive) {
                startScreenIsActive();
            } else {
                handler.postDelayed(startScr, 50);
            }
        }
    };

    public void startScreenIsActive() {
        String nameMVP = SaveNameMVP;
        boolean startFlag = SaveStartFlag;
        Object object = SaveObject;
        int forResult = SaveForResult;
        boolean addFragment = SaveAddFragment;
        Screen mComponent = mapFragment.get(nameMVP);
        if (mComponent == null || mComponent.typeView == null) {
            log("0003 No screen with name " + nameMVP);
            return;
        }
        switch (mComponent.typeView) {
            case ACTIVITY:
                startActivitySimple(nameMVP, mComponent, object, forResult);
                break;
            case CUSTOM_ACTIVITY:
                startActivitySimple(nameMVP, mComponent, object, forResult);
                break;
            case FRAGMENT:
                startFragment(nameMVP, mComponent, startFlag, object, forResult, addFragment);
                break;
            case CUSTOM_FRAGMENT:
                startCustomFragment(nameMVP, mComponent, startFlag, object, forResult, addFragment);
                break;
        }
    }

    public void startCustomFragment(String nameMVP, Screen mComponent, boolean startFlag, Object object, int forResult, boolean addFragment) {
        BaseFragment fr = (BaseFragment) getSupportFragmentManager().findFragmentByTag(nameMVP);
//        int count = (fr == null) ? 0 : 1;
        if (startFlag) {
            clearBackStack(nameMVP, fr == null);
        }
        BaseFragment fragment = null; // (fr != null) ? fr : new ComponentsFragment();
        if (fr != null) {
            fragment = fr;
        } else {
            try {
                fragment = (BaseFragment) mComponent.customFragment.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (fragment != null) {
            Bundle bundle = null;
            if (object != null) {
                if (object instanceof Bundle) {
                    bundle= (Bundle) object;
                }
            }
            if (bundle == null){
                bundle = new Bundle();
            }
            bundle.putString(Constants.NAME_MVP, nameMVP);
            if (object != null) {
                if (object instanceof Record || object instanceof ListRecords) {
                    SimpleRecordToJson recordToJson = new SimpleRecordToJson();
                    Field f = new Field();
                    f.value = object;
                    if (object instanceof Record) {
                        f.type = Field.TYPE_RECORD;
                    } else {
                        f.type = Field.TYPE_LIST_RECORD;
                    }
                    bundle.putString(Constants.NAME_PARAM_FOR_SCREEN, recordToJson.modelToJson(f));
                } else {
                    fragment.setObject(object);
                }
            }
            fragment.setArguments(bundle);
            fragment.setModel(mComponent);
            startNewFragment(fragment, nameMVP, mComponent, addFragment);
        }
    }

    public void startFragment(String nameMVP, Screen mComponent, boolean startFlag, Object object, int forResult, boolean addFragment) {
        BaseFragment fr = (BaseFragment) getSupportFragmentManager().findFragmentByTag(nameMVP);

        if (startFlag) {
            clearBackStack(nameMVP, fr == null);
        }
        BaseFragment fragment = (fr != null) ? fr : new BaseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.NAME_MVP, nameMVP);
        if (object != null) {
            SimpleRecordToJson recordToJson = new SimpleRecordToJson();
            Field f = new Field();
            f.value = object;
            if (object instanceof Record) {
                f.type = Field.TYPE_RECORD;
                bundle.putString(Constants.NAME_PARAM_FOR_SCREEN, recordToJson.modelToJson(f));
            } else if (object instanceof ListRecords) {
                f.type = Field.TYPE_LIST_RECORD;
                bundle.putString(Constants.NAME_PARAM_FOR_SCREEN, recordToJson.modelToJson(f));
            }
        }
        fragment.setArguments(bundle);
        fragment.setModel(mComponent);
        if (fr == null) {
            startNewFragment(fragment, nameMVP, mComponent, addFragment);
        }
        String tTypePush = preferences.getPushType();
        if (tTypePush.length() > 0) {
            fragment.startPush();
        }
    }

    private void startNewFragment(BaseFragment fragment, String nameMVP, Screen mComponent, boolean addFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mComponent.animateScreen != null) {
            switch (mComponent.animateScreen) {
                case RL:
                    transaction.setCustomAnimations(R.anim.rl_in, R.anim.rl_out,
                            R.anim.lr_in, R.anim.lr_out);
                    break;
                case LR :
                    transaction.setCustomAnimations(R.anim.lr_in, R.anim.lr_out,
                            R.anim.rl_in, R.anim.rl_out);
                    break;
                case TB :
                    transaction.setCustomAnimations(R.anim.tb_in, R.anim.tb_out,
                            R.anim.bt_in, R.anim.bt_out);
                    break;
                case BT :
                    transaction.setCustomAnimations(R.anim.bt_in, R.anim.bt_out,
                            R.anim.tb_in, R.anim.tb_out);
                    break;
            }
        }
        if (containerFragmentId != 0) {
            if (addFragment) {
                transaction.add(containerFragmentId, fragment, nameMVP);
            } else {
                transaction.replace(containerFragmentId, fragment, nameMVP);
            }
        } else {
            log("0014 Нет контейнера фрагментов в " + nameScreen);
        }
        resetProgress();
        transaction.addToBackStack(nameMVP);
        transaction.commit();
//        addFragmentStack(nameMVP);
    }

    public void clearBackStack(String nameF, boolean thisClear) {
        FragmentManager manager = getSupportFragmentManager();
        int ik = manager.getBackStackEntryCount();
        if (ik > 0) {
            ik--;
            for (int i = ik; i > -1; i--) {
                FragmentManager.BackStackEntry bse = manager.getBackStackEntryAt(i);
                if (nameF.equals(bse.getName())) {
                    if (thisClear) {
                        manager.popBackStack();
//                        if (i < stackFragments.size()) {
//                            stackFragments.remove(i);
//                        }
                    }
                    break;
                } else {
                    manager.popBackStack();
//                    if (i < stackFragments.size()) {
//                        stackFragments.remove(i);
//                    }
                }
            }
        }
    }

    public void addParamValue(String name, String value) {
        componGlob.addParamValue(name, value);
    }


    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }

    @Override
    public void addInternetProvider(BaseInternetProvider internetProvider) {
        listInternetProvider.add(internetProvider);
    }

    @Override
    public View getParentLayout() {
        return parentLayout;
    }

    @Override
    public void addEvent(int sender, BaseComponent receiver) {
        listEvent.add(new EventComponent(sender, receiver));
    }

    @Override
    public void addEvent(int[] senderList, BaseComponent receiver) {
        for (int sender : senderList) {
            listEvent.add(new EventComponent(sender, receiver));
        }
    }

    @Override
    public void sendEvent(int sender) {
        for (EventComponent ev : listEvent) {
            if (ev.eventSenderId == sender) {
                ev.eventReceiverComponent.actual();
            }
        }
    }

    @Override
    public void sendActualEvent(int sender, Object paramEvent) {
        for (EventComponent ev : listEvent) {
            if (ev.eventSenderId == sender) {
                ev.eventReceiverComponent.actualEvent(sender, paramEvent);
            }
        }
    }

    @Override
    public void addAnimatePanel(AnimatePanel animatePanel) {
        animatePanelList.add(animatePanel);
    }

    @Override
    public void delAnimatePanel(AnimatePanel animatePanel) {
        animatePanelList.remove(animatePanel);
    }

    @Override
    public Field getParamScreen() {
        return paramScreen;
    }

    public String[] setFormatParam(String[] args) {
        String[] st = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            boolean is = true;
            for (Param paramV : componGlob.paramValues) {
                if (arg.equals(paramV.name)) {
                    st[i] = paramV.value;
                    is = false;
                    break;
                }
            }
            if (is) {
                st[i] = " ";
            }
        }
        return st;
    }

    public void showSheetBottom(int id, Record rec, BaseComponent bc, View.OnClickListener clickView) {
        View clickInfoWindow = parentLayout.findViewById(id);
        if (clickInfoWindow == null) {
            log("Не найден clickInfoWindow в " + mComponent.nameComponent);
        } else {
            if (clickInfoWindow instanceof AnimatePanel) {
                ((AnimatePanel) clickInfoWindow).show(this);
            } else {
                clickInfoWindow.setVisibility(View.VISIBLE);
            }
            if (rec != null) {
                workWithRecordsAndViews.RecordToView(rec, clickInfoWindow, bc, clickView);
            }
        }
    }

    public void setGlobalData(String name, int type, Object data) {
        if (name != null && name.length() > 0) {
            nameGlobalData.add(name);
            Field ff = componGlob.globalData.getField(name);
            if (ff == null) {
                componGlob.globalData.add(new Field(name, type, data));
            } else {
                ff.type = type;
                ff.value = data;
            }
        }
    }

    public void setMenu() {
        MenuBottomComponent menu = (MenuBottomComponent) mComponent.getComponent(ParamComponent.TC.MENU_BOTTOM);
        if (menu != null) {
            menu.setItem();
        }
    }

    public void setMenu(int position) {
        MenuBottomComponent menu = (MenuBottomComponent) mComponent.getComponent(ParamComponent.TC.MENU_BOTTOM);
        if (menu != null) {
            menu.setItem(position);
        }
    }

    public void callUp(String phoneDial) {
        this.phoneDial = phoneDial;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    CALL_PHONE_REQUEST);
        } else {
            startCallPhone();
        }
    }

    private void startCallPhone() {
        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneDial));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(dialIntent);
    }

    public void startDialPhone(String phone) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        startActivity(dialIntent);
    }

    // Прибирати клавіатуру при кліку за межами EditText
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            final View view = getCurrentFocus();
            if (view != null) {
                final boolean consumed = super.dispatchTouchEvent(ev);
                final View viewTmp = getCurrentFocus();
                final View viewNew = viewTmp != null ? viewTmp : view;
                if (viewNew.equals(view)) {
                    final Rect rect = new Rect();
                    final int[] coordinates = new int[2];
                    view.getLocationOnScreen(coordinates);
                    rect.set(coordinates[0], coordinates[1], coordinates[0] + view.getWidth(), coordinates[1] + view.getHeight());
                    final int x = (int) ev.getX();
                    final int y = (int) ev.getY();
                    if (rect.contains(x, y)) {
                        return consumed;
                    }
                } else if (viewNew instanceof EditText) {
                    return consumed;
                }
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(viewNew.getWindowToken(), 0);
                viewNew.clearFocus();
                return consumed;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
