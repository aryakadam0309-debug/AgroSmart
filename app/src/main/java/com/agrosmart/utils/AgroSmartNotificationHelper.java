package com.agrosmart.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.agrosmart.R;
import com.agrosmart.activities.farmer.WeatherActivity;

/**
 * System Notification Helper for Rain Alerts & Weather Advisory Updates (PS 5.4 Requirement 7).
 * Creates notification channels, builds high-priority heads-up alerts with sound/vibration,
 * and attaches deep-link PendingIntents to WeatherActivity.
 */
public class AgroSmartNotificationHelper {

    public static final String CHANNEL_ID_RAIN_ALERTS = "agrosmart_rain_alerts";
    public static final String CHANNEL_NAME_RAIN_ALERTS = "AgroSmart Rain & Weather Alerts";
    public static final String EXTRA_OPEN_ADVISORY_ALERT = "extra_open_advisory_alert";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID_RAIN_ALERTS,
                    CHANNEL_NAME_RAIN_ALERTS,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Severe weather, heavy rain warnings, and urgent farming advisory updates.");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void showRainAlertNotification(Context context, String title, String message, String activityAffected) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra(EXTRA_OPEN_ADVISORY_ALERT, true);
        intent.putExtra("affected_activity", activityAffected);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_RAIN_ALERTS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(0xFF2E7D32) // Forest green
                .setContentIntent(pendingIntent);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify((int) (System.currentTimeMillis() % 100000), builder.build());
        } catch (SecurityException ignored) {
            // Android 13+ permission catch
        }
    }
}
