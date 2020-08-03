package com.dpcsa.compon.components;

import android.content.Context;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;

public class PopUpComponent extends BaseComponent {

    View view;
    public PopupMenu popupMenu;

    public PopUpComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        view = parentLayout.findViewById(paramMV.paramView.viewId);
        if (view == null) {
            iBase.log( "Не найден View в PopUpComponent " + multiComponent.nameComponent);
            return;
        }
        listData = new ListRecords();
        view.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popupMenu.show();
        }
    };

    @Override
    public void changeData(Field field) {
        if (listData == null) return;
        listData.clear();
        listData.addAll((ListRecords) field.value);
        Context wrapper = new ContextThemeWrapper(activity, paramMV.paramView.tabId);
        popupMenu = new PopupMenu(wrapper, view);
        Menu m = popupMenu.getMenu();
        int ik = listData.size();
        for (int i = 0; i < ik; i++) {
            m.add(0, i, i, listData.get(i).getString(paramMV.paramView.fieldType));
        }
        popupMenu.setOnMenuItemClickListener(menuItemClick);
    }

    PopupMenu.OnMenuItemClickListener menuItemClick = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = item.getItemId();
            Record rec = listData.get(position);
            clickItem.onClick(null, null, position, rec);
//            iBase.customClickListenet(paramMV.paramView.viewId, position, rec);
            return false;
        }
    };
}
