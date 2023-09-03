package com.dadi590.ps3proxyserverforandroid;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import lib.folderpicker.FolderPicker;

import static com.dadi590.ps3proxyserverforandroid.Rules.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static com.dadi590.ps3proxyserverforandroid.Update_Found.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

public class Real_time_logging extends AppCompatActivity {

    public static final String CHANNEL_ID_3 = "NOTIFICATIONS_com.dadi590.ps3proxyserverforandroid.PPSFA_save_file_failed";

    public static final int id_save_logfile_failed = 3234_3;
    public static final int id_save_logfile_succeeded = 3234_4;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private TextView logs_obj;
    //private long startTime;
    private NotificationManager notificationManager;
    private Intent notificationIntent;
    //private int check_logs=1;
    ScrollView scrollview_obj;
    private Button button_check_logs;
    private int log_string_busy=0;
    private int log_num=0;
    private String session_id;
    private String see_requests;
    private String search_in_logs;
    private String log_string="";
    private Handler handler = new Handler();
    Context context;
    Toast toast;

    //I can't stop_server this, I think. At least the phone gets REALLY hot after sometime and I think it's this. So it's disabled as of now.
    //FIX THIS!!!!!!!!!!!!! (no idea how)
    /*Thread Logs_printer = new Thread() {
        public void run() {
            startTime = System.currentTimeMillis();
            while (Logs_printer!=null && check_logs==1) {
                if (System.currentTimeMillis() == startTime + 1000) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (settings.getString("settings_log_string_busy", "true").equals("false")) {
                                if (Logs_printer!=null && check_logs==1) {
                                    logs_obj.setText(Html.fromHtml(settings.getString("settings_log_string", "ERROR")));
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            View lastChild = scrollview_obj.getChildAt(scrollview_obj.getChildCount() - 1);
                                            int bottom = lastChild.getBottom() + scrollview_obj.getPaddingBottom();
                                            int sy = scrollview_obj.getScrollY();
                                            int sh = scrollview_obj.getHeight();
                                            int delta = bottom - (sy + sh);
                                            scrollview_obj.smoothScrollBy(0,delta);
                                        }
                                    }, 500);
                                }
                            }
                            startTime = System.currentTimeMillis();
                        }
                    });
                }
            }
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_logs);

        Toolbar toolbar = findViewById(R.id.toolbar_logs_tempo_real);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        editor = settings.edit();

        logs_obj = findViewById(R.id.logs);
        scrollview_obj = findViewById(R.id.scrollview);
        button_check_logs = findViewById(R.id.button_check_logs);
        context = getApplicationContext();

        //Logs_printer.start_server();

        button_check_logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (settings.getString("settings_log_string_busy", "true").equals("false")) {
                    if (!logs_obj.getText().equals(settings.getString("settings_log_string", "ERROR"))) {
                        logs_obj.setText(Html.fromHtml(settings.getString("settings_log_string", "ERROR")));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                View lastChild = scrollview_obj.getChildAt(scrollview_obj.getChildCount() - 1);
                                int bottom = lastChild.getBottom() + scrollview_obj.getPaddingBottom();
                                int sy = scrollview_obj.getScrollY();
                                int sh = scrollview_obj.getHeight();
                                int delta = bottom - (sy + sh);
                                scrollview_obj.smoothScrollBy(0, delta);
                            }
                        }, 500);
                    }
                }*/
                if (log_string_busy==0) {
                    String log_string_before=log_string;
                    log_string_busy=1;
                    try {
                        Process process = Runtime.getRuntime().exec("logcat -d");
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));

                        session_id = settings.getString("settings_session_id", "NONE");
                        see_requests = settings.getString("settings_see_requests", "false");
                        if (see_requests.equals("true")) {
                            search_in_logs = "PPSFA > ";
                        } else {
                            search_in_logs = "PPSFA - ";
                        }
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.contains("PPSFA ")) {
                                if (line.contains("SI:" + session_id + " ")) {
                                    if (line.contains("LN:" + log_num + " ")) {
                                        if (line.contains("PPSFA - ") || line.contains(search_in_logs)) {
                                            log_string += (line.substring(line.indexOf("P") + 8) + "\n\n");
                                        }
                                        log_num += 1;
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                    }
                    if (!log_string_before.equals(log_string)) {
                        logs_obj.setText(Html.fromHtml(log_string));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                View lastChild = scrollview_obj.getChildAt(scrollview_obj.getChildCount() - 1);
                                int bottom = lastChild.getBottom() + scrollview_obj.getPaddingBottom();
                                int sy = scrollview_obj.getScrollY();
                                int sh = scrollview_obj.getHeight();
                                int delta = bottom - (sy + sh);
                                scrollview_obj.smoothScrollBy(0,delta);
                            }
                        }, 500);
                    }
                    log_string_busy=0;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rules_normal,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
        } else if (item.getItemId()==R.id.save_logs) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(Real_time_logging.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(Real_time_logging.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    if (ContextCompat.checkSelfPermission(Real_time_logging.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(Real_time_logging.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    } else {
                        Intent intent = new Intent(Real_time_logging.this, FolderPicker.class);
                        intent.putExtra("title", getString(R.string.choose_folder));
                        startActivityForResult(intent, 3234_2);
                    }
                }
            } else {
                Intent intent = new Intent(Real_time_logging.this, FolderPicker.class);
                intent.putExtra("title", getString(R.string.choose_folder));
                startActivityForResult(intent, 3234_2);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Real_time_logging.this, FolderPicker.class);
            intent.putExtra("title", getString(R.string.choose_folder));
            startActivityForResult(intent, 3234_2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 3234_2 && resultCode == RESULT_OK) {
            String folder_path = intent.getExtras().getString("data");
            String filename = "PPSFA_Logfile.html";
            File file = new File (folder_path, filename);
            String fileContents = log_string;
            if (!file.exists()) {
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(fileContents.getBytes());
                    fos.close();
                    toast = Toast.makeText(context, R.string.notificaion_save_logs_succeeded_content, Toast.LENGTH_SHORT);
                    toast.show();
                } catch (Exception e) {
                    notificationIntent = new Intent(Real_time_logging.this, Real_time_logging.class);
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    PendingIntent pendingIntent = PendingIntent.getActivity(Real_time_logging.this,
                            0, notificationIntent, 0);
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(Real_time_logging.this, createNotificationChannel(CHANNEL_ID_3))
                                .setContentTitle(getString(R.string.notificaion_save_logs_failed_title))
                                .setContentText(getString(R.string.notificaion_save_logs_failed_content))
                                .setSmallIcon(R.drawable.main_old_icon_without_borders)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        Notification notification = builder.build();
                        notificationManager.notify(id_save_logfile_failed, notification);
                    } else {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(Real_time_logging.this, createNotificationChannel(CHANNEL_ID_3))
                                .setContentTitle(getString(R.string.notificaion_save_logs_failed_title))
                                .setContentText(getString(R.string.notificaion_save_logs_failed_content))
                                .setSmallIcon(R.drawable.main_new_icon)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.main_old_icon_without_borders))
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        Notification notification = builder.build();
                        notificationManager.notify(id_save_logfile_failed, notification);
                    }
                }
            } else {
                notificationIntent = new Intent(Real_time_logging.this, Real_time_logging.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                PendingIntent pendingIntent = PendingIntent.getActivity(Real_time_logging.this,
                        0, notificationIntent, 0);
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Real_time_logging.this, createNotificationChannel(CHANNEL_ID_3))
                            .setContentTitle(getString(R.string.notificaion_save_logs_file_exists_title))
                            .setContentText(getString(R.string.notificaion_save_logs_file_exists_content))
                            .setSmallIcon(R.drawable.main_old_icon_without_borders)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    Notification notification = builder.build();
                    notificationManager.notify(id_save_logfile_failed, notification);
                } else {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Real_time_logging.this, createNotificationChannel(CHANNEL_ID_3))
                            .setContentTitle(getString(R.string.notificaion_save_logs_file_exists_title))
                            .setContentText(getString(R.string.notificaion_save_logs_file_exists_content))
                            .setSmallIcon(R.drawable.main_new_icon)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.main_old_icon_without_borders))
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    Notification notification = builder.build();
                    notificationManager.notify(id_save_logfile_failed, notification);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        //check_logs=0;
        //Logs_printer.interrupt();
        //Logs_printer=null;
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
