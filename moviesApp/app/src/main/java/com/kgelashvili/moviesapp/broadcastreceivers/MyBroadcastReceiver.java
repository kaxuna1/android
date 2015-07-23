package com.kgelashvili.moviesapp.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kgelashvili.moviesapp.services.StartUpService;

/**
 * Created by KGelashvili on 7/21/2015.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent startServiceIntent = new Intent(context, StartUpService.class);
        context.startService(startServiceIntent);

    }
}