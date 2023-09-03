package com.dadi590.ps3proxyserverforandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

public class Initialization_receptor extends BroadcastReceiver {

    private SharedPreferences settings;

    @Override
    public void onReceive(Context context, Intent intent) {

        settings = context.getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        Intent Service = new Intent(context, Service.class);
        Service.putExtra("extras_auto_start_boot","true");

        if (settings.getString("settings_auto_start_boot", "false").equals("true")) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(Service);
            } else {
                context.startService(Service);
            }
        }
    }
}