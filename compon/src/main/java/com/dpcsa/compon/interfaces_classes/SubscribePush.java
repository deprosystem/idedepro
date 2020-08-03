package com.dpcsa.compon.interfaces_classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;

import com.dpcsa.compon.base.BasePresenter;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.tools.Constants;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.dpcsa.compon.param.ParamModel.POST;


public class SubscribePush extends SingleSetting{

    String url, namePref;
    ParamModel mSub;
    boolean auth;
    private BroadcastReceiver networkReceiver, authReceiver, pushReceiver;

    public SubscribePush(IBase iBase, ViewHandler vh) {
        super(iBase, vh);
        url = vh.nameFieldWithValue;
        auth = vh.blocked;
        namePref = Constants.TAG_SUBSCRIBE + url;
    }

    @Override
    public void set() {
        if (preferences.getNameBoolean(namePref)) {
            componGlob.addCountSettings();
        } else {
            if (setReceivers()) {
//                mSub = new ParamModel(POST, url);
//                if (componGlob.appParams.nameTokenPushInHeader.length() > 0) {
//
//                }

                mSub = new ParamModel(POST, url);
                if (componGlob.appParams.nameTokenPushInHeader.length() > 0) {
                    mSub.isHeaderPush = true;
                } else {
                    if (componGlob.appParams.nameTokenPush != null && componGlob.appParams.nameTokenPush.length() > 0) {
                        mSub.param = componGlob.appParams.nameTokenPush;
                    } else {
                        iBase.log("1003 Не определен способ передачи пуш токена на сервер в initialSettings");
                        return;
                    }
                }

                new BasePresenter(iBase, mSub, null, null, listener_sub);
            }
        }
    }

    private boolean setReceivers() {
        boolean result = true;
Log.d("QWERT","setReceivers componGlob.isOnline()="+componGlob.isOnline());
        if ( ! componGlob.isOnline()) {
            networkReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (componGlob.isOnline()) {
                        set();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            iBase.getBaseActivity().registerReceiver(networkReceiver, intentFilter);
            result = false;
        }

        if (auth && preferences.getSessionToken().length() == 0) {
            authReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (preferences.getSessionToken().length() > 0) {
                        set();
                    }
                }
            };
            LocalBroadcastManager.getInstance(context).registerReceiver(authReceiver,
                    new IntentFilter(componGlob.token.name));
            result = false;
        }

        if (preferences.getPushToken().length() == 0) {
            pushReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (preferences.getPushToken().length() > 0) {
                        set();
                    }
                }
            };
            LocalBroadcastManager.getInstance(context).registerReceiver(pushReceiver,
                    new IntentFilter(componGlob.pushToken.name));
            result = false;
        }
        return result;
    }

    IPresenterListener listener_sub = new IPresenterListener() {
        @Override
        public void onResponse(Field response) {
            preferences.setNameBoolean(namePref, true);
            componGlob.addCountSettings();
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
        }
    };

    @Override
    public void close() {
        if (networkReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(networkReceiver);
            networkReceiver = null;
        }

        if (authReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(authReceiver);
            authReceiver = null;
        }

        if (pushReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(pushReceiver);
            pushReceiver = null;
        }
    }

}
