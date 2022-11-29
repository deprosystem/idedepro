package com.dpcsa.compon.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import com.dpcsa.compon.R;
import com.dpcsa.compon.base.BaseActivity;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BaseProvider;
import com.dpcsa.compon.base.BaseProviderAdapter;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.ElementMenu;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IsetMenu;
import com.dpcsa.compon.interfaces_classes.Menu;
import com.dpcsa.compon.interfaces_classes.OnResumePause;
import com.dpcsa.compon.interfaces_classes.PushHandler;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.presenter.ListPresenter;
import com.dpcsa.compon.tools.Constants;

import static com.dpcsa.compon.interfaces_classes.PushHandler.TYPE.SELECT_MENU;

public class MenuComponent extends BaseComponent implements IsetMenu {
    ElementMenu recycler;
    ListRecords listData;
    BaseProviderAdapter adapter;
    private String componentTag = "MENU_";
    private String fieldType = "select";
    int colorNorm, colorSelect, colorEnabl, colorDivider, colorBadge;
    boolean isBaseItem;
    boolean isEnabled = false;
    BroadcastReceiver tokenMessageReceiver = null;
    private Field fieldMenu;
    private Menu menu;
    private int selectStart;

    public MenuComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        recycler = (ElementMenu) parentLayout.findViewById(paramMV.paramView.viewId);
        if (recycler == null) {
            iBase.log("0009 Не найден ElementMenu для Menu в " + multiComponent.nameComponent);
            return;
        }
        if (navigator != null) {
            for (ViewHandler vh : navigator.viewHandlers) {
                if (vh.viewId == 0 && vh.type == ViewHandler.TYPE.FIELD_WITH_NAME_SCREEN) {
                    selectViewHandler = vh;
                    break;
                }
            }
        } else {
            iBase.log("0009 Нет навигатора для Menu в " + multiComponent.nameComponent);
        }
        colorNorm = recycler.colorNorm;
        colorSelect = recycler.colorSelect;
        colorEnabl = recycler.colorEnabl;
        colorDivider = recycler.colorDivider;
        colorBadge = recycler.colorBadge;
        isBaseItem = false;
        paramMV.paramView.fieldType = fieldType;
        if (paramMV.paramView.layoutTypeId == null) {
            isBaseItem = true;
            paramMV.paramView.layoutTypeId = new int[]{R.layout.item_menu_base, R.layout.item_menu_base,
                    R.layout.item_menu_divider_base, R.layout.item_menu_base};
        }
        activity.menuDraw = this;
        listData = new ListRecords();
        listPresenter = new ListPresenter(this);
        provider = new BaseProvider(listData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recycler.setLayoutManager(layoutManager);
        adapter = new BaseProviderAdapter(this);
        recycler.setAdapter(adapter);
    }

    @Override
    public void changeData(Field field) {
        if (field == null) return;
        fieldMenu = field;
        if (field instanceof Menu) {
            menu = (Menu) field;
        }
        listData.clear();
        listData.addAll((ListRecords) field.value);
//        colorNorm = getThemeColor("colorPrimary");
//        colorSelect = getThemeColor("colorPrimaryDark");
//        colorEnabl = 0xffbbbbbb;
//        if (menu != null) {
//            if (menu.colorNorm != 0 && menu.colorSelect != 0 && menu.colorEnabl != 0) {
//                colorNorm = activity.getResources().getColor(menu.colorNorm);
//                colorSelect = activity.getResources().getColor(menu.colorSelect);
//                colorEnabl = activity.getResources().getColor(menu.colorEnabl);
//            }
//        }
        provider.setData(listData);
        int ik = listData.size();
        isEnabled = false;

        for (int i = 0; i < ik; i++) {      // визначається текст по його ід
            Record r = listData.get(i);
            int stId = r.getInt("nameId");
            if (stId != 0) {
                Field ff = r.getField("name");
                if (ff == null) {
                    r.add(new Field("name", Field.TYPE_STRING, activity.getString(stId)));
                } else {
                    ff.value = activity.getString(stId);
                }
            }
            if (r.getInt("enabled") > 0) {
                isEnabled = true;
            }
        }

        if (isEnabled) {
            if (tokenMessageReceiver == null) {
                tokenMessageReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        changeData(fieldMenu);
                    }
                };
                iBase.setResumePause(resumePause);
                LocalBroadcastManager.getInstance(activity).registerReceiver(tokenMessageReceiver,
                        new IntentFilter(componGlob.token.name));
            }
            changeView();
        }

        PushHandler ph = iBase.getPusHandler(SELECT_MENU, paramMV.paramView.viewId);
        if (ph != null) {
            selectStart = getSelectPush(ph.screen);
            if ( ! ph.continuePush) {
                preferences.setPushType("");
            }
        } else {
            selectStart = -1;
        }
        if (selectStart == -1) {
            selectStart = preferences.getNameInt(componentTag + multiComponent.nameComponent, -1);
        }
        if (selectStart >= ik) {
            selectStart = -1;
        }
        if (selectStart == -1) {
            for (int i = 0; i < ik; i++) {
                Record r = listData.get(i);
//                int j = (Integer) r.getValue(fieldType);
                if (r.getBoolean("start")) {
                    selectStart = i;
                    break;
                }
            }
        } else {
            Record r = listData.get(selectStart);
            Field ft = r.getField(fieldType);
            int type = (int) ft.value;
            if (type != 1) {
                if (type == 3) {
                    Record rr;
                    for (int i = 0; i < ik; i++) {
                        rr = listData.get(i);
                        Field frr = rr.getField(fieldType);
                        if ((int) frr.value == 1) {
                            frr.value = 0;
                        }
                    }
                    if (menu != null && menu.menuStart > -1) {
                        listData.get(menu.menuStart).getField(fieldType).value = 1;
                        selectStart = menu.menuStart;
                    } else {
                        for (int i = 0; i < ik; i++) {
                            rr = listData.get(i);
                            Field frr = rr.getField(fieldType);
                            if ((int) frr.value == 0) {
                                frr.value = 1;
                                selectStart = i;
                                break;
                            }
                        }
                    }
                } else {
                    for (int i=0; i < ik; i++) {
                        Record rrr = listData.get(i);
                        Field fff = rrr.getField(fieldType);
                        if ((int) fff.value == 1) {
                            fff.value = 0;
                        }
                    }
                    ft.value = 1;
                }
            }
        }
        listPresenter.changeData(listData, selectStart);
        adapter.notifyDataSetChanged();
    }

    private void changeView() {
        int ik = listData.size();
        boolean isToken = componGlob.token != null && ((String)componGlob.token.value).length() > 0;
        for (int i = 0; i < ik; i++) {
            Record r = listData.get(i);
            if (r.getInt("enabled") > 0) {
                Field ff = r.getField(fieldType);
                if (((int) ff.value) == 1 && ! isToken) {
                    selectStart = -1;
                }
                if ( ! isToken) {
                    ff.value = 3;
                } else {
                    ff.value = 0;
                }
            }
        }
    }

// Синхронизация меню начало
    public String getSelectScreen() {
        Record record = listData.get(selectStart);
        String screen = (String) record.getField(selectViewHandler.nameFieldScreen).value;
        return screen;
    }

    public void syncMenu(String scr) {
        int ik = listData.size();
        Record r;
        Field ft;
        if (selectStart > -1) {
            r = listData.get(selectStart);
            ft = r.getField(fieldType);
            ft.value = new Integer(0);
            adapter.notifyItemChanged(selectStart);
        }
        selectStart = -1;
        for (int i = 0; i < ik; i++) {
            Record record = listData.get(i);
            ft = record.getField(selectViewHandler.nameFieldScreen);
            if (ft == null) {
                continue;
            }
//            String screen = (String) record.getField(selectViewHandler.nameFieldScreen).value;
            String screen = (String) ft.value;
            if (scr.equals(screen)) {
                selectStart = i;
                preferences.setNameInt(componentTag + multiComponent.nameComponent, selectStart);
                r = listData.get(selectStart);
                ft = r.getField(fieldType);
                ft.value = 1;
                adapter.notifyItemChanged(selectStart);
                return;
            }
        }
    }
// Синхронизация меню окончание
    OnResumePause resumePause = new OnResumePause() {
        @Override
        public void onResume() {

        }

        @Override
        public void onPause() {

        }

        @Override
        public void onDestroy() {
            if (tokenMessageReceiver != null) {
                LocalBroadcastManager.getInstance(activity).unregisterReceiver(tokenMessageReceiver);
                tokenMessageReceiver = null;
            }
        }
    };

    public void selectPunct(String name) {
        int pos = getSelectPush(name);
        if (pos > -1) {
            listPresenter.ranCommand(ListPresenter.Command.SELECT, pos, null);
        }
    }

    public int getSelectPush(String screen) {
        int ik = listData.size();
        for (int i = 0; i < ik; i++) {
            Record record = listData.get(i);
            Field ff = record.getField(Constants.NAME_FUNC);
            if (ff != null) {
                String st = (String) ff.value;
                if (st != null && st.equals(screen)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void changeDataPosition(int position, boolean select) {
        adapter.notifyItemChanged(position);
        activity.closeDrawer();
        preferences.setNameInt(componentTag + multiComponent.nameComponent, position);
        if (select && selectViewHandler != null) {
            selectStart = position;
            Record record = listData.get(position);
            componGlob.setParam(record);
            String screen = (String) record.getField(selectViewHandler.nameFieldScreen).value;
            if (screen != null && screen.length() > 0) {
                if (activity.menuBottom != null) {
                    activity.menuBottom.syncMenu(screen);
                }
                iBase.startScreen(screen, true);
            }
        } else {
            if (selectStart > -1) {
                Record r = listData.get(selectStart);
                Field ft = r.getField(fieldType);
                ft.value = new Integer(0);
            }
            selectStart = position;
            adapter.notifyItemChanged(selectStart);
        }
    }

    public void setColor(int position, Record record, RecyclerView.ViewHolder holder) {
        int type = record.getInt(fieldType);
        ImageView img = (ImageView) componGlob.findViewByName(holder.itemView, "icon");
        TextView txt = (TextView) componGlob.findViewByName(holder.itemView, "name");
        switch (type) {
            case 0:     // norm
                setColors(img, colorNorm);
                txt.setTextColor(colorNorm);
                holder.itemView.setBackgroundColor(0x00000000);
                break;
            case 1:     // select
                setColors(img, colorSelect);
                txt.setTextColor(colorSelect);
                holder.itemView.setBackgroundColor(0x0a000000);
                break;
            case 3:     // enabled
                setColors(img, colorEnabl);
                txt.setTextColor(colorEnabl);
                holder.itemView.setBackgroundColor(0x00000000);
                break;
        }
    }

    public void setColors(ImageView img, int color) {
        ColorStateList stateList = new ColorStateList(
                new int[][]{
                        new int[]{}
                },
                new int[] {color}
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            img.setImageTintList(stateList);
        } else {
            img.setImageDrawable(tintIcon(img.getDrawable(), stateList));
        }
    }

    public Drawable tintIcon(Drawable icon, ColorStateList colorStateList) {
        if(icon!=null) {
            icon = DrawableCompat.wrap(icon).mutate();
            DrawableCompat.setTintList(icon, colorStateList);
            DrawableCompat.setTintMode(icon, PorterDuff.Mode.SRC_IN);
        }
        return icon;
    }

    public int getThemeColor (String nameColor) {
        int colorAttr = activity.getResources().getIdentifier(nameColor, "attr", activity.getPackageName());
        TypedValue value = new TypedValue ();
        activity.getTheme ().resolveAttribute (colorAttr, value, true);
        return value.data;
    }

    @Override
    public void setMenu(int itemNew, int itemOld) {
        if (itemOld < 0 || itemOld != selectStart) {

        }
    }
}
