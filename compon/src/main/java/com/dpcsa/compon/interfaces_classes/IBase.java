package com.dpcsa.compon.interfaces_classes;

import android.os.Bundle;
import android.view.View;

import com.dpcsa.compon.components.ToolBarComponent;
import com.google.android.gms.common.api.GoogleApiClient;

import com.dpcsa.compon.base.BaseActivity;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BaseFragment;
import com.dpcsa.compon.base.BaseInternetProvider;
import com.dpcsa.compon.json_simple.Field;

public interface IBase {
    BaseActivity getBaseActivity();
    BaseFragment getBaseFragment();
    PushHandler getPusHandler(PushHandler.TYPE pushType, int viewId);
    View getParentLayout();
    void addEvent(int sender, BaseComponent receiver);
    void addEvent(int[] senderList, BaseComponent receiver);
    void sendEvent(int sender);
    void sendActualEvent(int sender, Object paramEvent);
    ParentModel getParentModel(String name);
    Field getProfile();
    void startScreen(String screen, boolean startFlag, Object object, int forResult, boolean addFragment);
    void startScreen(String screen, boolean startFlag, Object object, int forResult);
    void startScreen(String screen, boolean startFlag, Object object);
    void startScreen(String screen, boolean startFlag);
    void startDrawerFragment(String screen, int containerFragmentId);
    void startPush();
    void backPressed();
    void progressStart();
    void progressStop();
    void showDialog(String title, String message, View.OnClickListener click);
    void showDialog(int statusCode, String message, View.OnClickListener click);
    boolean isViewActive();
    void setFragmentsContainerId(int id);
    Bundle getSavedInstanceState();
    void addInternetProvider(BaseInternetProvider internetProvider);
    void setGoogleApiClient(GoogleApiClient googleApiClient);
    void addAnimatePanel(AnimatePanel animatePanel);
    void delAnimatePanel(AnimatePanel animatePanel);
    Field getParamScreen();
    boolean isHideAnimatePanel();
    void log(String msg);
    void logNet(String msg);
    void logDB(String msg);
    void setResumePause(OnResumePause resumePause);
    void itemSetValue(int viewId, Object value);
    void setToolBar(ToolBarComponent toolBar);
}
