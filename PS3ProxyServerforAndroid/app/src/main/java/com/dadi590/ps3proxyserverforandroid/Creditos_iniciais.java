package com.dadi590.ps3proxyserverforandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Creditos_iniciais extends AppCompatActivity {

    private Button botao;
    private Button botao1;
    private Button botao2;
    private Button botao3;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditos_iniciais);

        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putString("settings_caminho_ficheiro","NONE");
        editor.apply();

        botao = findViewById(R.id.botao);
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Instrucoes_iniciais = new Intent(Creditos_iniciais.this, Instrucoes_iniciais.class);
                Instrucoes_iniciais.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Instrucoes_iniciais.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(Instrucoes_iniciais);
                finish();
            }
        });

        botao1 = findViewById(R.id.botao1);
        botao1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/mondul/PS3-Proxy");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        botao2 = findViewById(R.id.botao2);
        botao2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.psx-place.com/threads/tutorial-how-to-set-up-ps3-proxy-server-on-android.22846/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        botao3 = findViewById(R.id.botao3);
        botao3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.psx-place.com/resources/ps3-proxy-server-for-android.795/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}