package com.dadi590.ps3proxyserverforandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Initial_credits extends AppCompatActivity {

    private Button button;
    private Button button1;
    private Button button2;
    private Button button3;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_credits);

        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putString("settings_path_ps3_updatelist","NONE");
        editor.apply();

        button = findViewById(R.id.button_real_time_logging);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Instructions_iniciais = new Intent(Initial_credits.this, Initial_instructions.class);
                Instructions_iniciais.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Instructions_iniciais.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(Instructions_iniciais);
                finish();
            }
        });

        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/mondul/PS3-Proxy");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.psx-place.com/threads/tutorial-how-to-set-up-ps3-proxy-server-on-android.22846/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.psx-place.com/resources/ps3-proxy-server-for-android.795/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
