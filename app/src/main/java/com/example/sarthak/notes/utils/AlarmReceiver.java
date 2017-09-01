package com.example.sarthak.notes.utils;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Broadcast receiver called by AlarmManager at time specified in the Note.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String notificationData = intent.getStringExtra(Constants.INTENT_PASS_NOTIFICATION_MESSAGE);

        // set ringtone
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
        ringtone.play();

        // launch AlarmService to set notification
        Intent notificationService = new Intent(context, AlarmService.class);
        notificationService.putExtra(Constants.INTENT_PASS_NOTIFICATION_MESSAGE, notificationData);
        startWakefulService(context, notificationService);
    }
}
