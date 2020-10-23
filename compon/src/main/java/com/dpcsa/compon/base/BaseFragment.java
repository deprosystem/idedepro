package com.dpcsa.compon.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import jp.wasabeef.glide.transformations.BlurTransformation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dpcsa.compon.components.MenuBottomComponent;
import com.dpcsa.compon.components.MenuComponent;
import com.dpcsa.compon.components.PagerFComponent;
import com.dpcsa.compon.components.RecyclerComponent;
import com.dpcsa.compon.components.ToolBarComponent;
import com.dpcsa.compon.custom_components.ComponImageView;
import com.dpcsa.compon.glide.GlideApp;
import com.dpcsa.compon.glide.GlideRequest;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.ActivityResult;
import com.dpcsa.compon.interfaces_classes.Animate;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.interfaces_classes.ItemSetValue;
import com.dpcsa.compon.interfaces_classes.Param;
import com.dpcsa.compon.interfaces_classes.PushHandler;
import com.dpcsa.compon.interfaces_classes.SpringScale;
import com.dpcsa.compon.interfaces_classes.SpringY;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;
import com.dpcsa.compon.single.Injector;
import com.google.android.gms.common.api.GoogleApiClient;

import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.interfaces_classes.AnimatePanel;
import com.dpcsa.compon.interfaces_classes.EventComponent;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.OnResumePause;
import com.dpcsa.compon.interfaces_classes.ParentModel;
import com.dpcsa.compon.interfaces_classes.SetData;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.JsonSimple;
import com.dpcsa.compon.json_simple.JsonSyntaxException;
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.tools.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.circleCropTransform;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;
import static com.dpcsa.compon.interfaces_classes.ItemSetValue.TYPE_SOURCE.GROUPP_PARAM;
import static com.dpcsa.compon.interfaces_classes.ItemSetValue.TYPE_SOURCE.PARAM;
import static com.dpcsa.compon.param.ParamModel.GET;
import static com.dpcsa.compon.param.ParamModel.POST;
import static com.dpcsa.compon.param.ParamModel.POST_DB;

public class BaseFragment extends Fragment implements IBase {
    protected View parentLayout;
    private Object mObject;
    public List<BaseInternetProvider> listInternetProvider;
    public Screen mComponent;
    public List<EventComponent> listEvent;
    public List<ParentModel> parentModelList;
    private Bundle savedInstanceState;
    private GoogleApiClient googleApiClient;
    private List<AnimatePanel> animatePanelList;
    protected BaseActivity activity;
    private String nameMvp = null;
    public String TAG, TAG_DB, TAG_NET;
    public Field paramScreen;
    public List<OnResumePause> resumePauseList;
    private ComponGlob componGlob;
    private ComponPrefTool preferences;
    public WorkWithRecordsAndViews workWithRecordsAndViews;
    private ToolBarComponent toolBar;
    private String typePush;
    public ViewHandler selectViewHandler;

    public BaseFragment() {
        mObject = null;
        listInternetProvider = new ArrayList<>();
        listEvent = new ArrayList<>();
        parentModelList = new ArrayList<>();
        componGlob = Injector.getComponGlob();
        TAG = componGlob.appParams.NAME_LOG_APP;
        TAG_DB = componGlob.appParams.NAME_LOG_DB;
        TAG_NET = componGlob.appParams.NAME_LOG_NET;
    }

    public BaseFragment getThis() {
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        activity = getBaseActivity();
        preferences = Injector.getPreferences();
        workWithRecordsAndViews = new WorkWithRecordsAndViews();
        Bundle bundle = getArguments();
        if (bundle != null) {
            nameMvp = bundle.getString(Constants.NAME_MVP);
            String paramJson = bundle.getString(Constants.NAME_PARAM_FOR_SCREEN);
            if (paramJson != null && paramJson.length() >0) {
                JsonSimple jsonSimple = new JsonSimple();
                try {
                    paramScreen = jsonSimple.jsonToModel(paramJson);
                } catch (JsonSyntaxException e) {
                    log(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (savedInstanceState != null) {
            nameMvp = savedInstanceState.getString(Constants.NAME_MVP);
        }
        if (mComponent == null) {
            if (nameMvp != null && nameMvp.length() > 0) {
                mComponent = activity.getComponent(nameMvp);
            }
        }
        if (mComponent == null || mComponent.typeView == Screen.TYPE_VIEW.CUSTOM_FRAGMENT) {
            if (getLayoutId() != 0) {
                parentLayout = inflater.inflate(getLayoutId(), null, false);
            } else {
                log("0007 Вызывается неопределенный фрагмент");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                parentLayout = new LinearLayout(this.getActivity());
                parentLayout.setLayoutParams(lp);
            }
        } else {
            parentLayout = inflater.inflate(mComponent.fragmentLayoutId, null, false);
        }
        if (mComponent != null) {
            animatePanelList = new ArrayList<>();
            if ((mComponent.title != null && mComponent.title.length() > 0)|| mComponent.titleId != 0) {
                setTitle();
            }

            if (mComponent.navigator != null) {
                for (ViewHandler vh : mComponent.navigator.viewHandlers) {
                    View v = parentLayout.findViewById(vh.viewId);
                    if (v != null) {
                        v.setOnClickListener(navigatorClick);
                    }
                }
            }
            if (mComponent.listSetData != null) {
                int ik = mComponent.listSetData.size();
                for (int i = 0; i < ik; i++) {
                    SetData sd = (SetData) mComponent.listSetData.get(i);
                    String value;
                    if (sd.source == 0) {
                        value = preferences.getNameString(sd.nameParam);
                    } else {
                        value = componGlob.getParamValue(sd.nameParam);
                    }
                    View v = parentLayout.findViewById(sd.viewId);
                    if (v != null) {
                        if (v instanceof TextView) {
                            ((TextView)v).setText(value);
                        }
                    }
                }
            }
            mComponent.initComponents(this);
            if (mComponent.moreWork != null) {
                mComponent.moreWork.startScreen();
            }
        }
        initView(savedInstanceState);
        setValue();
        return parentLayout;
    }

    public void initView(Bundle savedInstanceState) { }

    public void setTitle() {
        String txtTit;
        if (mComponent.titleId != 0) {
            mComponent.title = getResources().getString(mComponent.titleId);
        }
        if (mComponent.args != null && mComponent.args.length() > 0) {
            txtTit = String.format(mComponent.title, activity.setFormatParam(mComponent.args.split(",")));
        } else {
            txtTit = mComponent.title;
        }
        if (toolBar != null) {
            toolBar.setTitle(txtTit);
        } else {
            TextView titV = (TextView) componGlob.findViewByName(parentLayout, "title");
            if (titV != null) {
                titV.setText(txtTit);
            } else {
                activity.setTitle(txtTit);
            }
        }
    }

    @Override
    public void setToolBar(ToolBarComponent toolBar) {
        this.toolBar = toolBar;
    }

    public void setValue() {
        if (mComponent != null && mComponent.itemSetValues != null) {
            for (ItemSetValue sv : mComponent.itemSetValues) {
                if (sv.type == GROUPP_PARAM) {
                    setValueParam(sv.viewId, sv.name);
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
                        case GLOBAL_VAR:
                            setVar(sv.viewId, sv.name);
                            break;
                        case LOCALE:
                            if (v instanceof TextView) {
                                ((TextView) v).setText(preferences.getLocale());
                            } else if (v instanceof IComponent) {
                                ((IComponent) v).setData(preferences.getLocale());
                            }
                            break;
                        case SYSTEM_TIME:
                            Calendar c = new GregorianCalendar();
                            long tt = c.getTimeInMillis();
                            String dat;
                            if (sv.name != null && sv.name.length() > 0) {
                                SimpleDateFormat df = new SimpleDateFormat(sv.name);
                                dat = df.format(tt);
                                if (v instanceof IComponent) {
                                    ((IComponent) v).setData(dat);
                                } else  if (v instanceof TextView) {
                                    ((TextView) v).setText(dat);
                                }
                            } else {
                                if (v instanceof IComponent) {
                                    ((IComponent) v).setData(tt);
                                } else  if (v instanceof TextView) {
                                    ((TextView) v).setText(String.valueOf(tt));
                                }
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

    @Override
    public void onStart() {
        super.onStart();
        if (mComponent.startNavig != null) {
            clickNavigat(null, 0, mComponent.startNavig.viewHandlers);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.NAME_MVP, nameMvp);
    }

    @Override
    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    @Override
    public void startDrawerFragment(String screen, int containerFragmentId) {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mComponent.pushNavigator != null) {
                startPush();
            }
        }
    }

    @Override
    public void startPush() {
        work.run();
    }

    Handler handler = new Handler();

    public Runnable work = new Runnable() {
        @Override
        public void run() {
            if (getView() == null ||  ! getView().isShown()) {
                handler.postDelayed(work, 30);
            } else {
                runPush();
            }
        }
    };

    public void runPush() {
        if (preferences == null) {
            preferences = Injector.getPreferences();
        }
        typePush = preferences.getPushType();
        if (mComponent.pushNavigator != null) {
            for (PushHandler push : mComponent.pushNavigator.pushHandlers) {
                switch (push.type) {
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
        typePush = "";
    }

    public int getLayoutId() {
        return 0;
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

    @Override
    public void setResumePause(OnResumePause resumePause) {
        if (resumePauseList == null) {
            resumePauseList = new ArrayList<>();
        }
        resumePauseList.add(resumePause);
    }

    View.OnClickListener navigatorClick = new View.OnClickListener() {
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
                            requestCode = activity.addForResult(vh.afterResponse, activityResult);
                        }
                        if (vh.blocked) {
                            if (view != null) {
                                view.setEnabled(false);
                            }
                        }
                        switch (vh.paramForScreen) {
                            case RECORD:
                                startScreen(vh.screen, false, paramScreen, requestCode);
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
                                break;
                        }
                        break;
                    case YOUTUBE:
                        if (paramScreen != null && paramScreen.value != null) {
                            componGlob.setParam((Record) paramScreen.value);
                        }
                        String stParYou = componGlob.getParamValue(vh.nameFieldWithValue);
                        if (stParYou != null && stParYou.length() > 0) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(stParYou)));
                        }
                        break;
                    case SET_PARAM:
                        componGlob.setParamValue(vh.nameFieldWithValue, vh.pref_value_string);
                        break;
                    case RESULT_RECORD :
                        Intent intent = new Intent();
                        if (vh.nameFieldWithValue != null && vh.nameFieldWithValue.length() > 0) {
                            Record record = workWithRecordsAndViews.ViewToRecord(parentLayout, vh.nameFieldWithValue);
                            componGlob.setParam(record);
                            intent.putExtra(Constants.RECORD, record.toString());
                        } else {
                            log("Запись с возвращаемыми параметрами не сформирована в " + mComponent.nameComponent);
                            intent.putExtra(Constants.RECORD, "{}");
                        }
                        activity.setResult(activity.RESULT_OK, intent);
                        activity.finishActivity();
                        break;
                    case SET_VALUE_PARAM:
                        setValueParam(vh.viewId, null);
                        break;
                    case SET_VALUE:
                        if (mComponent != null && mComponent.itemSetValues != null) {
                            setValue();
                        } else {
                            View showV = parentLayout.findViewById(vh.componId);
                            if (showV != null) {
                                if (showV != null) {
                                    if (showV instanceof IComponent) {
                                        if (vh.idString != 0) {
                                            ((IComponent) showV).setData(getString(vh.idString));
                                        } else if (vh.nameFieldWithValue != null) {
                                            ((IComponent) showV).setData(vh.nameFieldWithValue);
                                        }
                                    } else if (showV instanceof TextView) {
                                        if (vh.idString != 0) {
                                            ((TextView) showV).setText(getString(vh.idString));
                                        } else if (vh.nameFieldWithValue != null) {
                                            ((TextView) showV).setText(vh.nameFieldWithValue);
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case CALL_UP:
                        if (view != null && view instanceof TextView) {
                            String st = ((TextView) view).getText().toString();
                            if (st != null && st.length() > 0) {
                                activity.callUp(st);
                            }
                        }
                        break;
                    case DIAL_UP:
                        if (view != null && view instanceof TextView) {
                            String st = ((TextView) view).getText().toString();
                            if (st != null && st.length() > 0) {
                                activity.startDialPhone(st);
                            }
                        }
                        break;
                    case SHOW:
                        if (vh.onActivity) {
                            activity.showSheetBottom(vh.showViewId, null, null, null);
                        } else {
                            View showView = parentLayout.findViewById(vh.showViewId);
                            if (showView instanceof AnimatePanel) {
                                ((AnimatePanel) showView).show(BaseFragment.this);
                            } else {
                                showView.setVisibility(View.VISIBLE);
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
                                    tv.setText(activity.getString(vh.textHideId));
                                } else {
                                    ap.show(getThis());
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
                    case SAVE_PARAM:
                        Record recParam = workWithRecordsAndViews.ViewToRecord(parentLayout, vh.nameFieldWithValue);
                        activity.setParamFromComponents(recParam, parentLayout, mComponent, activity);
                        componGlob.setParam(recParam);
                        break;
                    case CLEAN_VAR:
                        Field ff = componGlob.globalData.getField(vh.nameFieldWithValue);
                        if (ff != null && ff.type == Field.TYPE_RECORD) {
                            ((Record) ff.value).clear();
                        }
                        break;
                    case CLEAN_COPY_VAR:
                        String nn = "\u0000" + vh.nameFieldWithValue;
                        String ns = vh.nameFieldWithValue;
                        ff = componGlob.globalData.getField(ns);
                        if (ff != null && ff.type == Field.TYPE_RECORD) {
                            Field ffCopy = componGlob.globalData.getField(nn);
                            if (ffCopy == null) {
                                ffCopy = new Field(nn, Field.TYPE_RECORD, null);
                                componGlob.globalData.add(ffCopy);
                            }
                            Record rec = (Record) ff.value;
                            if (rec != null) {
                                ffCopy.value = rec.copyRecord();
                                rec.clear();
                            }
                        }
                        break;
                    case RESTORE_VAR:
                        ns = "\u0000" + vh.nameFieldWithValue;
                        nn = vh.nameFieldWithValue;
                        ff = componGlob.globalData.getField(vh.nameFieldWithValue);
                        if (ff != null && ff.type == Field.TYPE_RECORD) {
                            Field ffCopy = componGlob.globalData.getField(nn);
                            if (ffCopy == null) {
                                ffCopy = new Field(nn, Field.TYPE_RECORD, null);
                                componGlob.globalData.add(ffCopy);
                            }
                            Record rec = (Record) ff.value;
                            if (rec != null) {
                                ffCopy.value = rec.copyRecord();
                                rec.clear();
                            }
                        }
                        componGlob.globalData.deleteField(ns);
                        break;
                    case DEL_VAR:
                        ff = componGlob.globalData.getField(vh.nameFieldWithValue);
                        if (ff != null && ff.type == Field.TYPE_RECORD) {
                            Record recGl = (Record) ff.value;
                            String[] par = vh.pref_value_string.split(",");
                            int ik = par.length;
                            for (int i = 0; i < ik; i++) {
                                recGl.deleteField(par[i]);
                            }
                        }
                        break;
                    case SET_GLOBAL:
                        BaseComponent bc = mComponent.getComponent(vh.componId);
                        bc.setGlobalData(vh.nameFieldWithValue);
                        break;
                    case SET_MENU_DEF:
                        activity.setMenu();
                        break;
                    case SET_MENU:
                        activity.setMenu();
                        break;
                    case CLICK_SEND:
                        Record rec = null;
                        if (paramScreen != null && paramScreen.value != null) {
                            rec = (Record) paramScreen.value;
                            componGlob.setParam(rec);
                        }
                        selectViewHandler = vh;
                        switch (vh.paramModel.method) {
                            case POST_DB:
                                BaseDB baseDB = Injector.getBaseDB();
                                baseDB.insertRecord(vh.paramModel.url, rec, listener_send_back_screen);
                                break;
                            case GET:
                                new BasePresenter(BaseFragment.this, vh.paramModel, null, rec, listener_send_back_screen);
                                break;
                            case POST:
                                new BasePresenter(BaseFragment.this, vh.paramModel, null, rec, listener_send_back_screen);
                                break;
                        }
                        break;
                    case CLICK_VIEW:
                        if (mComponent.iCustom != null) {
                            mComponent.iCustom.clickView(view, parentLayout, null, null, -1);
                        } else if (mComponent.moreWork != null) {
                            mComponent.moreWork.clickView(view, parentLayout, null, null, -1);
                        }
                        break;
                    case BACK:
                        backPressed();
                        break;
                    case EXIT:
                        activity.exitAccount();
                        break;
                    case OPEN_DRAWER:
                        activity.openDrawer();
                        break;
                    case RECEIVER:
                        LocalBroadcastManager.getInstance(activity)
                                .registerReceiver(broadcastReceiver, new IntentFilter(vh.nameFieldWithValue));
                        break;
                    case ANIMATE:
                        procesAnimate(vh.animate);
                        break;
                    case SPR_SCALE:
                        SpringScale scale = new SpringScale(parentLayout.findViewById(vh.showViewId), vh.velocity, vh.repeatTime);
                        scale.startAnim();
                        break;
                    case SPR_Y:
                        SpringY y = new SpringY(parentLayout.findViewById(vh.showViewId), vh.velocity, vh.repeatTime);
                        y.startAnim();
                        break;
                }
            }
        }
    }

    public void setValueParam(int viewId, String name) {
        View view = parentLayout.findViewById(viewId);
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            int ik = vg.getChildCount();
            for (int i = 0; i < ik; i++) {
                setOneViewValue(vg.getChildAt(i), null);
            }
        } else {
            setOneViewValue(view, name);
        }
    }

    public void setOneViewValue(View view, String namePar) {
        int id = view.getId();
        if (id <= 0) return;
        String name;
        if (namePar == null || namePar.length() == 0) {
            name = getResources().getResourceEntryName(id);
        } else {
            name = namePar;
        }
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

//    public void setValueParam(int viewId) {
//        View view = parentLayout.findViewById(viewId);
//        if (view instanceof ViewGroup) {
//            ViewGroup vg = (ViewGroup) view;
//            int ik = vg.getChildCount();
//            for (int i = 0; i < ik; i++) {
//                setOneViewValue(vg.getChildAt(i));
//            }
//        } else {
//            setOneViewValue(view);
//        }
//    }
//
//    public void setOneViewValue(View view) {
//        int id = view.getId();
//        if (id <= 0) return;
//        String name = getResources().getResourceEntryName(id);
//        if (name == null) return;
//        String value = componGlob.getParamValueIfIs(name);
//        if (value == null) return;
//        if (view instanceof TextView) {
//            if (view instanceof IComponent) {
//                ((IComponent) view).setData(value);
//            } else {
//                ((TextView) view).setText(value);
//            }
//        } else if (view instanceof ImageView) {
//            GlideRequest gr = GlideApp.with(this).load(value);
//            if (view instanceof ComponImageView) {
//                ComponImageView simg = (ComponImageView) view;
//                if (simg.getBlur() > 0) {
//                    gr.apply(bitmapTransform(new BlurTransformation(simg.getBlur())));
//                }
//                if (simg.getPlaceholder() > 0) {
//                    gr.apply(placeholderOf(simg.getPlaceholder()));
//                }
//                if (simg.isOval()) {
//                    gr.apply(circleCropTransform());
//                }
//            }
//            gr.into((ImageView) view);
//        }
//    }

    IPresenterListener listener_send_back_screen = new IPresenterListener() {
        @Override
        public void onResponse(Field response) {
//            if (selectViewHandler != null && selectViewHandler.afterResponse != null) {
//                afterHandler(response, selectViewHandler.afterResponse.viewHandlers);
//            }
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
//            onErrorModel(statusCode, message, click);
        }
    };

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

    ActivityResult activityResult  = new ActivityResult() {
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data, ActionsAfterResponse afterResponse) {
            if (resultCode == activity.RESULT_OK) {
                View vv;
                for (ViewHandler vh : afterResponse.viewHandlers) {
                    switch (vh.type) {
                        case SET_VALUE:
                            setValue();
                            break;
                        case SET_VALUE_PARAM:
                            setValueParam(vh.viewId, null);
                            break;
                        case SET_VAR:
                            setVar(vh.viewId, vh.nameFieldWithValue);
                            break;
                        case ASSIGN_VALUE:
                            vv = parentLayout.findViewById(vh.viewId);
                            if (vv != null) {
                                String json = data.getStringExtra(Constants.RECORD);
                                JsonSimple jsonSimple = new JsonSimple();
                                Field ff = null;
                                try {
                                    ff = jsonSimple.jsonToModel(json);
                                } catch (JsonSyntaxException e) {
                                    log(e.getMessage());
                                    e.printStackTrace();
                                }
                                if (ff != null) {
                                    workWithRecordsAndViews.RecordToView((Record) ff.value, vv);
                                }
                            }
                            break;
                        case ACTUAL:
                            BaseComponent bc = mComponent.getComponent(vh.showViewId);
                            if (bc != null) {
                                bc.actual();
                            } else {
                                String stN = activity.getResources().getResourceEntryName(vh.showViewId);
                                log("0004 Нет компонента с id " + stN + " для актуализации в " + mComponent.nameComponent);
                            }
                            break;
                        case SHOW:
                            vv = parentLayout.findViewById(vh.showViewId);
                            if (vv != null) {
                                if (vv instanceof AnimatePanel) {
                                    ((AnimatePanel) vv).show(getThis());
                                } else {
                                    vv.setVisibility(View.VISIBLE);
                                }
                                if (vh.nameFieldWithValue != null && vh.nameFieldWithValue.length() > 0) {
                                    workWithRecordsAndViews.RecordToView(componGlob.paramToRecord(vh.nameFieldWithValue), vv);
                                }
                            }
                            break;
                    }
                }
            }
        }
    };

    public void setVar(int viewId, String nameVar) {
        View view = parentLayout.findViewById(viewId);
        if (view != null) {
            Field ff = componGlob.globalData.getField(nameVar);
            Object obj = null;
            if (ff != null) {
                obj = ff.value;
                if (obj instanceof Record) {
                    for (Field fr : (Record) obj) {
                        setOneParam(fr);
                    }
                } else if (obj instanceof Field) {
                    setOneParam((Field) obj);
                }
            }
            if (view instanceof IComponent) {
                ((IComponent) view).setData(obj);
            } else if (view instanceof ViewGroup) {

            }
        }
    }

    public void setOneParam(Field ff) {
        Param param = componGlob.getParam(ff.name);
        if (param != null) {
            componGlob.setParamValue(param, ff);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mComponent.moreWork != null) {
                mComponent.moreWork.receiverWork(intent);
            }
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver);
        }
    };

    public void setModel(Screen mComponent) {
        this.mComponent = mComponent;
    }

    @Override
    public void onStop() {
        if (mComponent != null && mComponent.moreWork != null) {
            mComponent.moreWork.stopScreen();
        }
        if (listInternetProvider != null) {
            for (BaseInternetProvider provider : listInternetProvider) {
                provider.cancel();
            }
        }
        mObject = null;
        super.onStop();
    }

    @Override
    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (resumePauseList != null) {
            for (OnResumePause rp : resumePauseList) {
                rp.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        if (resumePauseList != null) {
            for (OnResumePause rp : resumePauseList) {
                rp.onPause();
            }
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (resumePauseList != null) {
            for (OnResumePause rp : resumePauseList) {
                rp.onDestroy();
            }
        }
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void setFragmentsContainerId(int id) {

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
    public Field getProfile() {
        return componGlob.profile;
    }

    @Override
    public void backPressed() {
        activity.onBackPressed();
    }

    @Override
    public void backOk() {
        activity.backOk();
    }

    public boolean canBackPressed() {
        return isHideAnimatePanel();
    }

    @Override
    public BaseActivity getBaseActivity(){
        return (BaseActivity) getActivity();
    }

    @Override
    public BaseFragment getBaseFragment() {
        return this;
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
    public void addInternetProvider(BaseInternetProvider internetProvider) {
        listInternetProvider.add(internetProvider);
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

    public ParentModel getParentModel(String name) {
        if (parentModelList.size() > 0) {
            for (ParentModel pm : parentModelList) {
                if (pm.nameParentModel != null && pm.nameParentModel.equals(name)) {
                    return pm;
                }
            }
        }
        ParentModel pm = new ParentModel(name);
        parentModelList.add(pm);
        return pm;
    }

    public String getName() {
        return "Base";
    }

    public void setObject(Object o) {
        mObject = o;
    }

    public Object getObject() {
        return mObject;
    }

    @Override
    public View getParentLayout() {
        return parentLayout;
    }

    @Override
    public void startScreen(String screen, boolean startFlag, Object object, int forResult) {
        activity.startScreen(screen, startFlag, object, forResult);
    }

    @Override
    public void startScreen(String screen, boolean startFlag, Object object, int forResult, boolean addFragment) {
        activity.startScreen(screen, startFlag, object, forResult, addFragment);
    }

    @Override
    public void startScreen(String screen, boolean startFlag, Object object) {
        activity.startScreen(screen, startFlag, object);
    }

    @Override
    public void startScreen(String screen, boolean startFlag) {
        activity.startScreen(screen, startFlag, -1);
    }

    public void startFragment(String nameMVP, boolean startFlag,Object object) {
        activity.startFragment(nameMVP, activity.mapFragment.get(nameMVP), startFlag, object, -1, false);
    }

    @Override
    public void progressStart() {
        activity.progressStart();
    }

    @Override
    public void progressStop() {
        activity.progressStop();
    }


    @Override
    public void showDialog(String title, String message, View.OnClickListener click) {
        activity.showDialog(title, message, click);
    }

    @Override
    public void showDialog(int statusCode, String message, View.OnClickListener click) {
        activity.showDialog(statusCode, message, click);
    }

    @Override
    public boolean isViewActive() {
        return activity.isViewActive();
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

    public boolean noKeyBack() {
        if (mComponent.keyBack != 0) {
            View v = parentLayout.findViewById(mComponent.keyBack);
            if (v == null) {
                log("0009 Нет View для keyBack в " + mComponent.nameComponent);
            } else {
                navigatorClick.onClick(v);
            }
            return false;
        } else {
            return true;
        }
    }

}
