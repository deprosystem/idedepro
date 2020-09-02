package com.dpcsa.compon.components;

import android.os.Handler;
import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BasePresenter;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

import static com.dpcsa.compon.interfaces_classes.ViewHandler.TYPE.AFTER;

public class AutoAutch extends BaseComponent {


    public AutoAutch(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        paramMV.startActual = false;
        int isc = preferences.getAutoAutch();
        if (isc == 0) {
            selectViewHandler = new ViewHandler(AFTER);
            selectViewHandler.afterResponse = paramMV.after;
            new BasePresenter(iBase, paramMV.paramModel, paramMV.paramModel.head, paramMV.paramModel.record, listener_send_back_screen);
        } else {
            iBase.startScreen(paramMV.name, false);
            handler.postDelayed(fin, 100);
        }
    }

    Handler handler = new Handler();

    Runnable fin = new Runnable() {
        @Override
        public void run() {
            activity.finish();
        }
    };

    @Override
    public void changeData(Field field) {

    }

    @Override
    public void afterAfter() {
        preferences.setAutoAutch(1);
        iBase.startScreen(paramMV.name, false);
        handler.postDelayed(fin, 100);
    }
}
