package com.dadi590.ps3proxyserverforandroid;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Atualizacao_Encontrada extends AppCompatActivity {

    private Activity activity;

    private Bundle extras;
    private Intent intent_extras;

    private String updates_txt;
    private String link_descarga;
    private String[] updates_txt_lista;
    private String[] updates_txt_lista_linhas_versoes;
    private List<String> lista_mudancas_lista = new ArrayList<>();
    private String[] lista_mudancas_array;
    private String lista_mudancas_texto="";
    private TextView lista_mudancas_texto_obj;
    private Button botao_instalar;
    private Button botao_ignorar;

    private DownloadManager downloadManager;

    private int ignorar = 0;
    private int onDestroy_alternativo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizacao_encontrada);

        Toolbar toolbar = findViewById(R.id.toolbar_atualizacao_encontrada);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lista_mudancas_texto_obj=findViewById(R.id.lista_mudancas_texto);
        botao_instalar=findViewById(R.id.botao_instalar);
        botao_ignorar=findViewById(R.id.botao_ignorar);

        extras = getIntent().getExtras();
        intent_extras = getIntent();

        if (intent_extras.hasExtra("texto_updates_txt")) {
            updates_txt = extras.getString("texto_updates_txt","ERROR");
        } else {
            onDestroy_alternativo();
        }

        if (intent_extras.hasExtra("link_descarga")) {
            link_descarga = extras.getString("link_descarga","ERROR");
        } else {
            onDestroy_alternativo();
        }

        updates_txt_lista=updates_txt.split("\\r?\\n");
        int primeira_versao=1;
        for (int i=0;i<updates_txt_lista.length;i++) {
            if (updates_txt_lista[i].contains(" --> ")) {
                updates_txt_lista_linhas_versoes = updates_txt_lista[i].split(" --> ");
                if (primeira_versao==1) {
                    lista_mudancas_lista.add("Version "+updates_txt_lista_linhas_versoes[0]+"\n");
                    primeira_versao=0;
                } else {
                    lista_mudancas_lista.add("\n\nVersion " + updates_txt_lista_linhas_versoes[0] + "\n");
                }
            }
            if (updates_txt_lista[i].contains("- ") && !updates_txt_lista[i].contains("<--")) {
                lista_mudancas_lista.add(updates_txt_lista[i]);
            }
        }
        lista_mudancas_array=lista_mudancas_lista.toArray(new String[lista_mudancas_lista.size()]);
        for (int i = 0;i<lista_mudancas_array.length; i++) {
            lista_mudancas_texto+=lista_mudancas_array[i]+"\n";
        }

        System.out.println("--------------------------------------------");
        System.out.println(lista_mudancas_texto);
        System.out.println("--------------------------------------------");

        lista_mudancas_texto_obj.setText(lista_mudancas_texto);

        botao_ignorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ignorar=1;
                onDestroy_alternativo();
            }
        });

        botao_instalar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // execute this when the downloader must be fired
                Uri uri = Uri.parse(link_descarga);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission is granted");
                    } else {

                        System.out.println("Permission is revoked");
                        ActivityCompat.requestPermissions(Atualizacao_Encontrada.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
                else { //permission is automatically granted on sdk<23 upon installation
                    System.out.println("Permission is granted");
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission is granted");
                        new DownloadFileFromURL().execute("https://www.github.com/DADi590/PS3-Proxy-Server-for-Android/releases/download/v1.0/PS3_Proxy_Server_for_Android_v1.0.apk");
                    }
                }*/
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onDestroy_alternativo();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // NÃO USADO!!!!!!!!! Mas podia ser, se soubesse como o fazer.............
    class DownloadFileFromURL extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(String... arg0) {

            Uri uri= Uri.parse(arg0[0]);

            // Create request for android download manager
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                    DownloadManager.Request.NETWORK_MOBILE);

            // set title and description
            request.setTitle("Data Download");
            request.setDescription("Android Data download using DownloadManager.");

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            //set the local destination for download file to a path within the application's external files directory
            request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().toString(),"PPSFA_update_file.apk");
            request.setMimeType("*/*");
            downloadManager.enqueue(request);

            return null;
        }

        /**
         * Updating progress bar
         * */

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            System.out.println("Downloaded");
            // dismiss the dialog after the file was downloaded
            /*Intent promptInstall = new Intent(Intent.ACTION_INSTALL_PACKAGE)
                    .setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().toString()+"/PPSFA_update_file.apk"),
                            "application/vnd.android.package-archive");
            startActivity(promptInstall);*/
            File toInstall = new File("/storage/1BFC-3D09/Download", "PPSFA_update_file.apk");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(Atualizacao_Encontrada.this, BuildConfig.APPLICATION_ID + ".provider", toInstall);
                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(apkUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(intent);
            } else {
                Uri apkUri = Uri.fromFile(toInstall);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }

        }

    }

    public void onDestroy_alternativo() {
        Intent Principal = new Intent(Atualizacao_Encontrada.this, Principal.class);
        Principal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Principal.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        Date currentTime = Calendar.getInstance().getTime();
        //String dayOfTheWeek = (String) DateFormat.format("EEEE", currentTime); // Thursday
        String day          = (String) DateFormat.format("dd",   currentTime); // 20
        /*String monthString  = (String) DateFormat.format("MMM",  currentTime); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   currentTime); // 06
        String year         = (String) DateFormat.format("yyyy", currentTime); // 2013
        String minute       = (String) DateFormat.format("mm",   currentTime); // 23
        String second       = (String) DateFormat.format("ss",   currentTime); // 23
        String hour       = (String) DateFormat.format("HH",   currentTime); // 23*/
        System.out.println("-------------------------------------------");
        System.out.println(currentTime);
        System.out.println(day);
        System.out.println("-------------------------------------------");
        if (ignorar==1) {
            Principal.putExtra("extras_ignorar_atualizacao",day);
        } else {
            Principal.putExtra("extras_ignorar_atualizacao","false");
        }
        Principal.putExtra("extras_estava_ligada","true");
        startActivity(Principal);
        onDestroy_alternativo=1;
        finish();
    }

    @Override
    public void onDestroy() {
        if (onDestroy_alternativo==0) {
            Intent Principal = new Intent(Atualizacao_Encontrada.this, Principal.class);
            Principal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Principal.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            Date currentTime = Calendar.getInstance().getTime();
            //String dayOfTheWeek = (String) DateFormat.format("EEEE", currentTime); // Thursday
            String day = (String) DateFormat.format("dd", currentTime); // 20
            /*String monthString  = (String) DateFormat.format("MMM",  currentTime); // Jun
            String monthNumber  = (String) DateFormat.format("MM",   currentTime); // 06
            String year         = (String) DateFormat.format("yyyy", currentTime); // 2013
            String minute       = (String) DateFormat.format("mm",   currentTime); // 23
            String second       = (String) DateFormat.format("ss",   currentTime); // 23
            String hour       = (String) DateFormat.format("HH",   currentTime); // 23*/
            System.out.println("-------------------------------------------");
            System.out.println(currentTime);
            System.out.println(day);
            System.out.println("-------------------------------------------");
            if (ignorar == 1) {
                Principal.putExtra("extras_ignorar_atualizacao", day);
            } else {
                Principal.putExtra("extras_ignorar_atualizacao", "false");
            }
            Principal.putExtra("extras_estava_ligada", "true");
            startActivity(Principal);
        }
        super.onDestroy();
    }
}