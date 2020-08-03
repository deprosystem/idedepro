package com.dpcsa.compon.interfaces_classes;

public class RequestActivityResult {
    public int request;
    public ActionsAfterResponse afterResponse;
    public ActivityResult activityResult;

    public RequestActivityResult(int request, ActionsAfterResponse afterResponse) {
        this.request = request;
        this.afterResponse = afterResponse;
        activityResult = null;
    }

    public RequestActivityResult(int request, ActionsAfterResponse afterResponse, ActivityResult activityResult) {
        this.request = request;
        this.afterResponse = afterResponse;
        this.activityResult = activityResult;
    }

    public RequestActivityResult(int request, ActivityResult activityResult) {
        this.request = request;
        this.activityResult = activityResult;
        afterResponse = null;
    }
}
