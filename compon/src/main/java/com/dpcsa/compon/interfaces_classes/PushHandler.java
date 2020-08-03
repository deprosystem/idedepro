package com.dpcsa.compon.interfaces_classes;

public class PushHandler {
    public int viewId, handlerId;
    public enum TYPE {DRAWER, SELECT_MENU, SELECT_PAGER, SELECT_RECYCLER, NULLIFY};
    public TYPE type;
    public String[] pushName;
    public String screen, pushType;
    public boolean continuePush;

    public PushHandler(int viewId, TYPE type, String[] pushName) {
        this.viewId = viewId;
        this.type = type;
        this.pushName = pushName;
        continuePush = false;
    }

    public PushHandler(int viewId, TYPE type, String pushType, String screen) {
        this.viewId = viewId;
        this.type = type;
        this.pushType = pushType;
        this.screen = screen;
        continuePush = false;
    }

    public PushHandler(int viewId, TYPE type, String pushType, String screen, boolean continuePush) {
        this.viewId = viewId;
        this.type = type;
        this.pushType = pushType;
        this.screen = screen;
        this.continuePush = continuePush;
    }

// handlerId - id элемента в навигаторе который нужно выполнить
    public PushHandler(int viewId, TYPE type, String pushType, String screen, int handlerId, boolean continuePush) {
        this.viewId = viewId;
        this.type = type;
        this.pushType = pushType;
        this.screen = screen;
        this.handlerId = handlerId;
        this.continuePush = continuePush;
    }
}
