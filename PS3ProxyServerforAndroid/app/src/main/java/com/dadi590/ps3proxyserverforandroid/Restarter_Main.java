package com.dadi590.ps3proxyserverforandroid;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class Restarter_Main extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent Main = new Intent();
        Main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        Main.setClass(Restarter_Main.this, Main.class);
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        if (intent.hasExtra("extras_start_server")) {
            Main.putExtra("extras_start_server",extras.getString("extras_start_server", "false"));
        }
        if (intent.hasExtra("extras_restart_server")) {
            Main.putExtra("extras_restart_server",extras.getString("extras_restart_server", "false"));
        }
        if (intent.hasExtra("extras_update_port")) {
            Main.putExtra("extras_update_port",extras.getString("extras_update_port", "8080"));
        }
        Main.putExtra("extras_was_on","true");
        startActivity(Main);
        finish();
    }
}
