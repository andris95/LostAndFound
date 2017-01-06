package com.sanislo.lostandfound.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.ExceptionCatchingInputStream;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sanislo.lostandfound.AddThingActivity;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.ThingsActivity;
import com.sanislo.lostandfound.utils.FirebaseUtils;

import java.util.concurrent.ExecutionException;

/**
 * Created by root on 05.01.17.
 */

public class LostAndFoundMessagingService extends FirebaseMessagingService {
    private String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: ");
        loadNotificationImage(remoteMessage);
    }

    private void loadNotificationImage(final RemoteMessage remoteMessage) {
        String imagePath = remoteMessage.getData().get("IMAGE_PATH");
        StorageReference imageReference = FirebaseUtils.getStorageRef().child(imagePath);
        try {
            Bitmap image = Glide.with(this).using(new FirebaseImageLoader())
                    .load(imageReference)
                    .asBitmap()
                    .into(-1, -1)
                    .get();
            displayNotification(remoteMessage, image);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void displayNotification(RemoteMessage remoteMessage, Bitmap image) {
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        PendingIntent actionIntent = getPendingIntent(body);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(image)
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
