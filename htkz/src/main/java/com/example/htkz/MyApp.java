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

//                .addParam("hot_depart_city_id", "2")
//                .addParam("hot_depart_city_name", "Алматы")
                .addParam("country_name", "")
                .addParam("adults", "1")
                .addParam("kids","")
                .addParam("country_id","")
                .addParam("city_id","")
//                .addParam("hot_flag_country", "https://deprosystem.com/images/htkz/flag_kaz.png")
                .addParam("depart_city_id", "2")
                .addParam("depart_city_name", "Алматы")
                .addParam("flag_country", "https://deprosystem.com/images/htkz/flag_kaz.png")
                .addParam("access-token","885bfa8ed2c62b023dfae54ecb8c598ea");
//                .addParam("access-token","885bfa8ed2c62b023dfae54ecb8c598ea85515b1cf95ba957f1c2f20675467e4");
    }
}
