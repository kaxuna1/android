package com.kgelashvili.moviesapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by KGelashvili on 7/21/2015.
 */
public class CheckEpisodesBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, CheckNewEpisodes.class);
        context.startService(startServiceIntent);
    }
}
