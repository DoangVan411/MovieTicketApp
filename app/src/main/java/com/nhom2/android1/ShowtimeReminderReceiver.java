package com.nhom2.android1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShowtimeReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
                String movieTitle = intent.getStringExtra(AppConstants.EXTRA_MOVIE_TITLE);
                String theaterName = intent.getStringExtra(AppConstants.EXTRA_THEATER_NAME);
                long showtimeMillis = intent.getLongExtra(AppConstants.EXTRA_SHOWTIME_MILLIS, 0L);

        String formattedTime = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                .format(new Date(showtimeMillis));
        String content = context.getString(
                R.string.notification_reminder_content,
                movieTitle,
                theaterName,
                formattedTime
        );

        Intent openAppIntent = new Intent(context, LoginActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                context,
                (movieTitle + showtimeMillis).hashCode(),
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AppConstants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.notification_reminder_title))
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(openAppPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify((movieTitle + showtimeMillis).hashCode(), builder.build());
        }
    }
}
