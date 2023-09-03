package com.dadi590.ps3proxyserverforandroid;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Update_Found extends AppCompatActivity {

    private static final String CHANNEL_ID_4 = "NOTIFICATIONS_com.dadi590.ps3proxyserverforandroid.PPSFA_save_update_file_failed";

    public static final short id_update_file_download = 3234_5;

    public static final short MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3234_2;

    private Bundle extras;
    private Intent intent;

    private String updates_txt;
    private String download_link;
    private String[] updates_txt_list;
    private String[] updates_txt_list_lines_versions;
    private List<String> changelog_list = new ArrayList<>();
    private String[] changelog_array;
    private String changelog_text="";
    private TextView changelog_text_obj;
    private Button button_install;
    private Button button_ignore_a_day;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    private DownloadManager downloadManager;

    private int ignore_a_day = 0;
    private int use_onDestroy_alternative = 0;
    //private ProgressDialog progressDialog;
    private NotificationManager notificationManager;
    private Intent notificationIntent;
    private NotificationCompat.Builder builder;
    private int downloading=0;
    private int downloaded=0;
    private File directory;
    private String file_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_found);

        Toolbar toolbar = findViewById(R.id.toolbar_update_found);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        changelog_text_obj=findViewById(R.id.changelog_text);
        button_install=findViewById(R.id.button_install);
        button_ignore_a_day=findViewById(R.id.button_ignore_a_day);

        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        editor = settings.edit();
        extras = getIntent().getExtras();
        intent = getIntent();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(Update_Found.this, createNotificationChannel(CHANNEL_ID_4));
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.main_old_icon_without_borders);
            builder.setAutoCancel(true);
        } else {
            builder.setSmallIcon(R.drawable.main_new_icon);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.main_old_icon_without_borders));
            builder.setAutoCancel(true);
        }

        if (intent.hasExtra("extras_text_updates_txt")) {
            updates_txt = extras.getString("extras_text_updates_txt","ERROR");
        } else {
            onDestroy_alternative();
        }

        if (intent.hasExtra("extras_download_link")) {
            download_link = extras.getString("extras_download_link","ERROR");
        } else {
            onDestroy_alternative();
        }

        updates_txt_list=updates_txt.split("\\r?\\n");
        for (int i=0;i<updates_txt_list.length;i++) {
            if (updates_txt_list[i].contains(" --> ")) {
                updates_txt_list_lines_versions = updates_txt_list[i].split(" --> ");
                if (updates_txt_list[i].contains("|NEW|")) {
                    changelog_list.add("<u><b>Version " + updates_txt_list_lines_versions[0] + "</b></u>");
                } else {
                    changelog_list.add("<b>Version " + updates_txt_list_lines_versions[0] + "</b>");
                }
            }
            if (updates_txt_list[i].contains("- ") && updates_txt_list[i].length()>2) {
                changelog_list.add(updates_txt_list[i]);
            }
        }
        changelog_array=changelog_list.toArray(new String[changelog_list.size()]);
        for (int i = 0;i<changelog_array.length; i++) {
            if (i!=changelog_array.length) {
                changelog_text+="<p>"+changelog_array[i]+"</p>";
            } else {
                changelog_text+=changelog_array[i];
            }
        }

        changelog_text_obj.setText(Html.fromHtml(changelog_text));

        button_ignore_a_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ignore_a_day=1;
                onDestroy_alternative();
            }
        });

        button_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloading==0) {
                    if (downloaded==0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(Update_Found.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(Update_Found.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                            } else {
                                new DownloadFileFromURL().execute();
                            }
                        } else {
                            new DownloadFileFromURL().execute();
                        }
                        // execute this when the downloader must be fired
                        /*Uri uri = Uri.parse(download_link);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);*/
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                System.out.println("Permission is granted");
                            } else {

                                System.out.println("Permission is revoked");
                                ActivityCompat.requestPermissions(Update_Found.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        }
                        else { //permission is automatically granted on sdk<23 upon installation
                            System.out.println("Permission is granted");
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                System.out.println("Permission is granted");
                                new DownloadFileFromURL().execute("https://www.github.com/Edw590/PS3-Proxy-Server-for-Android/releases/download/v1.0/PS3_Proxy_Server_for_Android_v1.0.apk");
                            }
                        }*/
                    } else {
                        File file = new File(directory, file_name);
                        Uri fileUri = Uri.fromFile(file);
                        if (Build.VERSION.SDK_INT >= 24) {
                            fileUri = FileProvider.getUriForFile(Update_Found.this, Update_Found.this.getPackageName() + ".authorityStr",
                                    file);
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
                        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                        intent.setDataAndType(fileUri, "application/vnd.android" + ".package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            new DownloadFileFromURL().execute();
        } else {
            Uri uri = Uri.parse(download_link);
            notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
            PendingIntent pendingIntent = PendingIntent.getActivity(Update_Found.this,
                    0, notificationIntent, 0);
            builder.setContentTitle(getString(R.string.notificaion_download_update_file_failed_title));
            builder.setContentText(getString(R.string.notificaion_download_update_file_failed_content));
            builder.setContentIntent(pendingIntent);
            notificationManager.notify(id_update_file_download, builder.build());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        int lengthOfFile;
        long total=-2;

        @Override
        protected void onPreExecute() {
            downloading = 1;
            downloaded=0;
            directory = new File(Environment.getExternalStorageDirectory(),"PPSFA_updates_delete_this_please");
            file_name = download_link.split("/")[download_link.split("/").length-1];
            //progressDialog = new ProgressDialog(Update_Found.this);
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            //progressDialog.setProgressNumberFormat("%dKB/%dKB");
            //progressDialog.setMessage(getString(R.string.download_update_file));
            //progressDialog.setCancelable(false);
            //progressDialog.setCanceledOnTouchOutside(false);
            //progressDialog.show();
            button_install.setText("0KB/0KB");
            button_install.setBackgroundResource(android.R.color.holo_orange_light);
        }


        @Override
        protected String doInBackground(String... arg0) {


            try {
                URL url = new URL(download_link);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                FileOutputStream f = new FileOutputStream(new File(directory, file_name));

                byte[] buffer = new byte[1024];
                int len1 = 0;
                total = 0;
                //progressDialog.setMax(lengthOfFile / 1024);
                //builder.setProgress(lengthOfFile, 0, false);
                builder.setContentTitle(getString(R.string.notification_download_update_file_title));
                builder.setContentText(getString(R.string.download_update_file));
                notificationManager.notify(id_update_file_download, builder.build());
                while ((len1 = input.read(buffer)) > 0) {
                    total += len1;
                    //publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    publishProgress("");
                    f.write(buffer, 0, len1);
                }
                f.close();
            } catch (Exception e) {
                total=-2;
                Uri uri = Uri.parse(download_link);
                notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
                PendingIntent pendingIntent = PendingIntent.getActivity(Update_Found.this,
                        0, notificationIntent, 0);
                builder.setContentTitle(getString(R.string.notificaion_download_update_file_failed_title));
                builder.setContentText(getString(R.string.notificaion_download_update_file_failed_content));
                builder.setContentIntent(pendingIntent);
                notificationManager.notify(id_update_file_download, builder.build());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            button_install.setText(((int) total/1024)+"KB/"+(lengthOfFile/1024)+"KB");
            /*progressDialog.setProgress((int) total/1024);
            if ((int) ((total * 100) / lengthOfFile)%10==0) {
                builder.setProgress(lengthOfFile, (int) total, false);
                notificationManager.notify(id_update_file_download, builder.build());
            }*/
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            downloading=0;
            button_install.setText(getString(R.string.download_install_update));
            button_install.setBackgroundResource(android.R.drawable.btn_default_small);
            //progressDialog.dismiss();
            notificationManager.cancel(id_update_file_download);
            if (total!=lengthOfFile) {
                Uri uri = Uri.parse(download_link);
                notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
                PendingIntent pendingIntent = PendingIntent.getActivity(Update_Found.this,
                        0, notificationIntent, 0);
                builder.setContentTitle(getString(R.string.notificaion_download_update_file_failed_title));
                builder.setContentText(getString(R.string.notificaion_download_update_file_failed_content));
                builder.setContentIntent(pendingIntent);
                notificationManager.notify(id_update_file_download, builder.build());
            } else {
                downloaded=1;
                button_install.setText(getString(R.string.install_update));
                Toast.makeText(getApplicationContext(), R.string.toast_save_update_file_succeeded, Toast.LENGTH_LONG).show();
                File file = new File(directory, file_name);
                Uri fileUri = Uri.fromFile(file);
                if (Build.VERSION.SDK_INT >= 24) {
                    fileUri = FileProvider.getUriForFile(Update_Found.this, Update_Found.this.getPackageName() + ".authorityStr",
                            file);
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.setDataAndType(fileUri, "application/vnd.android" + ".package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        }

    }

    public void onDestroy_alternative() {
        Intent Main = new Intent(Update_Found.this, Main.class);
        Main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        Date currentTime = Calendar.getInstance().getTime();
        //String dayOfTheWeek = (String) DateFormat.format("EEEE", currentTime); // Thursday
        String day          = (String) DateFormat.format("dd",   currentTime); // 20
        /*String monthString  = (String) DateFormat.format("MMM",  currentTime); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   currentTime); // 06
        String year         = (String) DateFormat.format("yyyy", currentTime); // 2013
        String minute       = (String) DateFormat.format("mm",   currentTime); // 23
        String second       = (String) DateFormat.format("ss",   currentTime); // 23
        String hour       = (String) DateFormat.format("HH",   currentTime); // 23*/
        if (ignore_a_day==1) {
            editor.putString("settings_ignore_update", day);
            editor.apply();
        } else {
            editor.putString("settings_ignore_update", "false");
            editor.apply();
        }
        Main.putExtra("extras_was_on","true");
        startActivity(Main);
        use_onDestroy_alternative=1;
        finish();
    }

    @Override
    public void onDestroy() {
        if (use_onDestroy_alternative==0) {
            Intent Main = new Intent(Update_Found.this, Main.class);
            Main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            Date currentTime = Calendar.getInstance().getTime();
            //String dayOfTheWeek = (String) DateFormat.format("EEEE", currentTime); // Thursday
            String day = (String) DateFormat.format("dd", currentTime); // 20
            /*String monthString  = (String) DateFormat.format("MMM",  currentTime); // Jun
            String monthNumber  = (String) DateFormat.format("MM",   currentTime); // 06
            String year         = (String) DateFormat.format("yyyy", currentTime); // 2013
            String minute       = (String) DateFormat.format("mm",   currentTime); // 23
            String second       = (String) DateFormat.format("ss",   currentTime); // 23
            String hour       = (String) DateFormat.format("HH",   currentTime); // 23*/
            if (ignore_a_day == 1) {
                editor.putString("settings_ignore_update", day);
                editor.apply();
            } else {
                editor.putString("settings_ignore_update", "false");
                editor.apply();
            }
            Main.putExtra("extras_was_on", "true");
            startActivity(Main);
        }
        super.onDestroy();
    }

    private String createNotificationChannel(String channel_id) {

        //Copied from developer.android.com
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.service_name);
            String description = getString(R.string.service_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
        return channel_id;
    }
}
