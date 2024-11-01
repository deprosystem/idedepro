package com.dpcsa.compon.components;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.ComponImageView;
import com.dpcsa.compon.glide.GlideApp;
import com.dpcsa.compon.glide.GlideRequest;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.circleCropTransform;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class PanelComponent extends BaseComponent {

    private View[] v_splash;
    @Override
    public void initView() {
Log.d("QWERT","PANEL initView");
        componentTag = "PANEL_";
        viewComponent = parentLayout.findViewById(paramMV.paramView.viewId);
        if (viewComponent == null) {
            iBase.log("0009 Не найдена панель в " + multiComponent.nameComponent);
        } else {
// Щоб кліки не проходили через панель, бо вона може бути над іншою зі своїми єлементами
            viewComponent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            if (navigator != null && navigator.viewHandlers.size() > 0) {
                for (ViewHandler vh : navigator.viewHandlers) {
                    if (vh.viewId == 0) {
                        viewComponent.setOnClickListener(clickView);
                        break;
                    }
                }
            }

// Якщо но дате. Але мабуть краще зробити лише одне View, а не багато
            int[] splash = paramMV.paramView.splashScreenViewId;
            if (splash != null && splash.length > 0) {
                v_splash = new View[splash.length];
                for (int i = 0; i < splash.length; i++) {
                    v_splash[i] = parentLayout.findViewById(splash[i]);
                    v_splash[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
            }
        }
    }

    @Override
    public void changeData(Field field) {
Log.d("QWERT","PANEL changeData");
        if (field == null) {
            recordComponent = null;
            showView();
            return;
        }
        if (field.value instanceof ListRecords) {
            ListRecords lr = (ListRecords) field.value;
            if (lr. size() > 0) {
                recordComponent = lr.get(0);
                showView();
            } else {
                iBase.log("Тип данных не Record в " + multiComponent.nameComponent);
            }
        } else {
            if (field.value instanceof Record) {
                recordComponent = (Record) field.value;
                showView();
            } else {
                iBase.log("Тип данных не Record в " + multiComponent.nameComponent);
            }
        }
    }

    private void showView() {
Log.d("QWERT","PANEL showView");
        if (v_splash != null && v_splash.length > 0) {
            for (int i = 0; i < v_splash.length; i++) {
                if (v_splash[i] != null) {
                    if (recordComponent != null && recordComponent.size() > 0) {
                        workWithRecordsAndViews.RecordToView(recordComponent, viewComponent, this, clickView);
                        v_splash[i].setVisibility(View.GONE);
                        viewComponent.setVisibility(View.VISIBLE);
                    } else {
                        viewComponent.setVisibility(View.GONE);
                        v_splash[i].setVisibility(View.VISIBLE);
                    }
                } else {
                    if (recordComponent != null && recordComponent.size() > 0) {
                        workWithRecordsAndViews.RecordToView(recordComponent, viewComponent, this, clickView);
                    }
                }
            }
        } else {
            workWithRecordsAndViews.RecordToView(recordComponent, viewComponent, this, clickView);
        }
    }

    public PanelComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }
}
