package com.dadi590.ps3proxyserverforandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Inicializador_Servico extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startMyService = new Intent();
        startMyService.setClass(Inicializador_Servico.this, Servico.class);
        startService(startMyService);
        finish();
    }
}
