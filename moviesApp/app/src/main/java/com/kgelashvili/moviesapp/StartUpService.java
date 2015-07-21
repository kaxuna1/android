package com.kgelashvili.moviesapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

public class StartUpService extends Service {
    public StartUpService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()

        AlarmManager alarm=(AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent myIntent = new Intent(this, CheckNewEpisodes.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StartUpService.this, 0, myIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarm.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),15000,pendingIntent );

        return 1;
    }
}
