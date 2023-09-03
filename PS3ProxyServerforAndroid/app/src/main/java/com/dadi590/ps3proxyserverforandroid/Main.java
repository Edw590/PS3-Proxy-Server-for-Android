package com.dadi590.ps3proxyserverforandroid;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Scanner;

public class Main extends AppCompatActivity {

    private EditText port_obj;
    private TextView status_obj;
    private TextView data_ip_obj;
    private TextView data_port_obj;
    private TextView ip_obj;
    private TextView error_port_obj;
    private TextView error_ip_obj;
    private TextView manual_file_obj;
    private ImageButton button_start_stop_obj;
    private Button button_update_ip_obj;
    private Button button_real_time_logging_obj;
    private Button button_update_port_obj;
    private String ip;
    private String port;
    private String path_ps3_updatelist;
    private String manual_ps3_updatelist;
    private String disable_auto_updater;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Bundle extras;
    private Intent intent;
    private String start_server="false";
    private String ignore_update;
    private NotificationManager notificationManager;

    public int on=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        editor = settings.edit();
        extras = getIntent().getExtras();
        intent = getIntent();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (intent.hasExtra("extras_start_server")) {
            if (extras.getString("extras_start_server", "false").equals("true")) {
                start_server="true";
            }
        }

        if (settings.getString("extras_initial_stuff_seen", "false").equals("false")) {
            Intent Credits_iniciais = new Intent(Main.this, Initial_credits.class);
            Credits_iniciais.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Credits_iniciais.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(Credits_iniciais);
            finish();
        }

        if (settings.getString("settings_auto_save_port", "false").equals("true")) {
            port=settings.getString("settings_port", "8080");
        } else {
            port="8080";
        }
        if (intent.hasExtra("extras_update_port")) {
            port=extras.getString("extras_update_port", "8080");
        }

        if (settings.getString("settings_auto_save_ip", "false").equals("true")) {
            ip=settings.getString("settings_ip", Main.getLocalIpAddress());
        } else {
            ip=Main.getLocalIpAddress();
        }

        path_ps3_updatelist="NONE";
        if (!settings.getString("settings_path_ps3_updatelist", "NONE").equals("NONE")) {
            path_ps3_updatelist=settings.getString("settings_path_ps3_updatelist", "NONE");
        }

        manual_ps3_updatelist=settings.getString("settings_manual_ps3_updatelist", "false");
        disable_auto_updater=settings.getString("settings_disable_auto_updater", "false");
        ignore_update=settings.getString("settings_ignore_update", "false");

        ip_obj = findViewById(R.id.ip);
        port_obj = findViewById(R.id.port);
        status_obj = findViewById(R.id.status);
        data_ip_obj = findViewById(R.id.data_ip);
        data_port_obj = findViewById(R.id.data_port);
        button_start_stop_obj = findViewById(R.id.button_start_stop);
        button_update_ip_obj = findViewById(R.id.button_update_ip);
        button_real_time_logging_obj = findViewById(R.id.button_real_time_logging);
        error_port_obj = findViewById(R.id.error_port);
        error_ip_obj = findViewById(R.id.error_ip);
        manual_file_obj = findViewById(R.id.manual_file);
        button_update_port_obj = findViewById(R.id.button_update_port);

        //ip_telemovel_obj.setKeyListener(null);
        //ip_telemovel_obj.setText(Main.getLocalIpAddress());
        /*if (ligar_servidor=="true") {
            ip_telemovel_obj.setText(Main.getLocalIpAddress());
        } else {
            ip_telemovel_obj.setText(settings.getString("settings_ip", "N/A"));
        }*/
        ip_obj.setText(ip);
        port_obj.setText(port);

        //Detect if the service is active or not
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.dadi590.ps3proxyserverforandroid.Service".equals(service.service.getClassName())) {
                status_obj.setText(getString(R.string.status) + "ON");
                data_ip_obj.setText("IP: "+settings.getString("settings_ip", "N/A"));
                data_port_obj.setText(getString(R.string.port) + settings.getString("settings_port", "N/A"));
                button_start_stop_obj.setImageResource(R.drawable.pause);
                on=1;
            }
        }

        //Disabled. Now it's Real-time logs button above this text.
        /*manual_file_obj.setText(getString(R.string.manual_file) + "OFF");
        if (manual_ps3_updatelist.equals("true")) {
            manual_file_obj.setText(getString(R.string.manual_file) + "ON");
        } else {
            manual_file_obj.setText(getString(R.string.manual_file) + "OFF");
        }*/

        if (on==0) {
            status_obj.setText(getString(R.string.status) + "OFF");
            data_ip_obj.setText("IP: N/A");
            data_port_obj.setText(getString(R.string.port) + "N/A");
            button_start_stop_obj.setImageResource(R.drawable.play);
            editor.putString("settings_log_string_busy", "false");
            editor.apply();
        }

        if (!intent.hasExtra("extras_was_on")) {
            if (disable_auto_updater.contains("false")) {
                Date currentTime = Calendar.getInstance().getTime();
                //String dayOfTheWeek = (String) DateFormat.format("EEEE", currentTime); // Thursday
                String day          = (String) DateFormat.format("dd",   currentTime); // 20
                /*String monthString  = (String) DateFormat.format("MMM",  currentTime); // Jun
                String monthNumber  = (String) DateFormat.format("MM",   currentTime); // 06
                String year         = (String) DateFormat.format("yyyy", currentTime); // 2013
                String minute       = (String) DateFormat.format("mm",   currentTime); // 23
                String second       = (String) DateFormat.format("ss",   currentTime); // 23
                String hour       = (String) DateFormat.format("HH",   currentTime); // 23*/
                if (!day.equals(ignore_update)) {
                    new Main.Updates_Checker().execute();
                }
            }
        }

        if (intent.hasExtra("extras_wrong_ip")) {
            error_ip_obj.setText(getString(R.string.error_ip)+" ("+extras.getString("extras_wrong_ip", "ERROR")+")");
        }

        button_update_ip_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Main.getLocalIpAddress().equals(settings.getString("settings_ip", Main.getLocalIpAddress()))) {
                    ip_obj.setText(Main.getLocalIpAddress());
                    if (on==1) {
                        status_obj.setText(getString(R.string.status) + "OFF");
                        data_ip_obj.setText("IP: N/A");
                        data_port_obj.setText(getString(R.string.port) + "N/A");
                        button_start_stop_obj.setImageResource(R.drawable.play);
                        ip = ip_obj.getText().toString();
                        editor.putString("settings_ip", ip);
                        editor.putString("settings_port", port);
                        editor.apply();
                        Intent stopMyService = new Intent();
                        stopMyService.setClass(Main.this, Service.class);
                        stopService(stopMyService);
                        Intent Restarter_Main = new Intent();
                        Restarter_Main.setClass(Main.this, com.dadi590.ps3proxyserverforandroid.Restarter_Main.class);
                        Restarter_Main.putExtra("extras_start_server", "true");
                        Restarter_Main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Restarter_Main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(Restarter_Main);
                        System.exit(0);
                    }
                } else if (!Main.getLocalIpAddress().equals(ip_obj.getText())) {
                    ip_obj.setText(Main.getLocalIpAddress());
                }
            }
        });

        button_update_port_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!port_obj.getText().toString().equals(settings.getString("settings_port", "8080"))) {
                if (on==1) {
                    if (!port_obj.getText().toString().equals("")) {
                        if (Integer.parseInt(port_obj.getText().toString()) >= 1024 && Integer.parseInt(port_obj.getText().toString()) <= 65535) {
                            error_port_obj.setText("");
                            if (on == 1) {
                                status_obj.setText(getString(R.string.status) + "OFF");
                                data_ip_obj.setText("IP: N/A");
                                data_port_obj.setText(getString(R.string.port) + "N/A");
                                button_start_stop_obj.setImageResource(R.drawable.play);
                                port = port_obj.getText().toString();
                                editor.putString("settings_ip", ip);
                                editor.putString("settings_port", port);
                                editor.apply();
                                Intent stopMyService = new Intent();
                                stopMyService.setClass(Main.this, Service.class);
                                stopService(stopMyService);
                                Intent Restarter_Main = new Intent();
                                Restarter_Main.setClass(Main.this, Restarter_Main.class);
                                Restarter_Main.putExtra("extras_start_server", "true");
                                Restarter_Main.putExtra("extras_update_port", port);
                                Restarter_Main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Restarter_Main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(Restarter_Main);
                                System.exit(0);
                            }
                        } else {
                            error_port_obj.setText(getString(R.string.invalid_port));
                        }
                    } else {
                        error_port_obj.setText(getString(R.string.invalid_port));
                    }
                }
            }
            }
        });

        if (start_server=="true") {
            ip = ip_obj.getText().toString();
            port = port_obj.getText().toString();
            editor.putString("settings_ip", ip);
            editor.putString("settings_port", port);
            editor.apply();
            Intent startMyService = new Intent();
            startMyService.setClass(Main.this, Service.class);
            startService(startMyService);

            status_obj.setText(getString(R.string.status) + "ON");
            data_ip_obj.setText("IP: " + ip);
            data_port_obj.setText(getString(R.string.port) + port);
            button_start_stop_obj.setImageResource(R.drawable.pause);
            on = 1;
        }

        //Leave this here to let it save and do everything it has to do before restarting everything
        if (intent.hasExtra("extras_restart_server")) {
            if (extras.getString("extras_restart_server", "false").equals("true")) {
                if (on==1) {
                    ip = ip_obj.getText().toString();
                    port = port_obj.getText().toString();
                    status_obj.setText(getString(R.string.status) + "OFF");
                    data_ip_obj.setText("IP: N/A");
                    data_port_obj.setText(getString(R.string.port) + "N/A");
                    button_start_stop_obj.setImageResource(R.drawable.play);
                    Intent stopMyService = new Intent();
                    stopMyService.setClass(Main.this, Service.class);
                    stopService(stopMyService);
                    Intent Restarter_Main = new Intent();
                    Restarter_Main.setClass(Main.this, com.dadi590.ps3proxyserverforandroid.Restarter_Main.class);
                    Restarter_Main.putExtra("extras_start_server", "true");
                    Restarter_Main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Restarter_Main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(Restarter_Main);
                    System.exit(0);
                }
            }
        }

        button_real_time_logging_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Real_time_logging = new Intent();
                Real_time_logging.setClass(Main.this, Real_time_logging.class);
                Real_time_logging.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Real_time_logging.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(Real_time_logging);
            }
        });

        button_start_stop_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = ip_obj.getText().toString();
                //First on == 1 because otherwise it was always activated, because after on == 1 finishes, on = 1 and this would stop_server the server right after having been activated.
                if (on == 1) {
                    if (!port_obj.getText().toString().equals("")) {
                        if (Integer.parseInt(port_obj.getText().toString()) >= 1024 && Integer.parseInt(port_obj.getText().toString()) <= 65535) {
                            port = port_obj.getText().toString();
                        }
                    }
                    editor.putString("settings_ip", ip);
                    editor.putString("settings_port", port);
                    editor.apply();
                    status_obj.setText(getString(R.string.status) + "OFF");
                    data_ip_obj.setText("IP: N/A");
                    data_port_obj.setText(getString(R.string.port) + "N/A");
                    button_start_stop_obj.setImageResource(R.drawable.play);
                    Intent stopMyService = new Intent();
                    stopMyService.setClass(Main.this, Service.class);
                    stopService(stopMyService);
                    notificationManager.cancel(Service.id_restart_server_notification_request);
                    Intent Main_Restarter = new Intent();
                    Main_Restarter.setClass(Main.this, Restarter_Main.class);
                    Main_Restarter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Main_Restarter.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(Main_Restarter);
                    System.exit(0);
                }
                port = port_obj.getText().toString();
                if (!port.equals("")) {
                    if (Integer.parseInt(port) >= 1024 && Integer.parseInt(port) <= 65535) {
                        error_port_obj.setText("");
                        if (on == 0) {
                            Intent startMyService = new Intent();
                            startMyService.setClass(Main.this, Service.class);
                            startMyService.putExtra("extras_start_Main","true");
                            startService(startMyService);

                            error_ip_obj.setText("");
                            status_obj.setText(getString(R.string.status) + "ON");
                            data_ip_obj.setText("IP: " + ip);
                            data_port_obj.setText(getString(R.string.port) + port);
                            button_start_stop_obj.setImageResource(R.drawable.pause);
                            on = 1;
                            editor.putString("settings_ip", ip);
                            editor.putString("settings_port", port);
                            editor.apply();
                        }
                    } else {
                        error_port_obj.setText(getString(R.string.invalid_port));
                    }
                } else {
                    error_port_obj.setText(getString(R.string.invalid_port));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_normal,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!port_obj.getText().toString().equals("")) {
            if (Integer.parseInt(port_obj.getText().toString()) >= 1024 && Integer.parseInt(port_obj.getText().toString()) <= 65535) {
                port = port_obj.getText().toString();
            }
        }
        ip = ip_obj.getText().toString();
        editor.putString("settings_ip", ip);
        editor.putString("settings_port", port);
        editor.apply();
        if (item.getItemId()==R.id.instructions) {
            Intent Instructions = new Intent();
            Instructions.setClass(Main.this, Instructions.class);
            Instructions.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Instructions.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(Instructions);
        } else if (item.getItemId()==R.id.settings) {
            Intent Settings = new Intent();
            Settings.setClass(Main.this, Settings.class);
            Settings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Settings.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(Settings);
            finish();
        } else if (item.getItemId()==R.id.rules) {
            Intent Rules = new Intent(new Intent (Main.this, Rules.class));
            Rules.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Rules.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(Rules);
            finish();
        } else if (item.getItemId()==R.id.credits) {
            Intent Credits = new Intent(new Intent (Main.this, Credits.class));
            Credits.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Credits.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(Credits);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        editor.putString("settings_ip", ip);
        editor.putString("settings_port", port);
        editor.apply();
        super.onDestroy();
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (final SocketException ignored) {
        }

        return "localhost";
    }

    private class Updates_Checker extends AsyncTask<String, Void, Void> {

        private String updates_txt;
        private String[] updates_txt_list;
        private String[] updates_txt_list_lines_versions;
        private String download_link = "NONE";

        double version_name = Double.parseDouble(BuildConfig.VERSION_NAME);

        int proceed=1;

        @Override
        protected Void doInBackground(String... url) {

            try {
                updates_txt = new Scanner(new URL("https://raw.githubusercontent.com/Edw590/PS3-Proxy-Server-for-Android/master/UPDATES.txt").openStream(), "UTF-8").useDelimiter("\\A").next();

            } catch (Exception e) {
                try {
                    updates_txt = new Scanner(new URL("https://github.com/Edw590/PS3-Proxy-Server-for-Android/raw/master/UPDATES.txt").openStream(), "UTF-8").useDelimiter("\\A").next();
                } catch (Exception w) {
                    proceed=0;
                }
            }

            return null;
        }

        protected void onPostExecute(Void result) {

            int update_found=0;

            if (proceed==1) {
                updates_txt_list = updates_txt.split("\\r?\\n");
                for (int i = 0; i < updates_txt_list.length; i++) {
                    try {
                        updates_txt_list_lines_versions = updates_txt_list[i].split(" --> ");
                        if (updates_txt_list_lines_versions[1].substring(0, 4).equals("http")) {
                            if (Double.parseDouble(updates_txt_list_lines_versions[0].split(" ")[0]) > version_name) {
                                download_link = updates_txt_list_lines_versions[1];
                                update_found = 1;
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                updates_txt_list = updates_txt.split("\\r?\\n");
                for (int i = 0; i < updates_txt_list.length; i++) {
                    try {
                        updates_txt_list_lines_versions = updates_txt_list[i].split(" -> ");
                        if (updates_txt_list_lines_versions[0].substring(0, 1).equals(">") && updates_txt_list_lines_versions[0].contains("PSX-Place news XML URL")) {
                            editor.putString("settings_psx_place_xml_url", updates_txt_list_lines_versions[1]);
                            editor.apply();
                        }
                    } catch (Exception e) {
                    }
                }

                if (update_found == 1) {
                    Intent Update_Found = new Intent();
                    Update_Found.setClass(Main.this, Update_Found.class);
                    Update_Found.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Update_Found.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    Update_Found.putExtra("extras_text_updates_txt", updates_txt);
                    Update_Found.putExtra("extras_download_link", download_link);
                    startActivity(Update_Found);
                    finish();
                }
            }
        }
    }
}
