package com.dpcsa.compon.interfaces_classes;

public class Multiply {

    public int viewId;
    public String nameField;
    public String nameFieldRes;

    public Multiply(int viewId, String nameField) {
        this.nameField = nameField;
        this.viewId = viewId;
    }

    public Multiply(int viewId, String nameField, String nameFieldRes) {
        this.nameField = nameField;
        this.viewId = viewId;
        this.nameFieldRes = nameFieldRes;
    }
}
