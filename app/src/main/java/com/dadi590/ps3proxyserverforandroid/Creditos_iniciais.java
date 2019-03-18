package com.dadi590.ps3proxyserverforandroid;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Creditos_iniciais extends AppCompatActivity {

    Button botao;
    Button botao1;
    Button botao2;
    Button botao3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditos_iniciais);

        botao = findViewById(R.id.botao);
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Principal = new Intent(Creditos_iniciais.this, Principal.class);
                Principal.putExtra("creditos_iniciais_vistos","true");
                startActivity(Principal);
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