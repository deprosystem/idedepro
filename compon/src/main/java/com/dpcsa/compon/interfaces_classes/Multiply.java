package com.dpcsa.compon.interfaces_classes;

public class Multiply {

    public int viewId;          // где показывается результат умножения
    public String nameField;    // название поля где находится значение для умножения
    public String nameFieldRes;  // имя поля куда заносится результат. Тип результата определяется типом nameFieldRes

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
