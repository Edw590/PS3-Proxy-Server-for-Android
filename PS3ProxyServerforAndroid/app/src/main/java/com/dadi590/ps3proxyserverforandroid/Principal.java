package com.dadi590.ps3proxyserverforandroid;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import ps3_proxy.Ps3_proxy;

public class Principal extends AppCompatActivity {

    public static final String CHANNEL_ID = "PS3 Proxy Server for Android";

    EditText porta_obj;
    TextView estado_obj;
    TextView dados_ip_obj;
    TextView dados_porta_obj;
    TextView ip_telemovel_obj;
    TextView erro;
    ImageButton botao;
    String ip;
    String porta;
    String auto_save;
    String auto_start;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Bundle extras;
    Intent intent_extras;

    public int ligado=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        editor = settings.edit();
        extras = getIntent().getExtras();
        intent_extras = getIntent();

        if (intent_extras.hasExtra("creditos_iniciais_vistos")) {
            editor.putString("creditos_iniciais_vistos", extras.getString("creditos_iniciais_vistos","false"));
            editor.apply();
        }

        if (settings.getString("creditos_iniciais_vistos", "false").equals("false")) {
            Intent intent = new Intent(Principal.this, Creditos_iniciais.class);
            startActivity(intent);
            finish();
        }

        if (intent_extras.hasExtra("settings_auto_start")) {
            editor.putString("settings_auto_start", settings.getString("creditos_iniciais_vistos", "false"));
            editor.apply();
        }

        if (intent_extras.hasExtra("settings_auto_save")) {
            editor.putString("settings_auto_save", settings.getString("creditos_iniciais_vistos", "false"));
            editor.apply();
        }

        if (settings.getString("settings_auto_save", "false").equals("true")) {
            porta=settings.getString("porta", "8080");
        } else {
            porta="8080";
        }

        try {
            editor.putString("settings_auto_save", extras.getString("auto_save","false"));
            editor.putString("settings_auto_start", extras.getString("auto_start","false"));
            editor.apply();
        } catch (NullPointerException e) {

        }

        auto_save=settings.getString("settings_auto_save", "false");
        auto_start=settings.getString("settings_auto_start", "false");

        createNotificationChannel();

        porta_obj = findViewById(R.id.porta);
        estado_obj = findViewById(R.id.estado);
        dados_ip_obj = findViewById(R.id.dados_ip);
        dados_porta_obj = findViewById(R.id.dados_porta);
        ip_telemovel_obj = findViewById(R.id.ip);
        botao = findViewById(R.id.botao);
        erro = findViewById(R.id.erro);

        ip_telemovel_obj.setKeyListener(null);
        ip_telemovel_obj.setText(Ps3_proxy.externalIP());

        porta_obj.setText(porta);

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.dadi590.ps3proxyserverforandroid.Servico".equals(service.service.getClassName())) {
                //SharedPreferences settings = getSharedPreferences("App_settings", MODE_PRIVATE);
                estado_obj.setText(getString(R.string.estado) + "ON");
                dados_ip_obj.setText("IP: "+settings.getString("ip", "N/A"));
                dados_porta_obj.setText(getString(R.string.porta) + settings.getString("porta", "N/A"));
                botao.setImageResource(R.drawable.pause);
                ligado=1;
            }
        }

        if (ligado==0) {
            estado_obj.setText(getString(R.string.estado) + "OFF");
            dados_ip_obj.setText("IP: N/A");
            dados_porta_obj.setText(getString(R.string.porta) + "N/A");
            botao.setImageResource(R.drawable.play);
        }

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ip = ip_telemovel_obj.getText().toString();
                final String porta = porta_obj.getText().toString();
                if (Integer.parseInt(porta) >= 1024 && Integer.parseInt(porta) <=65535) {
                    erro.setText("");
                    if (ligado == 0) {
                        Intent startMyService = new Intent();
                        startMyService.setClass(Principal.this, Servico.class);
                        /*startMyService.putExtra("ip",ip);
                        startMyService.putExtra("porta",porta);*/
                        startService(startMyService);

                        estado_obj.setText(getString(R.string.estado) + "ON");
                        dados_ip_obj.setText("IP: " + ip);
                        dados_porta_obj.setText(getString(R.string.porta) + porta);
                        botao.setImageResource(R.drawable.pause);
                        ligado = 1;
                        editor.putString("ip", ip);
                        editor.putString("porta", porta);
                        editor.apply();
                    } else if (ligado == 1) {
                        estado_obj.setText(getString(R.string.estado) + "OFF");
                        dados_ip_obj.setText("IP: N/A");
                        dados_porta_obj.setText(getString(R.string.porta) + "N/A");
                        botao.setImageResource(R.drawable.play);
                        Intent stopMyService = new Intent();
                        stopMyService.setClass(Principal.this, Servico.class);
                        stopService(stopMyService);
                        Intent Reinicializador_Principal = new Intent();
                        Reinicializador_Principal.setClass(Principal.this, Reinicializador_Principal.class);
                        startActivity(Reinicializador_Principal);
                        System.exit(0);
                    }
                } else {
                    erro.setText(getString(R.string.erro));
                }
            }
        });
    }

    private void createNotificationChannel() {

        //Copiado de developer.android.com
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.nome_servico);
            String description = getString(R.string.nome_servico);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.definicoes) {
            Intent Definicoes = new Intent();
            Definicoes.setClass(Principal.this, Definicoes.class);
            Definicoes.putExtra("auto_save",auto_save);
            Definicoes.putExtra("auto_start",auto_start);
            startActivity(Definicoes);
            finish();
        } else if (item.getItemId()==R.id.creditos) {
            startActivity (new Intent (Principal.this, Creditos.class));
        }
        return super.onOptionsItemSelected(item);
    }
}