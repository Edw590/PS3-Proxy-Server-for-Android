package com.dadi590.ps3proxyserverforandroid;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import ps3_proxy.Ps3_proxy;

public class Principal extends AppCompatActivity {

    public static final String CHANNEL_ID = "PS3 Proxy Server for Android";

    private EditText porta_obj;
    private TextView estado_obj;
    private TextView dados_ip_obj;
    private TextView dados_porta_obj;
    private TextView ip_telemovel_obj;
    private TextView erro;
    private TextView ficheiro_auto;
    private ImageButton botao_start_stop;
    private Button botao_atualizar_ip;
    private String ip;
    private String porta;
    private String caminho_ficheiro;
    private String auto_save;
    private String auto_start;
    private String escrever_ficheiro;
    private String desativar_auto_atualizador;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Bundle extras;
    private Intent intent_extras;
    private String ligar_servidor="false";
    private String ignorar_atualizacao;
    private String versao[];

    public int ligado=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Toolbar myToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        editor = settings.edit();
        extras = getIntent().getExtras();
        intent_extras = getIntent();

        if (intent_extras.hasExtra("extras_iniciar_servidor")) {
            if (extras.getString("extras_iniciar_servidor", "false").equals("true")) {
                ligar_servidor="true";
            }
        }

        if (intent_extras.hasExtra("extras_coisas_iniciais_vistas")) {
            editor.putString("settings_creditos_iniciais_vistos", extras.getString("extras_coisas_iniciais_vistas","false"));
            editor.apply();
        }

        if (settings.getString("settings_creditos_iniciais_vistos", "false").equals("false")) {
            Intent Creditos_iniciais = new Intent(Principal.this, Creditos_iniciais.class);
            Creditos_iniciais.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Creditos_iniciais.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(Creditos_iniciais);
            finish();
        }

        if (intent_extras.hasExtra("extras_ignorar_atualizacao")) {
            editor.putString("settings_ignorar_atualizacao", extras.getString("extras_ignorar_atualizacao", "false"));
            editor.apply();
        }

        if (intent_extras.hasExtra("extras_auto_start")) {
            editor.putString("settings_auto_start", extras.getString("extras_auto_start", "false"));
            editor.apply();
        }

        if (intent_extras.hasExtra("extras_auto_save")) {
            editor.putString("settings_auto_save", extras.getString("extras_auto_save", "false"));
            editor.apply();
        }

        if (intent_extras.hasExtra("extras_escrever_ficheiro")) {
            editor.putString("settings_escrever_ficheiro", extras.getString("extras_escrever_ficheiro", "false"));
            editor.apply();
        }

        if (intent_extras.hasExtra("extras_desativar_auto_atualizador")) {
            editor.putString("settings_desativar_auto_atualizador", extras.getString("extras_desativar_auto_atualizador", "settings_desativar_auto_atualizador"));
            editor.apply();
        }

        if (intent_extras.hasExtra("extras_caminho_ficheiro")) {
            editor.putString("settings_caminho_ficheiro", extras.getString("extras_caminho_ficheiro", "settings_caminho_ficheiro"));
            editor.apply();
        }

        if (settings.getString("settings_auto_save", "false").equals("true")) {
            porta=settings.getString("settings_porta", "8080");
        } else {
            porta="8080";
        }

        caminho_ficheiro="NONE";
        if (!settings.getString("settings_caminho_ficheiro", "NONE").equals("NONE")) {
            caminho_ficheiro=settings.getString("settings_caminho_ficheiro", "NONE");
        }

        try {
            editor.putString("settings_auto_save", settings.getString("settings_auto_save","false"));
            editor.apply();
        } catch (Exception e) {}
        try {
            editor.putString("settings_auto_start", settings.getString("settings_auto_start","false"));
            editor.apply();
        } catch (Exception e) {}
        try {
            editor.putString("settings_escrever_ficheiro", settings.getString("settings_escrever_ficheiro","false"));
            editor.apply();
        } catch (Exception e) {}
        try {
            editor.putString("settings_caminho_ficheiro", settings.getString("settings_caminho_ficheiro","NONE"));
            editor.apply();
        } catch (Exception e) {}
        try {
            editor.putString("settings_desativar_auto_atualizador", settings.getString("settings_desativar_auto_atualizador","false"));
            editor.apply();
        } catch (Exception e) {}
        try {
            editor.putString("settings_ignorar_atualizacao", settings.getString("settings_ignorar_atualizacao","false"));
            editor.apply();
        } catch (Exception e) {}

        auto_save=settings.getString("settings_auto_save", "false");
        auto_start=settings.getString("settings_auto_start", "false");
        escrever_ficheiro=settings.getString("settings_escrever_ficheiro", "false");
        desativar_auto_atualizador=settings.getString("settings_desativar_auto_atualizador", "false");
        ignorar_atualizacao=settings.getString("settings_ignorar_atualizacao", "false");

        createNotificationChannel();

        porta_obj = findViewById(R.id.porta);
        estado_obj = findViewById(R.id.estado);
        dados_ip_obj = findViewById(R.id.dados_ip);
        dados_porta_obj = findViewById(R.id.dados_porta);
        ip_telemovel_obj = findViewById(R.id.ip);
        botao_start_stop = findViewById(R.id.botao_start_stop);
        botao_atualizar_ip = findViewById(R.id.botao_atualizar_ip);
        erro = findViewById(R.id.erro);
        ficheiro_auto = findViewById(R.id.ficheiro_auto);

        ip_telemovel_obj.setKeyListener(null);
        ip_telemovel_obj.setText(Ps3_proxy.externalIP());
        porta_obj.setText(porta);

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.dadi590.ps3proxyserverforandroid.Servico".equals(service.service.getClassName())) {
                //SharedPreferences settings = getSharedPreferences("App_settings", MODE_PRIVATE);
                estado_obj.setText(getString(R.string.estado) + "ON");
                dados_ip_obj.setText("IP: "+settings.getString("settings_ip", "N/A"));
                dados_porta_obj.setText(getString(R.string.porta) + settings.getString("settings_porta", "N/A"));
                botao_start_stop.setImageResource(R.drawable.pause);
                ligado=1;
            }
        }

        ficheiro_auto.setText(getString(R.string.ficheiro_auto) + "OFF");
        if (escrever_ficheiro.equals("true")) {
            ficheiro_auto.setText(getString(R.string.ficheiro_auto) + "ON");
        } else {
            ficheiro_auto.setText(getString(R.string.ficheiro_auto) + "OFF");
        }

        if (ligado==0) {
            estado_obj.setText(getString(R.string.estado) + "OFF");
            dados_ip_obj.setText("IP: N/A");
            dados_porta_obj.setText(getString(R.string.porta) + "N/A");
            botao_start_stop.setImageResource(R.drawable.play);
        }


        if (!intent_extras.hasExtra("extras_estava_ligada")) {
            if (desativar_auto_atualizador.contains("false")) {
                Date currentTime = Calendar.getInstance().getTime();
                //String dayOfTheWeek = (String) DateFormat.format("EEEE", currentTime); // Thursday
                String day          = (String) DateFormat.format("dd",   currentTime); // 20
                /*String monthString  = (String) DateFormat.format("MMM",  currentTime); // Jun
                String monthNumber  = (String) DateFormat.format("MM",   currentTime); // 06
                String year         = (String) DateFormat.format("yyyy", currentTime); // 2013
                String minute       = (String) DateFormat.format("mm",   currentTime); // 23
                String second       = (String) DateFormat.format("ss",   currentTime); // 23
                String hour       = (String) DateFormat.format("HH",   currentTime); // 23*/
                if (!day.equals(ignorar_atualizacao)) {
                    new Principal.Verificador_Atualizacoes().execute();
                }
            }
        }

        botao_atualizar_ip.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (!settings.getString("settings_ip", "false").equals(Ps3_proxy.externalIP())) {
                     ip_telemovel_obj.setText(Ps3_proxy.externalIP());
                     if (ligado==1) {
                         estado_obj.setText(getString(R.string.estado) + "OFF");
                         dados_ip_obj.setText("IP: N/A");
                         dados_porta_obj.setText(getString(R.string.porta) + "N/A");
                         botao_start_stop.setImageResource(R.drawable.play);
                         Intent stopMyService = new Intent();
                         stopMyService.setClass(Principal.this, Servico.class);
                         stopService(stopMyService);
                         Intent Reinicializador_Principal = new Intent();
                         Reinicializador_Principal.setClass(Principal.this, Reinicializador_Principal.class);
                         Reinicializador_Principal.putExtra("extras_iniciar_servidor", "true");
                         Reinicializador_Principal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         Reinicializador_Principal.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                         startActivity(Reinicializador_Principal);
                         System.exit(0);
                     }
                 } else {
                     ip_telemovel_obj.setText(Ps3_proxy.externalIP());
                 }
             }
         });

        if (ligar_servidor=="true") {
            ip = ip_telemovel_obj.getText().toString();
            porta = porta_obj.getText().toString();
            Intent startMyService = new Intent();
            startMyService.setClass(Principal.this, Servico.class);
            /*startMyService.putExtra("ip",ip);
            startMyService.putExtra("porta",porta);*/
            startService(startMyService);

            estado_obj.setText(getString(R.string.estado) + "ON");
            dados_ip_obj.setText("IP: " + ip);
            dados_porta_obj.setText(getString(R.string.porta) + porta);
            botao_start_stop.setImageResource(R.drawable.pause);
            ligado = 1;
            editor.putString("settings_ip", ip);
            editor.putString("settings_porta", porta);
            editor.apply();
        }

        if (intent_extras.hasExtra("extras_guardar_e_desligar")) {
            if (extras.getString("extras_guardar_e_desligar", "false").equals("true")) {
                finish();
            }
        }

        //Deixar aqui para ele guardar e fazer tudo o que tem a fazer antes de reiniciar tudo
        if (intent_extras.hasExtra("extras_reiniciar_servidor")) {
            if (extras.getString("extras_reiniciar_servidor", "false").equals("true")) {
                if (ligado==1) {
                    ip = ip_telemovel_obj.getText().toString();
                    porta = porta_obj.getText().toString();
                    estado_obj.setText(getString(R.string.estado) + "OFF");
                    dados_ip_obj.setText("IP: N/A");
                    dados_porta_obj.setText(getString(R.string.porta) + "N/A");
                    botao_start_stop.setImageResource(R.drawable.play);
                    Intent stopMyService = new Intent();
                    stopMyService.setClass(Principal.this, Servico.class);
                    stopService(stopMyService);
                    Intent Reinicializador_Principal = new Intent();
                    Reinicializador_Principal.setClass(Principal.this, Reinicializador_Principal.class);
                    Reinicializador_Principal.putExtra("extras_iniciar_servidor", "true");
                    Reinicializador_Principal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Reinicializador_Principal.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(Reinicializador_Principal);
                    System.exit(0);
                }
            }
        }

        botao_start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = ip_telemovel_obj.getText().toString();
                //Primeiro o ligado == 1 porque senão era sempre ativado, porque depois de o ligado == 0 acabar, ligado = 1 e isto ia parar o servidor logo depois de ter sido ligado.
                if (ligado == 1) {
                    if (!porta_obj.getText().toString().equals("")) {
                        if (Integer.parseInt(porta_obj.getText().toString()) >= 1024 && Integer.parseInt(porta_obj.getText().toString()) <= 65535) {
                            porta = porta_obj.getText().toString();
                        }
                    }
                    editor.putString("settings_ip", ip);
                    editor.putString("settings_porta", porta);
                    editor.apply();
                    estado_obj.setText(getString(R.string.estado) + "OFF");
                    dados_ip_obj.setText("IP: N/A");
                    dados_porta_obj.setText(getString(R.string.porta) + "N/A");
                    botao_start_stop.setImageResource(R.drawable.play);
                    Intent stopMyService = new Intent();
                    stopMyService.setClass(Principal.this, Servico.class);
                    stopService(stopMyService);
                    Intent Reinicializador_Principal = new Intent();
                    Reinicializador_Principal.setClass(Principal.this, Reinicializador_Principal.class);
                    Reinicializador_Principal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Reinicializador_Principal.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(Reinicializador_Principal);
                    System.exit(0);
                }
                porta = porta_obj.getText().toString();
                if (!porta.equals("")) {
                    if (Integer.parseInt(porta) >= 1024 && Integer.parseInt(porta) <= 65535) {
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
                            botao_start_stop.setImageResource(R.drawable.pause);
                            ligado = 1;
                            editor.putString("settings_ip", ip);
                            editor.putString("settings_porta", porta);
                            editor.apply();
                        }
                    } else {
                        erro.setText(getString(R.string.erro));
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
        inflater.inflate(R.menu.menu_principal_normal,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!porta_obj.getText().toString().equals("")) {
            if (Integer.parseInt(porta_obj.getText().toString()) >= 1024 && Integer.parseInt(porta_obj.getText().toString()) <= 65535) {
                porta = porta_obj.getText().toString();
            }
        }
        ip = ip_telemovel_obj.getText().toString();
        editor.putString("settings_ip", ip);
        editor.putString("settings_porta", porta);
        editor.apply();
        if (item.getItemId()==R.id.instrucoes) {
            Intent Instrucoes = new Intent();
            Instrucoes.setClass(Principal.this, Instrucoes.class);
            Instrucoes.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Instrucoes.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(Instrucoes);
        } else if (item.getItemId()==R.id.definicoes) {
            Intent Definicoes = new Intent();
            Definicoes.setClass(Principal.this, Definicoes.class);
            Definicoes.putExtra("extras_auto_save",auto_save);
            Definicoes.putExtra("extras_auto_start",auto_start);
            Definicoes.putExtra("extras_escrever_ficheiro",escrever_ficheiro);
            Definicoes.putExtra("extras_caminho_ficheiro",caminho_ficheiro);
            Definicoes.putExtra("extras_desativar_auto_atualizador",desativar_auto_atualizador);
            Definicoes.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Definicoes.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(Definicoes);
            finish();
        } else if (item.getItemId()==R.id.creditos) {
            Intent Creditos = new Intent(new Intent (Principal.this, Creditos.class));
            Creditos.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Creditos.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(Creditos);
        }
        return super.onOptionsItemSelected(item);
    }

    private class Verificador_Atualizacoes extends AsyncTask<String, Void, Void> {

        private Context ctx;
        private String updates_txt;
        private String[] updates_txt_lista;
        private String[] updates_txt_lista_linhas_versoes;
        private String link_descarga = "NONE";

        double versionName = Double.parseDouble(BuildConfig.VERSION_NAME);

        int continuar=1;

        @Override
        protected Void doInBackground(String... url) {

            try {
                updates_txt = new Scanner(new URL("https://raw.githubusercontent.com/DADi590/PS3-Proxy-Server-for-Android/master/UPDATES.txt").openStream(), "UTF-8").useDelimiter("\\A").next();

            } catch (Exception e) {
                try {
                    updates_txt = new Scanner(new URL("https://github.com/DADi590/PS3-Proxy-Server-for-Android/raw/master/UPDATES.txt").openStream(), "UTF-8").useDelimiter("\\A").next();
                } catch (Exception w) {
                    continuar=0;
                }
            }

            return null;
        }

        protected void onPostExecute(Void result) {

            int atualizacao_encontrada=0;

            if (continuar==1) {
                updates_txt_lista = updates_txt.split("\\r?\\n");
                for (int i = 0; i < updates_txt_lista.length; i++) {
                    try {
                        updates_txt_lista_linhas_versoes = updates_txt_lista[i].split(" --> ");
                        if (updates_txt_lista_linhas_versoes[1].substring(0, 4).equals("http")) {
                            if (Double.parseDouble(updates_txt_lista_linhas_versoes[0].split(" ")[0]) > versionName) {
                                link_descarga = updates_txt_lista_linhas_versoes[1];
                                atualizacao_encontrada = 1;
                                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                                System.out.println(link_descarga);
                                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                System.out.println("---------------------------------------------------");
                System.out.println(link_descarga);
                System.out.println("---------------------------------------------------");

                if (atualizacao_encontrada == 1) {
                    Intent Atualizacao_Encontrada = new Intent();
                    Atualizacao_Encontrada.setClass(Principal.this, Atualizacao_Encontrada.class);
                    Atualizacao_Encontrada.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Atualizacao_Encontrada.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    Atualizacao_Encontrada.putExtra("texto_updates_txt", updates_txt);
                    Atualizacao_Encontrada.putExtra("link_descarga", link_descarga);
                    startActivity(Atualizacao_Encontrada);
                    finish();
                }
            }
        }
    }
}