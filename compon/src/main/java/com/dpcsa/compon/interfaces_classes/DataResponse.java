package com.dpcsa.compon.interfaces_classes;

public class DataResponse <T> {
    public long time;
    public long delta;
    public Object response;
    public DataResponse(long delta) {
        time = 0;
        response = null;
        this.delta = delta;
    }
}
