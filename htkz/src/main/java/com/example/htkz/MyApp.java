package com.example.htkz;

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
                .setDeclareScreens(new MyDeclare())
                .addParam("cityId_hot", "2")
                .addParam("cityName_hot", "Алматы")
                .addParam("flag_country_hot", "https://ht.kz/fls/page/1352713745eccd5ee1.gif")
                .addParam("access-token","885bfa8ed2c62b023dfae54ecb8c598ea");
//                .addParam("access-token","885bfa8ed2c62b023dfae54ecb8c598ea85515b1cf95ba957f1c2f20675467e4");
    }
}
