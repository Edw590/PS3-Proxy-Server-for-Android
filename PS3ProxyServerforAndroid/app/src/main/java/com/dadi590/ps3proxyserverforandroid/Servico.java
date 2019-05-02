package com.dadi590.ps3proxyserverforandroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import ps3_proxy.Ps3_proxy;

public class Servico extends Service {

    private String ip;
    private String porta="8080";
    private String escrever_ficheiro;
    private String caminho_ficheiro;
    private Bundle extras;
    private SharedPreferences settings;

    final Handler handler = new Handler();

    Thread Servidor = new Thread(new Runnable() {
        @Override
        public void run() {
            Ps3_proxy.server(ip+":"+porta+"|"+escrever_ficheiro+"|"+caminho_ficheiro);
        }
    });

    Thread Atualizador_IP = new Thread(new Runnable() {
        @Override
        public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        if (Ps3_proxy.externalIP().equals(ip)) {
                            Intent stopMyService = new Intent();
                            Servidor.interrupt();
                            Intent startMyService = new Intent();
                            startMyService.setClass(Servico.this, Reinicializador_Servico.class);
                            startService(startMyService);
                            stopMyService.setClass(Servico.this, Servico.class);
                            stopService(stopMyService);
                            System.out.println("-----------------------");
                        }
                    }
                }, 5000);
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

        if (intent_extras.hasExtra("extras_inicio_automatico")) {
            if (settings.getString("settings_auto_start", "false").equals("false")) {
                System.exit(0);
            }
        }
        // Ativar isto quando for para ativar o IP manual
        /*    ip = Ps3_proxy.externalIP();
        } else {
            ip = settings.getString("settings_ip", Ps3_proxy.externalIP());
        }*/
        ip = Ps3_proxy.externalIP();

        porta=settings.getString("settings_porta", "8080");
        escrever_ficheiro=settings.getString("settings_escrever_ficheiro", "false");
        caminho_ficheiro=settings.getString("settings_caminho_ficheiro", "NONE");

        /*ip = extras.getString("ip");
        porta = extras.getString("porta");*/

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

        Servidor.start();
        //Atualizador_IP.start();

        //do heavy work on a background thread
        //stopSelf();

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