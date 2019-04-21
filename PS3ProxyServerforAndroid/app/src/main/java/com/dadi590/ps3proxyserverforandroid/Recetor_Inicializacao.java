package com.dadi590.ps3proxyserverforandroid;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

public class Recetor_Inicializacao extends BroadcastReceiver {

    private SharedPreferences settings;

    @Override
    public void onReceive(Context context, Intent intent) {

        settings = context.getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        Intent intent_8_0 = new Intent(context,Servico.class);

        if (settings.getString("settings_auto_start", "false").equals("true")) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                intent_8_0.putExtra("extras_inicio_automatico","true");
                context.startForegroundService(intent_8_0);
            } else {
                Intent Servico = new Intent(context, Servico.class);
                Servico.putExtra("extras_inicio_automatico","true");
                context.startService(Servico);
            }
        }
    }
}