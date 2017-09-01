package com.example.sarthak.notes.utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.sarthak.notes.R;
import com.example.sarthak.notes.activities.NotesActivity;

public class AlarmService extends IntentService {

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        sendNotification("Alarm");
    }

    private void sendNotification(String msg) {
        NotificationManager alarmNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.notification_icon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
    }
}
