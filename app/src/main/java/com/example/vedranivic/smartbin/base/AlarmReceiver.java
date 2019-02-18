package com.example.vedranivic.smartbin.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import com.example.vedranivic.smartbin.MainActivity;
import com.example.vedranivic.smartbin.R;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver{

    public final static int reqCode = 101;
    public final static String CHANNEL_REMINDER = "REMINDER";
    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    public void showNotification(Context context) {
        PendingIntent notificationIntent = PendingIntent.getActivity(
                context,
                reqCode,
                new Intent(context, MainActivity.class)
                , PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_REMINDER)
                .setSmallIcon(R.drawable.ic_mybin_24dp)
                .setContentTitle("Reminder")
                .setContentText("You should take your bin out for waste collection!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(notificationIntent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setCategory(NotificationCompat.CATEGORY_ALARM);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(reqCode, mBuilder.build());
    }
}
