package com.dpcsa.compon.interfaces_classes;

public class RequestPermissionsResult {
    public int request;
    public PermissionsResult permissionsResult;

    public RequestPermissionsResult(int request, PermissionsResult permissionsResult) {
        this.request = request;
        this.permissionsResult = permissionsResult;
    }
}
