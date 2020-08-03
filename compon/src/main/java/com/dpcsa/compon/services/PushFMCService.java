package com.dpcsa.compon.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.dpcsa.compon.interfaces_classes.Channel;
import com.dpcsa.compon.interfaces_classes.Notice;
import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.single.Injector;
import com.dpcsa.compon.tools.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import androidx.core.app.NotificationCompat;

public class PushFMCService extends FirebaseMessagingService {

    public ComponGlob componGlob;
    public ComponPrefTool preferences;
    private String type, dataPush;

    @Override
    public void onNewToken(String s) {
        componGlob = Injector.getComponGlob();
        preferences = Injector.getPreferences();
        String st = componGlob.appParams.nameTokenPush;
        if (st != null && st.length() > 0) {
            componGlob.setParamValue(st, s);
        }
        preferences.setPushToken(s);
        componGlob.pushToken.setValue(new String(st), 0, componGlob.context);
        Log.d("QWERT", "New token="+s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        componGlob = Injector.getComponGlob();
        preferences = Injector.getPreferences();
        preferences.setPushData("");
        Map<String, String> data = remoteMessage.getData();
        type = data.get(Constants.PUSH_TYPE);
Log.d("QWERT","onMessageReceived type="+type);
        if (type != null && type.length() != 0) {
            dataPush = data.get(Constants.PUSH_DATA);
            for (Notice not : componGlob.notices) {
                if (not.type.equals(type)) {
                    formNotif(not, remoteMessage);
                    break;
                }
            }
        } else {
            Log.i(componGlob.appParams.NAME_LOG_APP, "0011 Нет типа пуша");
        }
    }

    private void formNotif(Notice not, RemoteMessage remoteMessage) {
//    private void formNotif(Notice not, Map<String, String> data) {
        String contentText, title;
        Map<String, String> data = remoteMessage.getData();
        RemoteMessage.Notification notif = remoteMessage.getNotification();
        if (notif != null) {
            contentText = notif.getBody();
            title = notif.getTitle();
        } else {
            contentText = data.get("message");
            title = data.get("title");
        }
        if (not.countLotPush) {
            not.addCount();
        }
        if (not.textLotPush != null && not.count() > 1) {
            contentText = not.textLotPush + " " + not.count();
        }

        Intent notificationIntent = new Intent(this, not.screen);
        notificationIntent.putExtra(Constants.SMPL_PUSH_TYPE, type);
        notificationIntent.putExtra(Constants.SMPL_PUSH_DATA, dataPush);
        notificationIntent.setAction(type);

        formCustomNotification(notificationIntent, not, remoteMessage);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Channel chan = componGlob.channels.get(not.idChannelInt);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, not.idChannel);
        notificationBuilder.setAutoCancel(true)
//                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentTitle(title)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis());
        if (not.img != 0) {
            notificationBuilder.setSmallIcon(not.img);
            if (not.color != 0) {
                notificationBuilder.setColor(not.color);
            }
        } else {
            if (chan.drawableId != 0) {
                notificationBuilder.setSmallIcon(chan.drawableId);
            }
            if (chan.iconColor != 0) {
                notificationBuilder.setColor(chan.iconColor);
            }
        }
        if (not.imgLarge != 0) {
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), not.imgLarge));
        } else if (chan.imgLarge != 0) {
                notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), chan.imgLarge));
            }

        mNotificationManager.notify(not.idNotice, notificationBuilder.build());
    }

    public void formCustomNotification(Intent notificationIntent, Notice not, RemoteMessage remoteMessage) {

    }
}
