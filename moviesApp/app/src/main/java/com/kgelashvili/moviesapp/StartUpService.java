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

        AlarmManager am=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(this, CheckEpisodesBroadcastReceiver.class);
        //intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent2, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 30), pi);
        return 1;
    }
}
