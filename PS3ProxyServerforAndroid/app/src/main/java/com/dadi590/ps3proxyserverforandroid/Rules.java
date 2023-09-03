package com.dadi590.ps3proxyserverforandroid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import lib.folderpicker.FolderPicker;

public class Rules extends AppCompatActivity {

    public static final short MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3234_1;

    private Switch whats_new_psx_place_news_switch;
    private Switch whats_new_rss_feed_switch;
    private Switch ps_store_psx_place_news_switch;
    private Switch ps_store_rss_feed_switch;
    private Switch tv_video_services_psx_place_news_switch;
    private Switch tv_video_services_rss_feed_switch;
    private EditText custom_rules_urls_obj;
    private EditText whats_new_rss_feed_url_obj;
    private EditText ps_store_rss_feed_url_obj;
    private EditText tv_video_services_rss_feed_url_obj;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private String custom_rules_urls;
    private String whats_new_psx_place_news;
    private String whats_new_rss_feed;
    private String whats_new_rss_feed_url;
    private String ps_store_psx_place_news;
    private String ps_store_rss_feed;
    private String ps_store_rss_feed_url;
    private String tv_video_services_psx_place_news;
    private String tv_video_services_rss_feed;
    private String tv_video_services_rss_feed_url;
    private TextView chosen_file_obj;
    private Button button_choose_file_obj;
    private String custom_ps3_updatelist;
    private String manual_ps3_updatelist;
    private Switch official_ps3_updatelist_switch;
    private Switch manual_ps3_updatelist_switch;
    private String path_ps3_updatelist;
    private Button button_choose_file_custom_urls;

    Intent Main;

    Intent intent = new Intent()
            .setType("resource/folder")
            .setAction(Intent.ACTION_VIEW);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        Main = new Intent(Rules.this, Main.class);
        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        editor = settings.edit();

        Toolbar toolbar = findViewById(R.id.toolbar_rules);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        custom_rules_urls_obj = findViewById(R.id.custom_rules_urls);
        whats_new_psx_place_news_switch = findViewById(R.id.whats_new_psx_place_news_switch);
        whats_new_rss_feed_switch = findViewById(R.id.whats_new_rss_feed_switch);
        whats_new_rss_feed_url_obj = findViewById(R.id.whats_new_rss_feed_url);
        ps_store_psx_place_news_switch = findViewById(R.id.ps_store_psx_place_news_switch);
        ps_store_rss_feed_switch = findViewById(R.id.ps_store_rss_feed_switch);
        ps_store_rss_feed_url_obj = findViewById(R.id.ps_store_rss_feed_url);
        tv_video_services_psx_place_news_switch = findViewById(R.id.tv_video_services_psx_place_news_switch);
        tv_video_services_rss_feed_switch = findViewById(R.id.tv_video_services_rss_feed_switch);
        tv_video_services_rss_feed_url_obj = findViewById(R.id.tv_video_services_rss_feed_url);
        official_ps3_updatelist_switch = findViewById(R.id.official_ps3_updatelist_switch);
        manual_ps3_updatelist_switch = findViewById(R.id.manual_ps3_updatelist_switch);
        button_choose_file_obj = findViewById(R.id.button_choose_file);
        chosen_file_obj = findViewById(R.id.chosen_file);
        button_choose_file_custom_urls = findViewById(R.id.button_choose_file_custom_urls);

        custom_rules_urls=settings.getString("settings_custom_rules_urls","");
        whats_new_psx_place_news=settings.getString("settings_whats_new_psx_place_news","false");
        whats_new_rss_feed=settings.getString("settings_whats_new_rss_feed","false");
        whats_new_rss_feed_url=settings.getString("settings_whats_new_rss_feed_url","");
        ps_store_psx_place_news=settings.getString("settings_ps_store_psx_place_news","false");
        ps_store_rss_feed=settings.getString("settings_ps_store_rss_feed","false");
        ps_store_rss_feed_url=settings.getString("settings_ps_store_rss_feed_url","");
        tv_video_services_psx_place_news=settings.getString("settings_tv_video_services_psx_place_news","false");
        tv_video_services_rss_feed=settings.getString("settings_tv_video_services_rss_feed","false");
        tv_video_services_rss_feed_url=settings.getString("settings_tv_video_services_rss_feed_url","");
        custom_ps3_updatelist=settings.getString("settings_custom_ps3_updatelist","true");
        manual_ps3_updatelist=settings.getString("settings_manual_ps3_updatelist","false");
        path_ps3_updatelist=settings.getString("settings_path_ps3_updatelist","NONE");

        chosen_file_obj.setText(getString(R.string.chosen_file) + path_ps3_updatelist);

        custom_rules_urls_obj.setText(custom_rules_urls);
        whats_new_rss_feed_url_obj.setText(whats_new_rss_feed_url);
        ps_store_rss_feed_url_obj.setText(ps_store_rss_feed_url);
        tv_video_services_rss_feed_url_obj.setText(tv_video_services_rss_feed_url);

        if (whats_new_psx_place_news.equals("true")) {
            whats_new_psx_place_news_switch.setChecked(true);
        } else {
            whats_new_psx_place_news_switch.setChecked(false);
        }
        if (ps_store_psx_place_news.equals("true")) {
            ps_store_psx_place_news_switch.setChecked(true);
        } else {
            ps_store_psx_place_news_switch.setChecked(false);
        }
        if (tv_video_services_psx_place_news.equals("true")) {
            tv_video_services_psx_place_news_switch.setChecked(true);
        } else {
            tv_video_services_psx_place_news_switch.setChecked(false);
        }
        if (whats_new_rss_feed.equals("true")) {
            whats_new_rss_feed_switch.setChecked(true);
        } else {
            whats_new_rss_feed_switch.setChecked(false);
        }
        if (ps_store_rss_feed.equals("true")) {
            ps_store_rss_feed_switch.setChecked(true);
        } else {
            ps_store_rss_feed_switch.setChecked(false);
        }
        if (tv_video_services_rss_feed.equals("true")) {
            tv_video_services_rss_feed_switch.setChecked(true);
        } else {
            tv_video_services_rss_feed_switch.setChecked(false);
        }
        if (manual_ps3_updatelist.equals("true")) {
            manual_ps3_updatelist_switch.setChecked(true);
        } else {
            manual_ps3_updatelist_switch.setChecked(false);
        }
        if (custom_ps3_updatelist.equals("true")) {
            official_ps3_updatelist_switch.setChecked(false);
        } else {
            official_ps3_updatelist_switch.setChecked(true);
            manual_ps3_updatelist_switch.setEnabled(false);
        }

        official_ps3_updatelist_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) {
                    manual_ps3_updatelist_switch.setEnabled(false);
                } else {
                    manual_ps3_updatelist_switch.setEnabled(true);
                }
            }
        });

        whats_new_psx_place_news_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) {
                    whats_new_rss_feed_switch.setChecked(false);
                }
            }
        });
        ps_store_psx_place_news_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) {
                    ps_store_rss_feed_switch.setChecked(false);
                }
            }
        });
        tv_video_services_psx_place_news_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) {
                    tv_video_services_rss_feed_switch.setChecked(false);
                }
            }
        });
        whats_new_rss_feed_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) {
                    whats_new_psx_place_news_switch.setChecked(false);
                }
            }
        });
        ps_store_rss_feed_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) {
                    ps_store_psx_place_news_switch.setChecked(false);
                }
            }
        });
        tv_video_services_rss_feed_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) {
                    tv_video_services_psx_place_news_switch.setChecked(false);
                }
            }
        });

        button_choose_file_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(Rules.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(Rules.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    } else {
                        Intent intent = new Intent(Rules.this, FolderPicker.class);
                        intent.putExtra("title", getString(R.string.choose_file));
                        intent.putExtra("pickFiles", true);
                        startActivityForResult(intent, 3234_3);
                    }
                } else {
                    Intent intent = new Intent(Rules.this, FolderPicker.class);
                    intent.putExtra("title", getString(R.string.choose_file));
                    intent.putExtra("pickFiles", true);
                    startActivityForResult(intent, 3234_3);
                }
            }
        });
        button_choose_file_custom_urls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (ContextCompat.checkSelfPermission(Rules.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(Rules.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    if (ContextCompat.checkSelfPermission(Rules.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                    } else {
                        Intent intent = new Intent(Rules.this, FolderPicker.class);
                        intent.putExtra("title", getString(R.string.choose_file));
                        intent.putExtra("pickFiles", true);
                        startActivityForResult(intent, 3234_4);
                    }
                } else {
                    Intent intent = new Intent(Rules.this, FolderPicker.class);
                    intent.putExtra("title", getString(R.string.choose_file));
                    intent.putExtra("pickFiles", true);
                    startActivityForResult(intent, 3234_4);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 3234_3 && resultCode == RESULT_OK) {
            path_ps3_updatelist = intent.getExtras().getString("data");
            chosen_file_obj.setText(getString(R.string.chosen_file) + path_ps3_updatelist);
        }
        if (requestCode == 3234_4 && resultCode == RESULT_OK) {
            custom_rules_urls_obj.setText(custom_rules_urls_obj.getText().toString()+" --> "+intent.getExtras().getString("data"));
        }
    }

    @Override
    public void onDestroy() {
        Main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        if (!manual_ps3_updatelist.equals(String.valueOf(manual_ps3_updatelist_switch.isChecked()))) {
            editor.putString("settings_manual_ps3_updatelist",String.valueOf(manual_ps3_updatelist_switch.isChecked()));
            if (!manual_ps3_updatelist_switch.isChecked()) {
                editor.putString("settings_path_ps3_updatelist","NONE");
            }
            Main.putExtra("extras_restart_server", "true");
        }
        if (!path_ps3_updatelist.equals(settings.getString("settings_path_ps3_updatelist","NONE"))) {
            editor.putString("settings_path_ps3_updatelist",path_ps3_updatelist);
            Main.putExtra("extras_restart_server", "true");
        }
        if (!custom_ps3_updatelist.equals(String.valueOf(!official_ps3_updatelist_switch.isChecked()))) {
            editor.putString("settings_custom_ps3_updatelist",String.valueOf(!official_ps3_updatelist_switch.isChecked()));
            Main.putExtra("extras_restart_server", "true");
        }
        if (!whats_new_psx_place_news.equals(String.valueOf(whats_new_psx_place_news_switch.isChecked()))) {
            editor.putString("settings_whats_new_psx_place_news",String.valueOf(whats_new_psx_place_news_switch.isChecked()));
            Main.putExtra("extras_restart_server", "true");
        }
        if (!ps_store_psx_place_news.equals(String.valueOf(ps_store_psx_place_news_switch.isChecked()))) {
            editor.putString("settings_ps_store_psx_place_news",String.valueOf(ps_store_psx_place_news_switch.isChecked()));
            Main.putExtra("extras_restart_server", "true");
        }
        if (!tv_video_services_psx_place_news.equals(String.valueOf(tv_video_services_psx_place_news_switch.isChecked()))) {
            editor.putString("settings_tv_video_services_psx_place_news",String.valueOf(tv_video_services_psx_place_news_switch.isChecked()));
            Main.putExtra("extras_restart_server", "true");
        }
        if (!whats_new_rss_feed.equals(String.valueOf(whats_new_rss_feed_switch.isChecked()))) {
            editor.putString("settings_whats_new_rss_feed",String.valueOf(whats_new_rss_feed_switch.isChecked()));
            Main.putExtra("extras_restart_server", "true");
        }
        if (!ps_store_rss_feed.equals(String.valueOf(ps_store_rss_feed_switch.isChecked()))) {
            editor.putString("settings_ps_store_rss_feed",String.valueOf(ps_store_rss_feed_switch.isChecked()));
            Main.putExtra("extras_restart_server", "true");
        }
        if (!tv_video_services_rss_feed.equals(String.valueOf(tv_video_services_rss_feed_switch.isChecked()))) {
            editor.putString("settings_tv_video_services_rss_feed",String.valueOf(tv_video_services_rss_feed_switch.isChecked()));
            Main.putExtra("extras_restart_server", "true");
        }
        if (!whats_new_rss_feed_url_obj.getText().toString().equals(whats_new_rss_feed_url)) {
            editor.putString("settings_whats_new_rss_feed_url",whats_new_rss_feed_url_obj.getText().toString());
            Main.putExtra("extras_restart_server", "true");
        }
        if (!ps_store_rss_feed_url_obj.getText().toString().equals(ps_store_rss_feed_url)) {
            editor.putString("settings_ps_store_rss_feed_url",ps_store_rss_feed_url_obj.getText().toString());
            Main.putExtra("extras_restart_server", "true");
        }
        if (!tv_video_services_rss_feed_url_obj.getText().toString().equals(tv_video_services_rss_feed_url)) {
            editor.putString("settings_tv_video_services_rss_feed_url",tv_video_services_rss_feed_url_obj.getText().toString());
            Main.putExtra("extras_restart_server", "true");
        }
        if (!custom_rules_urls_obj.getText().toString().equals(custom_rules_urls)) {
            editor.putString("settings_custom_rules_urls",custom_rules_urls_obj.getText().toString());
            Main.putExtra("extras_restart_server", "true");
        }
        Main.putExtra("extras_was_on","true");
        editor.apply();
        startActivity(Main);
        super.onDestroy();
    }
}
