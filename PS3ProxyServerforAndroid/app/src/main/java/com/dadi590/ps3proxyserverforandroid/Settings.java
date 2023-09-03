package com.dadi590.ps3proxyserverforandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.net.URL;
import java.util.Scanner;

public class Settings extends AppCompatActivity {

    private Switch auto_start_switch;
    private Switch auto_save_port_switch;
    private Switch auto_save_ip_switch;
    private Switch disable_auto_updater_switch;
    private Switch see_requests_switch;
    private String auto_save_port;
    private String auto_save_ip;
    private String auto_start;
    private String disable_auto_updater;
    private String see_requests;
    private Button button_manually_check_updates_obj;
    private TextView update_not_found_obj;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private int manually_check_updates=0;

    Intent Main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Main = new Intent(Settings.this, Main.class);
        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        editor = settings.edit();

        Toolbar toolbar = findViewById(R.id.toolbar_instructions);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        auto_save_port_switch = findViewById(R.id.auto_save_port_switch);
        auto_save_ip_switch = findViewById(R.id.auto_save_ip_switch);
        auto_start_switch = findViewById(R.id.auto_start_switch);
        disable_auto_updater_switch = findViewById(R.id.disable_auto_updater_switch);
        button_manually_check_updates_obj = findViewById(R.id.button_manually_check_updates);
        update_not_found_obj = findViewById(R.id.update_not_found);
        see_requests_switch = findViewById(R.id.see_requests_switch);

        auto_save_port=settings.getString("settings_auto_save_port","false");
        auto_save_ip=settings.getString("settings_auto_save_ip","false");
        auto_start=settings.getString("settings_auto_start_boot","false");
        disable_auto_updater=settings.getString("settings_disable_auto_updater","false");
        see_requests=settings.getString("settings_see_requests","false");

        if (auto_save_port.equals("true")) {
            auto_save_port_switch.setChecked(true);
        } else {
            auto_save_port_switch.setChecked(false);
        }
        if (auto_save_ip.equals("true")) {
            auto_save_ip_switch.setChecked(true);
        } else {
            auto_save_ip_switch.setChecked(false);
        }
        if (auto_start.equals("true")) {
            auto_start_switch.setChecked(true);
        } else {
            auto_start_switch.setChecked(false);
        }
        if (disable_auto_updater.equals("true")) {
            disable_auto_updater_switch.setChecked(true);
        } else {
            disable_auto_updater_switch.setChecked(false);
        }
        if (see_requests.equals("true")) {
            see_requests_switch.setChecked(true);
        } else {
            see_requests_switch.setChecked(false);
        }

        button_manually_check_updates_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Settings.Updates_Checker().execute();
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
        Main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        if (!auto_start.equals(String.valueOf(auto_start_switch.isChecked()))) {
            editor.putString("settings_auto_start_boot",String.valueOf(auto_start_switch.isChecked()));
        }
        if (!auto_save_port.equals(String.valueOf(auto_save_port_switch.isChecked()))) {
            editor.putString("settings_auto_save_port",String.valueOf(auto_save_port_switch.isChecked()));
        }
        if (!auto_save_ip.equals(String.valueOf(auto_save_ip_switch.isChecked()))) {
            editor.putString("settings_auto_save_ip",String.valueOf(auto_save_ip_switch.isChecked()));
        }
        if (!disable_auto_updater.equals(String.valueOf(disable_auto_updater_switch.isChecked()))) {
            editor.putString("settings_disable_auto_updater",String.valueOf(disable_auto_updater_switch.isChecked()));
        }
        if (!see_requests.equals(String.valueOf(see_requests_switch.isChecked()))) {
            editor.putString("settings_see_requests",String.valueOf(see_requests_switch.isChecked()));
        }
        Main.putExtra("extras_was_on","true");
        editor.apply();
        if (manually_check_updates==0) {
            startActivity(Main);
        }
        super.onDestroy();
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
                    Update_Found.setClass(Settings.this, Update_Found.class);
                    Update_Found.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Update_Found.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    Update_Found.putExtra("extras_text_updates_txt", updates_txt);
                    Update_Found.putExtra("extras_download_link", download_link);
                    startActivity(Update_Found);
                    manually_check_updates=1;
                    finish();
                } else {
                    update_not_found_obj.setText(getString(R.string.update_not_found));
                }
            } else {
                update_not_found_obj.setText(getString(R.string.update_not_found_error));
            }
        }
    }
}
