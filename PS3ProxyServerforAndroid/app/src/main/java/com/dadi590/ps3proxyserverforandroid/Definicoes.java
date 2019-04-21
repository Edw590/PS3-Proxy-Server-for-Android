package com.dadi590.ps3proxyserverforandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.net.URL;
import java.util.Scanner;

public class Definicoes extends AppCompatActivity {

    private Switch auto_start_switch;
    private Switch auto_save_switch;
    private Switch escrever_ficheiro_switch;
    private Switch desativar_auto_atualizador_switch;
    private Bundle extras;
    private String auto_save;
    private String auto_start;
    private String escrever_ficheiro;
    private String caminho_ficheiro;
    private String desativar_auto_atualizador;
    private Button botao_escolher_ficheiro;
    private Button botao_verificar_atualizacoes_manualmente;
    private TextView ficheiro_escolhido;
    private TextView desativar_auto_atualizador_obj;
    private TextView atualizacao_nao_encontrada;
    private SharedPreferences settings;
    private String versao[];
    private int verificar_atualizacoes_manualmente=0;

    Intent Principal;

    Intent intent = new Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definicoes);

        Principal = new Intent(Definicoes.this, Principal.class);

        Toolbar toolbar = findViewById(R.id.toolbar_instrucoes);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        auto_save_switch = findViewById(R.id.auto_save_switch);
        auto_start_switch = findViewById(R.id.auto_start_switch);
        escrever_ficheiro_switch = findViewById(R.id.escrever_ficheiro_switch);
        botao_escolher_ficheiro = findViewById(R.id.botao_escolher_ficheiro);
        ficheiro_escolhido = findViewById(R.id.ficheiro_escolhido);
        desativar_auto_atualizador_obj = findViewById(R.id.desativar_auto_atualizador);
        desativar_auto_atualizador_switch = findViewById(R.id.desativar_auto_atualizador_switch);
        botao_verificar_atualizacoes_manualmente = findViewById(R.id.botao_verificar_atualizacoes_manualmente);
        atualizacao_nao_encontrada = findViewById(R.id.atualizacao_nao_encontrada);

        extras = getIntent().getExtras();

        auto_save=extras.getString("extras_auto_save");
        auto_start=extras.getString("extras_auto_start");
        escrever_ficheiro=extras.getString("extras_escrever_ficheiro");
        caminho_ficheiro=extras.getString("extras_caminho_ficheiro");
        desativar_auto_atualizador=extras.getString("extras_desativar_auto_atualizador");
        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);

        ficheiro_escolhido.setText(getString(R.string.ficheiro_escolhido) + caminho_ficheiro);

        if (auto_save.contains("true")) {
            auto_save_switch.setChecked(true);
        } else {
            auto_save_switch.setChecked(false);
        }
        if (auto_start.contains("true")) {
            auto_start_switch.setChecked(true);
        } else {
            auto_start_switch.setChecked(false);
        }
        if (escrever_ficheiro.contains("true")) {
            escrever_ficheiro_switch.setChecked(true);
        } else {
            escrever_ficheiro_switch.setChecked(false);
        }
        if (desativar_auto_atualizador.contains("true")) {
            desativar_auto_atualizador_switch.setChecked(true);
        } else {
            desativar_auto_atualizador_switch.setChecked(false);
        }

        botao_escolher_ficheiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(intent, getString(R.string.escolher_ficheiro)), 123);
            }
        });

        botao_verificar_atualizacoes_manualmente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Definicoes.Verificador_Atualizacoes().execute();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        Principal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Principal.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        if (auto_start_switch.isChecked()) {
            Principal.putExtra("extras_auto_start","true");
        } else {
            Principal.putExtra("extras_auto_start","false");
        }
        if (auto_save_switch.isChecked()) {
            Principal.putExtra("extras_auto_save","true");
        } else {
            Principal.putExtra("extras_auto_save","false");
        }
        if (desativar_auto_atualizador_switch.isChecked()) {
            Principal.putExtra("extras_desativar_auto_atualizador","true");
        } else {
            Principal.putExtra("extras_desativar_auto_atualizador","false");
        }
        if (escrever_ficheiro_switch.isChecked()) {
            Principal.putExtra("extras_escrever_ficheiro","true");
            if (escrever_ficheiro.equals("false")) {
                Principal.putExtra("extras_reiniciar_servidor", "true");
            }
        } else {
            Principal.putExtra("extras_escrever_ficheiro","false");
            if (escrever_ficheiro.equals("true")) {
                Principal.putExtra("extras_reiniciar_servidor", "true");
            }
        }
        if (!settings.getString("settings_caminho_ficheiro","NONE").equals(caminho_ficheiro)) {
            Principal.putExtra("extras_reiniciar_servidor", "true");
        }
        Principal.putExtra("extras_caminho_ficheiro",caminho_ficheiro);
        Principal.putExtra("extras_estava_ligada","true");
        startActivity(Principal);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK) {
            if (data.getData().getPath().substring(0, 10).equals("/document/")) {
                caminho_ficheiro = Obter_caminho_real_pelo_Uri.getPath(Definicoes.this,data.getData());
                ficheiro_escolhido.setText(getString(R.string.ficheiro_escolhido) + caminho_ficheiro);
            } else {
                caminho_ficheiro = data.getData().getPath();
                ficheiro_escolhido.setText(getString(R.string.ficheiro_escolhido) + caminho_ficheiro);
            }
        }
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
                    Atualizacao_Encontrada.setClass(Definicoes.this, Atualizacao_Encontrada.class);
                    Atualizacao_Encontrada.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Atualizacao_Encontrada.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    Atualizacao_Encontrada.putExtra("texto_updates_txt", updates_txt);
                    Atualizacao_Encontrada.putExtra("link_descarga", link_descarga);
                    startActivity(Atualizacao_Encontrada);
                    Principal.putExtra("extras_guardar_e_desligar","true");
                    verificar_atualizacoes_manualmente=1;
                    finish();
                } else {
                    atualizacao_nao_encontrada.setText(getString(R.string.atualizacao_nao_encontrada));
                }
            } else {
                atualizacao_nao_encontrada.setText(getString(R.string.atualizacao_nao_encontrada_erro));
            }
        }
    }
}