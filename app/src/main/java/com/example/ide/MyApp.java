package com.example.ide;

import android.app.Application;
import android.content.Context;

import com.dpcsa.compon.single.DeclareParam;

public class MyApp extends Application {
    private static MyApp instance;
    private Context context;

    public static MyApp getInstance() {
        if (instance == null) {
            instance = new MyApp();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();

        DeclareParam.build(context)
                .setAppParams(new MyParams())
                .setDeclareScreens(new MyDeclare());
    }
}
