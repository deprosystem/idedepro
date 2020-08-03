package com.dpcsa.compon.param;

public class ParamMap {

    public float LEVEL_ZOOM = 16.0f;
    public double lonBegin, latBegin;
    public String lonBeginParam, latBeginParam;
    public String titleMarkerBegin = "";
    public float colorMarkerBegin = 180.0f;
    public boolean locationService;
    public int myMarker, otherMarker;
    public int[] markerIdArray;
    public String nameField;
    public int clickInfoWindowId;
    public boolean onActivity;
    public boolean uiZoom, uiTarget;

    public ParamMap(boolean locationService) {
        this.locationService = locationService;
    }

    public ParamMap levelZoom(float levelZoom) {
        LEVEL_ZOOM = levelZoom;
        return this;
    }

    public ParamMap coordinateValue(double latBegin, double lonBegin) {
        this.lonBegin = lonBegin;
        this.latBegin = latBegin;
        return this;
    }

    public ParamMap coordinateValue(double latBegin, double lonBegin, String title, float color) {
        titleMarkerBegin = title;
        colorMarkerBegin = color;
        this.lonBegin = lonBegin;
        this.latBegin = latBegin;
        return this;
    }

    public ParamMap coordinateNameParam(String latBegin, String lonBegin) {
        lonBeginParam = lonBegin;
        latBeginParam = latBegin;
        return this;
    }

    public ParamMap markerImg(int myMarker, int otherMarker) {
        this.myMarker = myMarker;
        this.otherMarker = otherMarker;
        return this;
    }

    public ParamMap markerImg(int myMarker, String nameField, int... args) {
        this.myMarker = myMarker;
        this.nameField = nameField;
        this.markerIdArray = args;
        return this;
    }

    public ParamMap mapControls(boolean target, boolean zoom) {
        uiTarget = target;
        uiZoom = zoom;
        return this;
    }

//    public ParamMap markerClick(int clickInfoWindowId) {
//        this.clickInfoWindowId = clickInfoWindowId;
//        return this;
//    }

    public ParamMap markerClick(int clickInfoWindowId, boolean onActivity) {
        this.clickInfoWindowId = clickInfoWindowId;
        this.onActivity = onActivity;
        return this;
    }
}
