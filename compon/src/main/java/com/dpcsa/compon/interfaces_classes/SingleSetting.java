package com.dpcsa.compon.interfaces_classes;

import android.content.Context;

import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.single.Injector;

public class SingleSetting {
    ComponGlob componGlob;
    public ComponPrefTool preferences;
    ViewHandler vh;
    IBase iBase;
    Context context;

    public SingleSetting(IBase iBase, ViewHandler vh) {
        this.vh = vh;
        this.iBase = iBase;
        componGlob = Injector.getComponGlob();
        preferences = Injector.getPreferences();
        context = componGlob.context;
    }

    public void set() {

    }

    public void close() {

    }
}
