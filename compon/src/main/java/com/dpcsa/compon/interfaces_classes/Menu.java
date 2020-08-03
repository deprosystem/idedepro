package com.dpcsa.compon.interfaces_classes;

import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.tools.Constants;

public class Menu extends Field {

    public ListRecords menuList;
    public int menuStart;
    public enum TYPE {NORMAL, SELECT, DIVIDER, ENABLED};
    public int colorNorm, colorSelect, colorEnabl;
    public static String ICON = "icon", NAME_ID = "nameId", BADGE = "badge",
            SELECT = "select", START = "start", ENABLED = "enabled";

    public Menu() {
        this(0, 0, 0);
    }

    public Menu(int colorNorm, int colorSelect) {
        this(colorNorm, colorSelect, 0);
    }

    public Menu(int colorNorm, int colorSelect, int colorEnabl) {
        this.colorNorm = colorNorm;
        this.colorSelect = colorSelect;
        this.colorEnabl = colorEnabl;
        name = "menu";
        type = TYPE_LIST_FIELD;
        menuList = new ListRecords();
        value = menuList;
        menuStart = -1;
    }


    public Menu item(int icon, int title, String nameFragment) {
        return item(icon, title, nameFragment, false);
    }

    public Menu divider(){
        Record item = new Record();
        item.add(new Field(SELECT, Field.TYPE_INTEGER, 2));
        menuList.add(item);
        return this;
    }

    public Menu item(int icon, int title, String nameFragment, TYPE type) {
        Record item = new Record();
        item.add(new Field(ICON, Field.TYPE_INTEGER, icon));
        item.add(new Field(NAME_ID, Field.TYPE_INTEGER, title));
        item.add(new Field(Constants.NAME_FUNC, Field.TYPE_STRING, nameFragment));
        item.add(new Field(SELECT, Field.TYPE_INTEGER, type.ordinal()));
        item.add(new Field(BADGE, Field.TYPE_STRING, ""));
        menuList.add(item);
        return this;
    }

    public Menu item(int icon, int title, String nameFragment, boolean start) {
        Record item = new Record();
        item.add(new Field(ICON, Field.TYPE_INTEGER, icon));
        item.add(new Field(NAME_ID, Field.TYPE_INTEGER, title));
        item.add(new Field(Constants.NAME_FUNC, Field.TYPE_STRING, nameFragment));
        item.add(new Field(BADGE, Field.TYPE_STRING, ""));
        if (start && menuStart < 0) {
            item.add(new Field(SELECT, Field.TYPE_INTEGER, 1));
            menuStart = menuList.size();
            item.add(new Field(START, Field.TYPE_BOOLEAN, true));
        } else {
            item.add(new Field(SELECT, Field.TYPE_INTEGER, 0));
        }
        menuList.add(item);
        return this;
    }

    public Menu addField(String name, int type, Object value) {
        int i = menuList.size() - 1;
        menuList.get(i).add(new Field(name, type, value));
        return this;
    }

    public Menu enabled(int enable) {
        int i = menuList.size() - 1;
        if (i > -1 && menuList.get(i).getInt(SELECT) == 0) {
            menuList.get(i).add(new Field(ENABLED, Field.TYPE_INTEGER, enable));
        }
        return this;
    }

    public Menu badge(String value) {
        int i = menuList.size() - 1;
        if (i > -1) {
            Record rec = menuList.get(i);
            Field ff = rec.getField(BADGE);
            ff.value = value;
//            menuList.get(i).add(new Field(BADGE, Field.TYPE_STRING, Notice.PREFIX_PUSH + value));
        }
        return this;
    }
}
