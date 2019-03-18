package com.dadi590.ps3proxyserverforandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Reinicializador_Principal extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent Principal = new Intent();
        Principal.setClass(Reinicializador_Principal.this, Principal.class);
        startActivity(Principal);
        finish();
    }
}