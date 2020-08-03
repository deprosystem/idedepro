package com.dpcsa.compon.components;

import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.presenter.ListPresenter;
import com.dpcsa.compon.tools.Constants;

public class MenuBottomComponent extends BaseComponent {

    public RadioGroup radioGroup;
    public int countButton;
    public RadioButton[] viewMenu;
    private String componentTag = "MENU_BOTTOM_";
    public int selectStart, selectRadio;

    public MenuBottomComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.paramView != null && paramMV.paramView.viewId != 0) {
            radioGroup = (RadioGroup) parentLayout.findViewById(paramMV.paramView.viewId);
        }
        if (radioGroup == null) {
            iBase.log("Не найден RadioGroup для Menu в " + multiComponent.nameComponent);
            return;
        }
        countButton = radioGroup.getChildCount();
        viewMenu = new RadioButton[countButton];
        selectStart = preferences.getNameInt(componentTag + multiComponent.nameComponent, -1);
        selectRadio = -1;
        for (int i = 0; i < countButton; i++) {
            RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
            rb.setOnClickListener(listener);
            viewMenu[i] = rb;
            if (rb.isChecked()) {
                selectRadio = i;
            }
        }
        if (selectStart == -1) {
            selectStart = selectRadio;
        }
        if (selectStart == -1) {
            selectStart = 0;
        }
        viewMenu[selectStart].setChecked(true);
        activity.navigatorClick.onClick(viewMenu[selectStart]);
        startScreen(selectStart);
    }

    public int getSelectPosition() {
        return selectStart;
    }

    public void setItem(int position) {
        if (position > -1 && position < viewMenu.length) {
            selectStart = position;
        }
        viewMenu[selectStart].setChecked(true);
        activity.navigatorClick.onClick(viewMenu[selectStart]);
        startScreen(selectStart);
    }

    public void setItem() {
        viewMenu[selectStart].setChecked(true);
        activity.navigatorClick.onClick(viewMenu[selectStart]);
        startScreen(selectStart);
    }

    public void selectPunct(String name) {
        int pos = getSelectPush(name);
        if (pos > -1) {
            setItem(pos);
            listPresenter.ranCommand(ListPresenter.Command.SELECT, pos, null);
        }
    }

    public int getSelectPush(String screen) {
        int ik = paramMV.paramView.screens.length;
        for (int i = 0; i < ik; i++) {
            if (paramMV.paramView.screens[i].equals(screen)) {
                return i;
            }
        }
        return -1;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < countButton; i++) {
                if (viewMenu[i] == v) {
                    activity.navigatorClick.onClick(v);
                    startScreen(i);
                    return;
                }
            }
        }
    };

    private void startScreen(int position) {
        preferences.setNameInt(componentTag + multiComponent.nameComponent, position);
        String screen = "";
        if (paramMV.paramView.screens.length > position) {
            selectStart = position;
            screen = paramMV.paramView.screens[position];
        }
        iBase.startScreen(screen, true);
    }

    @Override
    public void changeData(Field field) {

    }
}
