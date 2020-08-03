package com.dpcsa.compon.custom_components;

public class PointValue {
    public float value;
    public String name;
    public int colorBegin;
    public int colorEnd;
    public int widthLine;
    public boolean isRounding;

    public PointValue(String name, float value) {
        this.value = value;
        this.name = name;
        colorBegin = 0;
        colorEnd = 0;
        isRounding = false;
        widthLine = -1;
    }

    public PointValue(String name, float value, int cBegin, int cEnd) {
        this.value = value;
        this.name = name;
        colorBegin = cBegin;
        colorEnd = cEnd;
        isRounding = false;
        widthLine = -1;
    }

    public PointValue(String name, float value, int cBegin, int cEnd, boolean isRounding) {
        this.value = value;
        this.name = name;
        colorBegin = cBegin;
        colorEnd = cEnd;
        this.isRounding = isRounding;
        widthLine = -1;
    }

    public PointValue(String name, float value, int cBegin, int cEnd, boolean isRounding, int width) {
        this.value = value;
        this.name = name;
        colorBegin = cBegin;
        colorEnd = cEnd;
        this.isRounding = isRounding;
        widthLine = width;
    }
}
