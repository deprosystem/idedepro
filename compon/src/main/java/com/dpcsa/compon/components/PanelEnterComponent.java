package com.dpcsa.compon.components;

import android.util.Log;
import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;

public class PanelEnterComponent extends BaseComponent {

    public PanelEnterComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        componentTag = "PANEL_ENTER_";
        viewComponent = (View) parentLayout.findViewById(paramMV.paramView.viewId);
        if (viewComponent == null) {
            iBase.log("0009 Не найдена панель в " + multiComponent.nameComponent);
        } else {
            if (paramMV.paramModel != null && paramMV.paramModel.method == paramMV.paramModel.FIELD) {
                workWithRecordsAndViews.RecordToView((Record) paramMV.paramModel.field.value,
                        viewComponent, this, clickView);
            } else {
                workWithRecordsAndViews.RecordToView(null, viewComponent, this, clickView);
            }
            if (navigator != null) {
                for (ViewHandler vh : navigator.viewHandlers) {
                    if (vh.mustValid != null && vh.changeEnabled) {
                        vh.validArray = new boolean[vh.mustValid.length];
                        for (int i = 0; i < vh.validArray.length; i++) {
                            int mv = vh.mustValid[i];
                            View view = viewComponent.findViewById(mv);
                            if (view != null && view instanceof IComponent) {
                                ((IComponent) view).setOnChangeStatusListener(statusListener);
                                vh.validArray[i] = false;
                            } else {
                                vh.validArray[i] = true;
                            }
                        }
                    }
                }
            }
        }
    }

    OnChangeStatusListener statusListener = new OnChangeStatusListener() {
        @Override
        public void changeStatus(View view, Object status) {
            int stat = (Integer) status;
            if (stat == 2 || stat == 3) {
                int viewId = view.getId();
                for (ViewHandler vh : navigator.viewHandlers) {
                    if (vh.mustValid != null && vh.changeEnabled) {
                        for (int i = 0; i < vh.validArray.length; i++) {
                            int mv = vh.mustValid[i];
                            if (mv == viewId) {
                                vh.validArray[i] = stat == 3;
                                break;
                            }
                        }
                        boolean enabled = true;
                        for (boolean bb : vh.validArray) {
                            if ( ! bb) {
                                enabled = false;
                                break;
                            }
                        }
                        View viewNav = viewComponent.findViewById(vh.viewId);
                        if (viewNav != null) {
                            viewNav.setEnabled(enabled);
                        }
                    }
                }
            }
        }
    };

    @Override
    public void changeData(Field field) {
        if (field != null && field.value != null && (field.type == Field.TYPE_RECORD )) {
            recordComponent = (Record) field.value;
            workWithRecordsAndViews.RecordToView(recordComponent, viewComponent, this, clickView);
        } else {

        }
    }

}
