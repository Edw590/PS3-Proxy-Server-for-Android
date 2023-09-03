package com.dadi590.ps3proxyserverforandroid;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class Restarter_Service extends AppCompatActivity {

    private Handler handler = new Handler();
    private Bundle extras;
    private Intent intent;
    private NotificationManager notificationManager;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_restarter);

        extras = getIntent().getExtras();
        intent = getIntent();
        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        editor = settings.edit();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);

        if (intent.hasExtra("extras_auto_restart")) {

            editor.putString("settings_ip", Main.getLocalIpAddress());
            editor.apply();

            Intent startMyService = new Intent();
            startMyService.setClass(Restarter_Service.this, Service.class);
            startMyService.putExtra("extras_reload_ip", "true");
            startService(startMyService);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Not to use unless it's better to initiate the Main activity after restarting the service (probably not, for example if the person is playing a game or something like that, just wants not to have any time lost)
                    // Do something after 5s = 5000ms

                    /*Intent Main = new Intent();
                    Main.setClass(Restarter_Service.this, Main.class);
                    Main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(Main);*/

                    finish();
                }
            }, 2500);
        } else {
            Intent stopMyService = new Intent();
            stopMyService.setClass(Restarter_Service.this, Service.class);
            stopService(stopMyService);

            Intent Restarter_Service = new Intent();
            Restarter_Service.setClass(Restarter_Service.this, Restarter_Service.class);
            Restarter_Service.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Restarter_Service.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            Restarter_Service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Restarter_Service.putExtra("extras_auto_restart", "true");
            startActivity(Restarter_Service);

            System.exit(0);
        }
    }
}
