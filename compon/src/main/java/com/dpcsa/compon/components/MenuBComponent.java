package com.dpcsa.compon.components;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.ComponMenuB;
import com.dpcsa.compon.custom_components.ComponRadioButtonRL;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IsetMenu;
import com.dpcsa.compon.interfaces_classes.Menu;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.presenter.ListPresenter;
import com.dpcsa.compon.tools.Constants;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MenuBComponent extends BaseComponent implements IsetMenu {
    public ComponMenuB menuB;
    public int countButton;
    public int selectStart;
    private Field fieldMenu;
    private Menu menu;
    static int colorNorm, colorSelect;
    float DENSITY = activity.getResources().getDisplayMetrics().density;
    float SP_DENSITY = activity.getResources().getDisplayMetrics().scaledDensity;
    float sp_10 = SP_DENSITY * 10;
    float sp_8 = SP_DENSITY * 8;
    int dp_10 = (int) (10 * DENSITY);
    int dp_24 = (int) (24f * DENSITY);
    ComponRadioButtonRL[] viewMenu;
    int imageLocale;
    public int selBackgr;
    public boolean noSelImgChangeColor, toAnimate;
    private boolean isActivity;

    public MenuBComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        isActivity = iBase.getBaseFragment() == null;
        if (paramMV.paramView != null && paramMV.paramView.viewId != 0) {
            menuB = (ComponMenuB) parentLayout.findViewById(paramMV.paramView.viewId);
        }
        if (menuB == null) {
            iBase.log("0009 Не найден ComponMenuB для Menu в " + multiComponent.nameComponent);
            return;
        }
        selBackgr = menuB.selBackgr;
        noSelImgChangeColor = menuB.noSelImgChangeColor;
        toAnimate = menuB.toAnimate;
        componentTag = "MENU_B_";
        if (isActivity) {
            activity.menuBottom = this;
        }
        imageLocale = menuB.imageLocale;
        listData = new ListRecords();
        listPresenter = new ListPresenter(this);
        menuB.setOrientation(LinearLayout.HORIZONTAL);
        menuB.setGravity(Gravity.CENTER);
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
        colorNorm = menuB.colorNorm;
        colorSelect = menuB.colorSelect;

        countButton = listData.size();
        viewMenu = new ComponRadioButtonRL[countButton];
        selectStart = preferences.getNameInt(componentTag + multiComponent.nameComponent, -1);

//selectStart = -1;

        int selectRadio = -1;
        for (int i = 0; i < countButton; i++) {
            Record rr = listData.get(i);
            if (rr.getBoolean(Menu.START)) {
                selectRadio = i;
            }
            ComponRadioButtonRL crb = newRadioButton(rr);
            menuB.addView(crb);
            viewMenu[i] = crb;
            crb.setOnClickListener(listener);
        }
        if (selectStart == -1) {
            selectStart = selectRadio;
        }
        if (selectStart == -1) {
            selectStart = 0;
        }
        if (isActivity) {
            if (activity.menuDraw != null) {
                String selScr = activity.menuDraw.getSelectScreen();
                int countButton = listData.size();
                int selPos = -1;
                for (int i = 0; i < countButton; i++) {
                    Record rr = listData.get(i);
                    String screen = rr.getString(Constants.NAME_FUNC);
                    if (screen != null && screen.equals(selScr)) {
                        selPos = i;
                        break;
                    }
                }
                if (selPos != -1) {
                    selectStart = selPos;
                    viewMenu[selectStart].setSelected(true);
                    preferences.setNameInt(componentTag + multiComponent.nameComponent, selectStart);
                }
            } else {
                viewMenu[selectStart].setSelected(true);
                startScreen(selectStart);
            }
        } else {
            viewMenu[selectStart].setSelected(true);
            startScreen(selectStart);
        }
        listPresenter.changeData(listData, selectStart);
    }

    public void syncMenu(String scr) {
        int countButton = listData.size();
        if (selectStart != -1) {
            viewMenu[selectStart].setSelected(false);
        }
        selectStart = -1;
        for (int i = 0; i < countButton; i++) {
            String screen = listData.get(i).getString(Constants.NAME_FUNC);
            if (scr.equals(screen)) {
                selectStart = i;
                viewMenu[selectStart].setSelected(true);
                return;
            }
        }
    }

    private ComponRadioButtonRL newRadioButton(Record rr) {
        ComponRadioButtonRL ll = new ComponRadioButtonRL(activity);
        LinearLayout.LayoutParams lpL = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, 1f);
        ll.setLayoutParams(lpL);
        ll.setColors(colorNorm, colorSelect, selBackgr, noSelImgChangeColor, toAnimate);
        ll.setImg(rr.getInt("icon"), imageLocale);
        ll.setText(rr.getInt("nameId"));
        return ll;
    }

    public void setItem(int position) {
        if (position > -1 && position < viewMenu.length) {
            selectStart = position;
        }
        viewMenu[selectStart].setSelected(true);
        if (isActivity) {
            activity.navigatorClick.onClick(viewMenu[selectStart]);
        }
        startScreen(selectStart);
    }

    public void setItem() {
        viewMenu[selectStart].setSelected(true);
        if (isActivity) {
            activity.navigatorClick.onClick(viewMenu[selectStart]);
        }
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
        int ik = listData.size();
        for (int i = 0; i < ik; i++) {
            String scr = listData.get(i).getString(Constants.NAME_FUNC);
            if (scr != null && scr.equals(screen)) {
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
                    if (selectStart != i) {
                        if (selectStart != -1) {
                            viewMenu[selectStart].setSelected(false);
                        }
                        selectStart = i;
                        viewMenu[selectStart].setSelected(true);
                        viewMenu[selectStart].animClick();
                    }
                    startScreen(selectStart);
                    return;
                }
            }
        }
    };

    private void startScreen(int position) {
        preferences.setNameInt(componentTag + multiComponent.nameComponent, position);
        String screen = "";
        if (listData.size() > position) {
            selectStart = position;
            screen = listData.get(position).getString(Constants.NAME_FUNC);
        }
        if (screen.length() > 0) {
            if (isActivity && activity.menuDraw != null) {
                activity.menuDraw.syncMenu(screen);
            }
//Log.d("QWERT","Menu startScreen screen="+screen+"<<");
            iBase.startScreen(screen, true);
        } else {
            if (paramMV.arrNavigator != null) {
                if (position < paramMV.arrNavigator.length) {
                    navigator = paramMV.arrNavigator[position];
                    if (navigator != null) {
                        clickHandler(menuB, 0);
                    } else {
                        iBase.log("0015 Не описаны действия для Menu в " + multiComponent.nameComponent);
                    }
                } else {
                    iBase.log("0015 Не описаны действия для Menu в " + multiComponent.nameComponent);
                }

            } else {
                iBase.log("0015 Не описаны действия для Menu в " + multiComponent.nameComponent);
            }
        }
    }

    @Override
    public void setMenu(int itemNew, int itemOld) {

    }
}
