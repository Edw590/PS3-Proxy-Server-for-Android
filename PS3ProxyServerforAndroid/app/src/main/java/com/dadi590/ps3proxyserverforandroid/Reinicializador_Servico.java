package com.dadi590.ps3proxyserverforandroid;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

public class Reinicializador_Servico extends Service {

    private SharedPreferences settings;

    final Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("++++++++++++++++++++++++++++++++++++");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms

                settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
                Intent intent_8_0 = new Intent(Reinicializador_Servico.this,Servico.class);

                if (settings.getString("settings_auto_start", "false").equals("true")) {
                    Intent Servico = new Intent(Reinicializador_Servico.this, Servico.class);
                    Servico.putExtra("extras_inicio_automatico","true");
                    startService(Servico);
                }
            }
        }, 1000);

        //do heavy work on a background thread
        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
