package com.dadi590.ps3proxyserverforandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Reinicializador_Principal extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent Principal = new Intent();
        Principal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Principal.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        Principal.setClass(Reinicializador_Principal.this, Principal.class);
        Intent intent_extras = getIntent();
        if (intent_extras.hasExtra("extras_iniciar_servidor")) {
            Bundle extras = getIntent().getExtras();
            Principal.putExtra("extras_iniciar_servidor",extras.getString("extras_iniciar_servidor", "false"));
        }
        if (intent_extras.hasExtra("extras_reiniciar_servidor")) {
            Bundle extras = getIntent().getExtras();
            Principal.putExtra("extras_reiniciar_servidor",extras.getString("extras_reiniciar_servidor", "false"));
        }
        Principal.putExtra("extras_estava_ligada","true");
        startActivity(Principal);
        finish();
    }
}