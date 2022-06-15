package com.example.moengagetask.firebase;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.moengagetask.R;
import com.example.moengagetask.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseIIDService";
    private int notification_count = 0;
    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        Log.e(TAG, "Refreshed token: " + refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("onMessageReceived", "onMessageReceived");

        if (remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        }
    }

    public void sendNotification(String title, String messageBody) {

        Intent intent = null;
        intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.small_icon)
//                        .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap())
                        .setColor(getResources().getColor(R.color.notification_color))
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(messageBody));


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelId,
                    title,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notification_count /* ID of notification */, notificationBuilder.build());
        notification_count++;
    }
}
