package com.sos.msgroup.notification;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sos.msgroup.AdminNotificationActivity;
import com.sos.msgroup.R;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FCMReceiver extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "Notification_channel";
    Random random = new Random();
    // get the VIBRATOR_SERVICE system service
    private Vibrator vibrator = null;

    /*
     * This is automatically called when notification is being received
     * */
    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        createNotificationChannel();
       /* if (remoteMessage.getData().get("for") != null) {
            if (remoteMessage.getData().get("for").equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                showNotification(remoteMessage);
            }
        }*/
        if (remoteMessage.getData().get("for") != null) {
           // if (remoteMessage.getData().get("for").equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                showNotification(remoteMessage);
            //}
        }

    }

    /*
     * Method to show notification when received
     * */
    private void showNotification(RemoteMessage remoteMessage) {

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // this effect creates the vibration of default amplitude for 1000ms(1 sec)
        VibrationEffect vibrationEffect1 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrationEffect1 = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);
        }

        // it is safe to cancel other vibrations currently taking place
        vibrator.cancel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(vibrationEffect1);
        }

        ringtone();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, AdminNotificationActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(remoteMessage.getData().get("body"))
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setColor(ContextCompat.getColor(this, R.color.notification_red));

        builder.setContentIntent(contentIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(random.nextInt() + 1000, builder.build());


    }

    /*
     * Method to create notification channel
     * */
    private void createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    public void ringtone() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
