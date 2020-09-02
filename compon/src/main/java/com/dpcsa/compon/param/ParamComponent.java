package com.dpcsa.compon.param;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.Filters;
import com.dpcsa.compon.interfaces_classes.ModifierTool;
import com.dpcsa.compon.interfaces_classes.MoreWork;
import com.dpcsa.compon.interfaces_classes.Multiply;
import com.dpcsa.compon.interfaces_classes.Navigator;
import com.dpcsa.compon.interfaces_classes.ToolMenu;

public class ParamComponent <T>{
    public static enum TC {PANEL, PANEL_ENTER,
        SPINNER, DRAWER, PLUS_MINUS, CALENDAR, SWITCH, SUBSCRIBE,
        RECYCLER, //        RECYCLER_HORIZONTAL, RECYCLER_GRID, RECYCLER_EXPANDED, RECYCLER_STICKY,
        TOOL, MODIFY_TOOL, AUTO_AUTCH,
        MENU, MENU_B, MENU_BOTTOM, CONTAINER, MAP, SEQUENCE, BUTTON, PHONE, TOTAL, SEARCH, PHOTO, RECOGNIZE_VOICE,
        STATIC_LIST, MODEL, PAGER_V, PAGER_F, INTRO, POP_UP, DATE_DIAPASON, BARCODE, LOAD_DB, CUSTOM,
        ENABLED, YOU_TUBE};
    public ParamComponent () {
        additionalWork = null;
        typeClass = null;
    }
    public String name;
    public TC type;
    public int fragmentsContainerId;
    public String startScreen;
    public int eventComponent;
    public MoreWork moreWork;
    public Class<T> additionalWork, typeClass;
    public BaseComponent baseComponent;
    public ParamModel paramModel;
    public ParamView paramView;
    public ParamMap paramMap;
    public Navigator navigator, navigatorOff;
    public Navigator[] arrNavigator;
    public ActionsAfterResponse after;
    public String intro, auth, main, paramForPathFoto;
    public int[] mustValid;
    public int viewSearchId, titleId;
    public ToolMenu toolMenu;
    public int delayMillis, minLen;
    public boolean startActual = true;
    public boolean hide = false;
    public String st1, st2;     // ля всіляких потреб для кожного типу своя сутність цих рядків
    public String nameReceiver;
    public Multiply[] multiplies;
    public Filters filters;
    public ModifierTool[] modifierTools;
}
