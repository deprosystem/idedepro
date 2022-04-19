package com.dpcsa.compon.interfaces_classes;

import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;

public class ToolBarMenu extends Field {

    public ListRecords menuList;
    public static String ICON = "icon", TITLE = "title", SHOW = "show",
            WITH_TEXT = "withText", VISIB = "visib", ID = "idField", COLLAPSE = "collapseView";
    public static int ACTION_ALWAYS = 2, ACTION_IF_ROOM = 1, ACTION_NEVER = 0,
            ACTION_WITH_TEXT = 4, COLLAPSE_ACTION_VIEW = 8;

    public ToolBarMenu() {
        name = "menu";
        type = TYPE_LIST_FIELD;
        menuList = new ListRecords();
        value = menuList;
    }

    public ToolBarMenu item(int idField, int icon, int title, int show, boolean withText,
                            boolean collapseView, boolean visib) {
        Record item = new Record();
        item.add(new Field(ID, Field.TYPE_INTEGER, idField));
        item.add(new Field(ICON, Field.TYPE_INTEGER, icon));
        item.add(new Field(TITLE, Field.TYPE_INTEGER, title));
        item.add(new Field(SHOW, Field.TYPE_INTEGER, show));
        item.add(new Field(WITH_TEXT, Field.TYPE_BOOLEAN, withText));
        item.add(new Field(COLLAPSE, Field.TYPE_BOOLEAN, collapseView));
        item.add(new Field(VISIB, Field.TYPE_BOOLEAN, visib));
        menuList.add(item);
        return this;
    }
}
