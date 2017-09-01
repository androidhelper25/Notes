package com.example.sarthak.notes.utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.sarthak.notes.R;

/**
 * Service to set up notification at specified time even when app is in background.
 */

public class AlarmService extends IntentService {

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {

        String notificationData = intent.getStringExtra(Constants.INTENT_PASS_NOTIFICATION_MESSAGE);
        //  set notification message
        if (!notificationData.equals("")) {
            sendNotification(notificationData);
        } else {
            sendNotification("Alarm");
        }
    }

    /**
     * Set up notification with message in the Note.
     *
     * @param msg is the message to be displayed in the notification
     */
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
