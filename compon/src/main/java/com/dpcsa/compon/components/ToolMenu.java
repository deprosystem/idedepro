package com.dpcsa.compon.components;

import android.util.Log;
import android.view.Menu;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BaseFragment;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.ToolBarMenu;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import androidx.appcompat.widget.Toolbar;

public class ToolMenu extends BaseComponent {
    ToolBarComponent toolBar;
    Toolbar tool_XML;

    public ToolMenu(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        paramMV.startActual = false;
        BaseFragment bf = iBase.getBaseFragment();
        if (bf != null) {
            if (activity.toolBar != null) {
                toolBar = activity.toolBarC;
                tool_XML = toolBar.tool;
            } else {
                iBase.log("0013 ToolBar отсутствует в родительской активити " + multiComponent.nameComponent);
                return;
            }
        } else {
            iBase.log("0012 ToolMenu не может быть в Activity " + multiComponent.nameComponent);
            return;
        }
        bf.setToolMenu(this);
    }

    public boolean setMenu(Menu menu) {
        boolean cl = false;
        if (paramMV.paramView.booleanParams != null && paramMV.paramView.booleanParams.length > 0) {
            cl = paramMV.paramView.booleanParams[0];
        }
        if (cl) {
            menu.clear();
        }
        if (paramMV.paramModel != null && paramMV.paramModel.field != null) {
            ListRecords menuList = (ListRecords) paramMV.paramModel.field.value;
            if (menuList != null) {
                int ik = menuList.size();
                if (ik > 0) {
                    for (int i = 0; i < ik; i++) {
                        Record rec = menuList.get(i);
                        int id = rec.getInt("idField");
                        if (id > 0) {
                            int sh = rec.getInt("show");
                            if (rec.getBoolean("withText")) {
                                sh = sh | ToolBarMenu.ACTION_WITH_TEXT;
                            }
                            if (rec.getBoolean("collapseView")) {
                                sh = sh | ToolBarMenu.COLLAPSE_ACTION_VIEW;
                            }
                            menu.add(Menu.NONE, id, 0, rec.getInt("title"))
                                    .setIcon(rec.getInt("icon"))
                                    .setShowAsAction(sh);
                        } else {
                            if ( ! rec.getBoolean("visib") && ! cl) {
                                menu.getItem(-id).setVisible(false);
                            }
                        }
                    }
                    return true;
                }
            }

        }
        return true;
    }

        @Override
    public void changeData(Field field) {

    }
}
