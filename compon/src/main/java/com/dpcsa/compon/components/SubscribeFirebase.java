package com.dpcsa.compon.components;

import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.ISwitch;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.tools.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SubscribeFirebase extends BaseComponent {

    private ISwitch iSwitch;
    private int viewId;
    private String namePref;
//    private ParamModel mSub, mUnsub;

    public SubscribeFirebase(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        componentTag = Constants.TAG_SUBSCRIBE_TOPIC;
        viewId = paramMV.paramView.viewId;
        viewComponent = parentLayout.findViewById(viewId);
        namePref = Constants.TAG_SUBSCRIBE_TOPIC + paramMV.st1;
        String param = "";
        iSwitch = (ISwitch) viewComponent;
        if (iSwitch == null) {
            iBase.log("0009 Не найден Switch в " + multiComponent.nameComponent);
            return;
        }
        iSwitch.setOnChangeListener(listener);
        iSwitch.setOnStatus(preferences.getNameBoolean(namePref));
    }

    @Override
    public void changeData(Field field) {

    }

    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) { // ON
                FirebaseMessaging.getInstance().subscribeToTopic(paramMV.st1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    preferences.setNameBoolean(namePref, true);
                                } else {
                                    iSwitch.setOnStatus(false);
                                }
                            }
                        });
            } else {    // OFF
                FirebaseMessaging.getInstance().unsubscribeFromTopic(paramMV.st1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    preferences.setNameBoolean(namePref, false);
                                } else {
                                    iSwitch.setOnStatus(true);
                                }
                            }
                        });
            }
            preferences.setNameBoolean(namePref, isChecked);
        }
    };
}
