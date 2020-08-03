package com.dpcsa.compon.components;

import android.view.View;
import android.widget.CompoundButton;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BasePresenter;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.interfaces_classes.ISwitch;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.tools.Constants;

import static com.dpcsa.compon.param.ParamModel.POST;

public class SubscribeComponent extends BaseComponent {

    private ISwitch iSwitch;
    private int viewId;
    private String namePref;
    private ParamModel mSub, mUnsub;

    public SubscribeComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        componentTag = Constants.TAG_SUBSCRIBE;
        viewId = paramMV.paramView.viewId;
        viewComponent = parentLayout.findViewById(viewId);
        namePref = Constants.TAG_SUBSCRIBE + paramMV.st1;
        String param = "";
        iSwitch = (ISwitch) viewComponent;
        if (iSwitch == null) {
            iBase.log("0009 Не найден Switch в " + multiComponent.nameComponent);
            return;
        }
        mSub = new ParamModel(POST, paramMV.st1);
        mUnsub = new ParamModel(POST, paramMV.st2);
        if (componGlob.appParams.nameTokenPushInHeader.length() > 0) {
            mSub.isHeaderPush = true;
            mUnsub.isHeaderPush = true;
        } else {
            if (componGlob.appParams.nameTokenPush != null && componGlob.appParams.nameTokenPush.length() > 0) {
                mSub.param = componGlob.appParams.nameTokenPush;
                mUnsub.param = componGlob.appParams.nameTokenPush;
            } else {
                iBase.log("1003 Не определен способ передачи пуш токена на сервер в " + multiComponent.nameComponent);
                return;
            }
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
                new BasePresenter(iBase, mSub, null, null, listener_sub);
            } else {    // OFF
                new BasePresenter(iBase, mUnsub, null, null, listener_unsub);
            }
            preferences.setNameBoolean(namePref, isChecked);
        }
    };

    IPresenterListener listener_sub = new IPresenterListener() {

        @Override
        public void onResponse(Field response) {
            preferences.setNameBoolean(namePref, true);
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
            iSwitch.setOnStatus(false);
        }
    };

    IPresenterListener listener_unsub = new IPresenterListener() {

        @Override
        public void onResponse(Field response) {
            preferences.setNameBoolean(namePref, false);
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
            iSwitch.setOnStatus(true);
        }
    };
}
