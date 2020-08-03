package com.dpcsa.compon.components;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.IValidate;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.interfaces_classes.OnResumePause;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

public class EnabledComponent extends BaseComponent {

    public boolean[] validArray;
    private boolean isValid;
    private int isToken = Integer.MAX_VALUE;
    private int isPushToken = isToken - 1;
    private BroadcastReceiver tokenMessageReceiver = null;
    private BroadcastReceiver pushTokenMessageReceiver = null;

    public EnabledComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.paramView != null || paramMV.paramView.viewId != 0) {
            viewComponent = parentLayout.findViewById(paramMV.paramView.viewId);
        }
        if (viewComponent == null) {
            iBase.log("Не найден View для EnabledComponent в " + multiComponent.nameComponent);
            return;
        }
        if (paramMV.mustValid != null) {
            int ik = paramMV.mustValid.length;
            validArray = new boolean[ik];
            isValid = true;
            for (int i = 0; i < ik; i++) {
                if (paramMV.mustValid[i] == isToken) {
                    boolean token = componGlob.token != null && ((String)componGlob.token.value).length() > 0;
                    if ( ! token) {
                        isValid = false;
                    }
                    validArray[i] = token;
                    if (tokenMessageReceiver == null) {
                        tokenMessageReceiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                statusTokenChange();
                            }
                        };
                        iBase.setResumePause(resumePause);
                        LocalBroadcastManager.getInstance(activity).registerReceiver(tokenMessageReceiver,
                                new IntentFilter(componGlob.token.name));
                    }
                    continue;
                }
                if (paramMV.mustValid[i] == isPushToken) {
                    boolean pushToken = componGlob.pushToken != null && ((String)componGlob.pushToken.value).length() > 0;
                    if ( ! pushToken) {
                        isValid = false;
                    }
                    validArray[i] = pushToken;
                    if (pushTokenMessageReceiver == null) {
                        pushTokenMessageReceiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                statusPushTokenChange();
                            }
                        };
                        iBase.setResumePause(resumePause);
                        LocalBroadcastManager.getInstance(activity).registerReceiver(pushTokenMessageReceiver,
                                new IntentFilter(componGlob.pushToken.name));
                    }
                    continue;
                }
                View v = parentLayout.findViewById(paramMV.mustValid[i]);
                if (v != null && v instanceof IComponent) {
                    ((IComponent) v).setOnChangeStatusListener(statusListener);
                    if (v instanceof IValidate) {
                        boolean vv = ((IValidate) v).isValid();
                        if ( ! vv) {
                            isValid = false;
                        }
                        validArray[i] = vv;
                    } else {
                        validArray[i] = false;
                        isValid = false;
                    }
                } else {
                    if (v instanceof RecyclerView) {
                        RecyclerComponent rc = (RecyclerComponent) getComponent(paramMV.mustValid[i]);
                        rc.setOnChangeStatusListener(statusListener);
                        boolean vv = rc.isValid();
                        if ( ! vv) {
                            isValid = false;
                        }
                        validArray[i] = vv;
                    } else {
                        validArray[i] = true;
                    }
                }
            }
        }
        viewComponent.setEnabled(isValid);
    }

    private void statusTokenChange() {
        int ik = paramMV.mustValid.length;
        for (int i = 0; i < ik; i++) {
            if (paramMV.mustValid[i] == isToken) {
                validArray[i] = componGlob.token != null && ((String)componGlob.token.value).length() > 0;
                break;
            }
        }
        setEnab();
    }

    private void statusPushTokenChange() {
        int ik = paramMV.mustValid.length;
        for (int i = 0; i < ik; i++) {
            if (paramMV.mustValid[i] == isPushToken) {
                validArray[i] = componGlob.pushToken != null && ((String)componGlob.pushToken.value).length() > 0;
                break;
            }
        }
        setEnab();
    }

    @Override
    public void changeData(Field field) {

    }

    OnChangeStatusListener statusListener = new OnChangeStatusListener() {
        @Override
        public void changeStatus(View view, Object status) {
            int stat = (Integer) status;
            if (stat == 2 || stat == 3) {
                int viewId = view.getId();
                int ik = paramMV.mustValid.length;
                for (int i = 0; i < ik; i++) {
                    if (paramMV.mustValid[i] == viewId) {
                        validArray[i] = stat == 3;
                        break;
                    }
                }
                setEnab();
            }
        }
    };

    private void setEnab() {
        isValid = true;
        for (boolean bb : validArray) {
            if ( ! bb) {
                isValid = false;
                break;
            }
        }
        viewComponent.setEnabled(isValid);
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
            if (tokenMessageReceiver != null) {
                LocalBroadcastManager.getInstance(activity).unregisterReceiver(tokenMessageReceiver);
                tokenMessageReceiver = null;
            }
            if (pushTokenMessageReceiver != null) {
                LocalBroadcastManager.getInstance(activity).unregisterReceiver(pushTokenMessageReceiver);
                pushTokenMessageReceiver = null;
            }
        }
    };
}
