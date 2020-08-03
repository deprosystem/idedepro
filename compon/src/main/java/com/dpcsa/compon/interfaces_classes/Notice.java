package com.dpcsa.compon.interfaces_classes;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.FieldBroadcast;
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.single.Injector;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Notice<T> {
    public int idChannelInt, idNotice;
    public String idChannel;
    public String type;
    public Class<T> screen;
    public String textLotPush;
    public boolean countLotPush;
    public FieldBroadcast fieldBroadcast;
    public static String PREFIX_PUSH = "SMPL_PUSH_";
    public String simpleType;
    private Context context;
    private ComponPrefTool preferences;
    public int img, color, imgLarge;

    public Notice(String type, Context context) {
        this.type = type;
        this.context = context;
    }

    public Notice lotPushs(String textLotPush, boolean countLotPush) {
        this.textLotPush = textLotPush;
        this.countLotPush = countLotPush;
        simpleType = PREFIX_PUSH + type;
        preferences = Injector.getPreferences();
        if (countLotPush) {
            int cc = preferences.getNameInt(simpleType, 0);
            fieldBroadcast = new FieldBroadcast(simpleType, Field.TYPE_INTEGER, cc);
        }
        return this;
    }

    public Notice icon(int img, int color) {
        this.img = img;
        this.color = color;
        return this;
    }

    public Notice iconLarge(int img) {
        this.imgLarge = img;
        return this;
    }

    public void addCount() {
        if (countLotPush) {
            int cc = ((Integer) fieldBroadcast.value) + 1;
            preferences.setNameInt(simpleType, cc);
            fieldBroadcast.setValue(cc, 0, context);
        }
    }

    public int count() {
        if (countLotPush) {
            return (Integer) fieldBroadcast.value;
        } else {
            return 0;
        }
    }

    public void nullifyValue() {
        if (countLotPush) {
            if (((int) fieldBroadcast.value) != 0) {
                int cc = 0;
                preferences.setNameInt(simpleType, cc);
                fieldBroadcast.setValue(cc, 0, context);
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(idNotice);
            }
        }
    }

}
