package com.dpcsa.compon.single;

import android.content.Context;

public class ComponTools {

    private Context context;
    private ComponPrefTool preferences;
    private ComponGlob componGlob;

    public ComponTools(Context context, ComponPrefTool preferences, ComponGlob componGlob) {
        this.context = context;
        this.preferences = preferences;
        this.componGlob = componGlob;
    }

    public String getLocale() {
        return preferences.getLocale();
    }

    public String getParamValue(String name) {
        return componGlob.getParamValue(name);
    }

    public void setParamValue(String name, String value) {
        componGlob.setParamValue(name, value);
    }

}
