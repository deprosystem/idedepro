package com.dpcsa.compon.interfaces_classes;

public class AdditionalWork <T> {
    private final Class<T> clazz;
    public AdditionalWork(Class<T> clazz) {
        this.clazz = clazz;
        try {
            Object o = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
