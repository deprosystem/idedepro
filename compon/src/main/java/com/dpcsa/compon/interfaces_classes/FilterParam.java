package com.dpcsa.compon.interfaces_classes;

public class FilterParam {
    public String nameField;
    public enum Operation {equally, more, less, maxSize};
    public Operation oper;
    public Object value;
    public int maxSize;

    public FilterParam(String nameField, Operation oper, Object value) {
        this.nameField = nameField;
        this.oper = oper;
        this.value = value;
    }

    public FilterParam(Operation oper, int max) {
        this.oper = oper;
        maxSize = max;
    }
}
