package com.dpcsa.compon.components;

import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.RequiresApi;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.ToolBarMenu;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;

public class ToolBarComponent extends BaseComponent {

    public Toolbar tool;
    public boolean emptyStack;

    public ToolBarComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initView() {
        paramMV.startActual = false;
        tool = (Toolbar) parentLayout.findViewById(paramMV.paramView.viewId);
        if (tool == null) {
            iBase.log("Не найден ToolBar в " + multiComponent.nameComponent);
            return;
        }
        iBase.setToolBar(this);
    }

    @Override
    public void changeData(Field field) {
    }

    public void setParamToolbar() {
        tool.setNavigationOnClickListener(NavOnCl);
        int overIcon = paramMV.paramView.layoutTypeId[2];
        if (overIcon != 0) {
            tool.setOverflowIcon(activity.getResources().getDrawable(overIcon));
        }
    }

    View.OnClickListener NavOnCl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (emptyStack) {
                activity.openDrawer();
            } else {
                activity.onBackPressed();
            }
        }
    };

    public void navigatorMenu(int featureId, Menu menu) {

    }

    public boolean setMenu(Menu menu) {
        if (paramMV.paramModel != null && paramMV.paramModel.field != null) {
            ListRecords menuList = (ListRecords) paramMV.paramModel.field.value;
            if (menuList != null) {
                int ik = menuList.size();
                if (ik > 0) {
                    for (int i = 0; i < ik; i++) {
                        Record rec = menuList.get(i);
                        int sh = rec.getInt("show");
                        if (rec.getBoolean("withText")) {
                            sh = sh | ToolBarMenu.ACTION_WITH_TEXT;
                        }
                        if (rec.getBoolean("collapseView")) {
                            sh = sh | ToolBarMenu.COLLAPSE_ACTION_VIEW;
                        }
                        menu.add(Menu.NONE, rec.getInt("idField") * 10 + 100, 0, rec.getInt("title"))
                                .setIcon(rec.getInt("icon"))
                                .setShowAsAction(sh);
                    }
                    return true;
                }
            }

        }
        return false;
    }

    public void setTitle(String name) {
        tool.setTitle(name);
    }

    public String  getTitle() {
        return tool.getTitle().toString();
    }

    public void showView(boolean emptyStack) {
        if (emptyStack) {
            if (paramMV.paramView.layoutTypeId[1] != 0) {
                tool.setNavigationIcon(paramMV.paramView.layoutTypeId[1]);
            }
        } else {
            if (paramMV.paramView.layoutTypeId[0] != 0) {
                tool.setNavigationIcon(paramMV.paramView.layoutTypeId[0]);
            }
        }
        this.emptyStack = emptyStack;
    }
}
