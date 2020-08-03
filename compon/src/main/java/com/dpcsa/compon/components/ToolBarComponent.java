package com.dpcsa.compon.components;

import android.util.Log;
import android.view.View;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.ComponToolBar;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.ToolMenu;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

public class ToolBarComponent extends BaseComponent {
    public ComponToolBar tool;

    public ToolBarComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        tool = (ComponToolBar) parentLayout.findViewById(paramMV.paramView.viewId);
        if (tool == null) {
            iBase.log("Не найден ToolBar (RelativeLayout) в " + multiComponent.nameComponent);
            return;
        }
        iBase.setToolBar(this);
    }

    @Override
    public void changeData(Field field) {
        if (paramMV.toolMenu != null) {
            for (ToolMenu.ItemTool item : paramMV.toolMenu.listItem) {
                item.view = tool.addItem(item.viewId);
                item.view.setOnClickListener(click);
            }

//            for (ToolMenu.ItemTool item : paramMV.toolMenu.listItem) {
//                Log.d("QWERT","1111 VVVV="+item.view.getVisibility()+" III="+item.viewId);
//                item.view.setVisibility(View.GONE);
//                Log.d("QWERT","22222222 VVVV="+item.view.getVisibility()+" III="+item.viewId);
//            }
        }

        if (tool.BACK != null) {
            tool.BACK.setOnClickListener(clickBack);
        }

        if (tool.HAMBURGER != null) {
            tool.HAMBURGER.setOnClickListener(clickHamburger);
        }
    }

    public void setTitle(String name) {
        tool.setTitle(name);
    }

    View.OnClickListener clickHamburger = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            activity.openDrawer();
        }
    };

    View.OnClickListener clickBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            activity.onBackPressed();
        }
    };

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
Log.d("QWERT","CCCCCC="+v.getId());
            for (ToolMenu.ItemTool item : paramMV.toolMenu.listItem) {
                if (item.view == v) {
                    navigator = item.navigator;
                    clickHandler(v, 0);
                }
            }
        }
    };

    public void showView(boolean emptyStack) {
        if (emptyStack) {
            if (tool.BACK != null) {
                tool.BACK.setVisibility(View.GONE);
            }
            if (tool.HAMBURGER != null) {
                tool.HAMBURGER.setVisibility(View.VISIBLE);
            }
        } else {
            if (tool.BACK != null) {
                tool.BACK.setVisibility(View.VISIBLE);
            }
            if (tool.HAMBURGER != null) {
                tool.HAMBURGER.setVisibility(View.GONE);
            }
        }
        if (paramMV.toolMenu == null) return;
        for (ToolMenu.ItemTool item : paramMV.toolMenu.listItem) {
            if (emptyStack) {
                if (item.statusView == ToolMenu.STATUS_VIEW.NO_EMPTY_STACK) {
                    if (item.view.getVisibility() == View.VISIBLE) {
                        item.view.setVisibility(View.GONE);
                    }
                } else {
                    if (item.view.getVisibility() == View.GONE) {
                        item.view.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (item.statusView == ToolMenu.STATUS_VIEW.EMPTY_STACK) {
                    if (item.view.getVisibility() == View.VISIBLE) {
                        item.view.setVisibility(View.GONE);
                    }
                } else {
                    if (item.view.getVisibility() == View.GONE) {
                        item.view.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
}
