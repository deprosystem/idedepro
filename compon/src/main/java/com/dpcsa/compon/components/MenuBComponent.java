package com.dpcsa.compon.components;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.ComponMenuB;
import com.dpcsa.compon.custom_components.ComponRadioButton;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.Menu;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.tools.Constants;

import androidx.core.graphics.drawable.DrawableCompat;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MenuBComponent extends BaseComponent {
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
    ComponRadioButton[] viewMenu;

    public MenuBComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (iBase.getBaseFragment() != null) {
            iBase.log("0015 Нижнее меню может быть только в активности " + multiComponent.nameComponent);
            return;
        }
        if (paramMV.paramView != null && paramMV.paramView.viewId != 0) {
            menuB = (ComponMenuB) parentLayout.findViewById(paramMV.paramView.viewId);
        }
        if (menuB == null) {
            iBase.log("Не найден ComponMenuB для Menu в " + multiComponent.nameComponent);
            return;
        }
        componentTag = "MENU_B_";
        activity.menuBottom = this;

        listData = new ListRecords();
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
        colorNorm = 0xffff0000;
        colorSelect = 0xff00ff00;
         if (menu != null) {
            if (menu.colorNorm != 0 && menu.colorSelect != 0) {
                colorNorm = activity.getResources().getColor(menu.colorNorm);
                colorSelect = activity.getResources().getColor(menu.colorSelect);
            }
        }
        countButton = listData.size();
        viewMenu = new ComponRadioButton[countButton];
        selectStart = preferences.getNameInt(componentTag + multiComponent.nameComponent, -1);
        int selectRadio = -1;
        for (int i = 0; i < countButton; i++) {
            Record rr = listData.get(i);
            if (rr.getBoolean(Menu.START)) {
                selectRadio = i;
            }
            ComponRadioButton crb = newRadioButton(rr);
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
            if ( ! activity.isDrawerComponent()) {
                viewMenu[selectStart].setSelected(true);
                startScreen(selectStart);
            }
        }
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

    private ComponRadioButton newRadioButton(Record rr) {
        ComponRadioButton ll = new ComponRadioButton(activity);
        LinearLayout.LayoutParams lpL = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, 1f);
        ll.setLayoutParams(lpL);
        ll.setColors(colorNorm, colorSelect);
        ll.setImg(rr.getInt("icon"));
        ll.setText(rr.getInt("nameId"));
        return ll;
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
        if (activity.menuDraw != null) {
            activity.menuDraw.syncMenu(screen);
        }
        iBase.startScreen(screen, true);
    }

    public int getThemeColor (String nameColor) {
        int colorAttr = activity.getResources().getIdentifier(nameColor, "attr", activity.getPackageName());
        TypedValue value = new TypedValue ();
        activity.getTheme().resolveAttribute (colorAttr, value, true);
        return value.data;
    }

    private void setSelectorIMG(ImageView img) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            img.setImageTintList(selectorImage());
        } else {
            img.setImageDrawable(tintIcon(img.getDrawable(), selectorImage()));
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

    public static ColorStateList selectorImage() {
        ColorStateList selectorImage = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_selected},
                        new int[]{}
                },
                new int[] {
                        colorNorm,
                        colorSelect
                }
        );
        return selectorImage;
    }
}
