package com.kgelashvili.moviesapp.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.kgelashvili.moviesapp.broadcastreceivers.CheckEpisodesBroadcastReceiver;

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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        AlarmManager am=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(this, CheckEpisodesBroadcastReceiver.class);
        //intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent2, 0);

        boolean alarmUp = (PendingIntent.getBroadcast(this, 0,
                intent2,
                PendingIntent.FLAG_NO_CREATE) != null);
        //if(!alarmUp)
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60*60*preferences.getInt("interval",6)), pi);

        return 1;
    }
}
