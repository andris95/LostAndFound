package com.sanislo.lostandfound.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sanislo.lostandfound.AddThingActivity;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.ThingsActivity;

/**
 * Created by root on 05.01.17.
 */

public class LostAndFoundMessagingService extends FirebaseMessagingService {
    private String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: ");
        displayNotification(remoteMessage);
    }

    private void displayNotification(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        PendingIntent actionIntent = getPendingIntent(body);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(actionIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0, notificationBuilder.build());
    }

    private PendingIntent getPendingIntent(String notificationText) {
        Intent intent = new Intent(this, AddThingActivity.class);
        intent.putExtra("NOTIFICATION_TEXT", notificationText);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        return pendingIntent;
    }
}
