package com.dpcsa.compon.interfaces_classes;

public class Channel<T> {
    public String id;
    public String name;
    public String description;
    public Class<T> screen;
    public Notice[] notices;
    public boolean enableLights = false, enableVibration = false, sBadge = false;
    public int lightColor, iconColor, drawableId, importance, imgLarge;
    public long[] vibrationPattern;

    public Channel(String id, String name, int importance, Class<T> screen, Notice[] notices) {
        this.id = id;
        this.importance = importance;
        if (this.importance > 4 || this.importance < 0) {
            this.importance = 0;
        }
        this.name = name;
        this.screen = screen;
        this.notices = notices;
    }

    public Channel description(String description) {
        this.description = description;
        return this;
    }

    public Channel enableLights(boolean lights) {
        enableLights = lights;
        return this;
    }

    public Channel enableVibration(boolean vibration) {
        enableVibration = vibration;
        return this;
    }

    public Channel vibrationPattern(long[] vibration) {
        enableVibration = vibration != null && vibration.length > 0;
        vibrationPattern = vibration;
        return this;
    }

    public Channel lightColor(int color) {
        lightColor = color;
        return this;
    }

    public Channel iconColor(int color) {
        iconColor = color;
        return this;
    }

    public Channel showBadge(boolean sBadge) {
        this.sBadge = sBadge;
        return this;
    }

    public Channel icon(int drawableId) {
        this.drawableId = drawableId;
        return this;
    }

    public Channel iconLarge(int drawableId) {
        this.imgLarge = drawableId;
        return this;
    }

}
