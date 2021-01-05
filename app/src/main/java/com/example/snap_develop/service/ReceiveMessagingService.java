package com.example.snap_develop.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.snap_develop.R;
import com.example.snap_develop.view.ui.ApplicatedFollowListActivity;
import com.example.snap_develop.view.ui.DisplayCommentActivity;
import com.example.snap_develop.view.ui.MapActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ReceiveMessagingService extends FirebaseMessagingService {

    private Intent mIntent;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        mIntent = new Intent(this, MapActivity.class);

        String messageBody = "";
        if (remoteMessage.getNotification() != null) {
            messageBody = remoteMessage.getNotification().getBody();

            if (TextUtils.equals(remoteMessage.getNotification().getTitle(), "good")) {
                mIntent = new Intent(this, DisplayCommentActivity.class)
                        .putExtra("parent_post", remoteMessage.getNotification().getTag());
            } else if (TextUtils.equals(remoteMessage.getNotification().getTitle(), "applicated_follow")) {
                mIntent = new Intent(this, ApplicatedFollowListActivity.class);
            }
        }

        sendNotification(messageBody);
    }

    /**
     * Create and show a simple notification containing the received FCM message.     *
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, mIntent,
                PendingIntent.FLAG_ONE_SHOT);

        //String channelId = getString(R.string.default_notification_channel_id);
        String channelId = "001";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        //.setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("SNap")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
