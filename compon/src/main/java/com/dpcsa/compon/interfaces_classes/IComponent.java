package com.dpcsa.compon.interfaces_classes;

public interface IComponent {
    void setData(Object data);
    Object getData();
    void setOnChangeStatusListener(OnChangeStatusListener statusListener);
    String getString();
}
