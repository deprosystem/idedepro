package com.dpcsa.compon.interfaces_classes;

public class Visibility {
    public int typeShow; // 0 - visibility, 1 - enabled
    public int viewId;
    public String nameField;

    public Visibility(int typeShow, int viewId, String nameField) {
        this.typeShow = typeShow;
        this.viewId = viewId;
        this.nameField = nameField;
    }
}
