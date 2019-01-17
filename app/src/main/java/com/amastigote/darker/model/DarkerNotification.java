package com.amastigote.darker.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.amastigote.darker.R;
import com.amastigote.darker.activity.MainActivity;

public class DarkerNotification {
    private static NotificationManager notificationManager;
    private static NotificationCompat.Builder builder;
    private final static String IS_ON = "滤镜已激活 (•̀ᴗ•́)و";
    private final static String IS_OFF = "滤镜未激活 (˘•_•˘)";
    private final static String CLICK_TO_ACTIVATE = "轻触可启用";
    private final static String CLICK_TO_DEACTIVATE = "轻触可关闭";
    private final static String OPEN_PANEL = "设置面板";
    public final static String PRESS_BUTTON = "PRESS_BUTTON";

    public DarkerNotification(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.night_128)
                .setAutoCancel(false);
        PendingIntent settingsPendingIntent = PendingIntent.getActivity(
                context,
                0,
                new Intent(context, MainActivity.class),
                0);
        builder.addAction(R.mipmap.ic_settings_white_24dp, OPEN_PANEL, settingsPendingIntent);
        PendingIntent changeStatusPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                new Intent(PRESS_BUTTON),
                0);
        builder.setContentIntent(changeStatusPendingIntent);
    }

    public void updateStatus(boolean isRunning) {
        if (isRunning) {
            builder.setContentTitle(IS_ON)
                    .setContentText(CLICK_TO_DEACTIVATE);
        } else {
            builder.setContentTitle(IS_OFF)
                    .setContentText(CLICK_TO_ACTIVATE);
        }
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(R.string.app_name, notification);
    }

    public void removeNotification() {
        notificationManager.cancel(R.string.app_name);
    }
}
