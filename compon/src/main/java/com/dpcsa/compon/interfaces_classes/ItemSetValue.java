package com.dpcsa.compon.interfaces_classes;

public class ItemSetValue {
    public int viewId;
    public enum TYPE_SOURCE {PARAM, SIZE, LOCALE};
    public TYPE_SOURCE type;
    public String name;
    public int componId;

    public ItemSetValue(int viewId, TYPE_SOURCE source) {
        this.viewId = viewId;
        type = source;
    }

    public ItemSetValue(int viewId, TYPE_SOURCE source, String name) {
        this.viewId = viewId;
        type = source;
        this.name = name;
    }

    public ItemSetValue(int viewId, TYPE_SOURCE source, int componId) {
        this.viewId = viewId;
        type = source;
        this.componId = componId;
    }
}
