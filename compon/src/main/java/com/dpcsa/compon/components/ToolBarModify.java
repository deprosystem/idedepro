package com.dpcsa.compon.components;

import android.util.Log;
import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.ComponToolBar;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.ModifierTool;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ToolBarModify extends BaseComponent {
    ToolBarComponent toolBar;
    ComponToolBar tool_XML;

    public ToolBarModify(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (iBase.getBaseFragment() != null) {
            if (activity.toolBar != null) {
                toolBar = activity.toolBar;
                tool_XML = toolBar.tool;
                for (ModifierTool mod : paramMV.modifierTools) {
Log.d("QWERT","mod.type="+mod.type);
                    switch (mod.type) {
                        case UN_VISIBLE:
                            setVisible(GONE, mod);
                            break;
                        case VISIBLE:
                            setVisible(VISIBLE, mod);
                            break;
                    }
                }
            } else {
                iBase.log("0013 ToolBar отсутствует в родительской активити " + multiComponent.nameComponent);
            }
        } else {
            iBase.log("0012 ToolBarModify не может быть в активити " + multiComponent.nameComponent);
        }
    }

    private void setVisible(int vis, ModifierTool mod) {
        for (int i : mod.ind) {
            Log.d("QWERT","IIIiiiI="+i);
            for (ComponToolBar.ItemView it: tool_XML.listView) {
                Log.d("QWERT","     it.id="+it.viewId);
                if (it.viewId == i ) {
                    Log.d("QWERT","======= it.view.getVisibility()="+it.view.getVisibility());
                    if (it.view.getVisibility() != vis) {
                        Log.d("QWERT","!!!!!!!!!!!!!!!!!!");
                        it.view.setVisibility(vis);
                    }
                    Log.d("QWERT","+++++++ it.view.getVisibility()="+it.view.getVisibility());
                    break;
                }
            }
        }
    }

    @Override
    public void changeData(Field field) {

    }
}
