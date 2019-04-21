package com.dadi590.ps3proxyserverforandroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import ps3_proxy.Ps3_proxy;

public class Servico extends Service {

    String ip;
    String porta;
    Bundle extras;
    SharedPreferences settings;

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            Ps3_proxy.server(ip+":"+porta);
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        extras = intent.getExtras();
        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        Intent intent_extras = new Intent(this, Service.class);

        porta=settings.getString("porta", "8080");
        ip=settings.getString("ip", Ps3_proxy.externalIP());

        /*ip = extras.getString("ip");
        porta = extras.getString("porta");*/

        if (intent_extras.hasExtra("inicio_automatico")) {
            if (settings.getString("settings_auto_start", "false").equals("false")) {
                System.exit(0);
            }
        }

        final Intent notificationIntent = new Intent(this, Principal.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Notification notification = new NotificationCompat.Builder(this, Principal.CHANNEL_ID)
                    .setContentTitle("PS3 Proxy Server " + getString(R.string.servidor_ativado))
                    .setContentText("IP: " + ip + " | " + getString(R.string.porta) + porta)
                    .setSmallIcon(R.drawable.icone_principal_antigo_sem_bordas)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);
        } else {
            Notification notification = new NotificationCompat.Builder(this, Principal.CHANNEL_ID)
                    .setContentTitle("PS3 Proxy Server " + getString(R.string.servidor_ativado))
                    .setContentText("IP: " + ip + " | " + getString(R.string.porta) + porta)
                    .setSmallIcon(R.drawable.icone_principal_novo)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);
        }

        thread.start();

        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}