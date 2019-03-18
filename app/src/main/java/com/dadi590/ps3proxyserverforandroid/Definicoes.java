package com.dadi590.ps3proxyserverforandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Switch;

public class Definicoes extends AppCompatActivity {

    Switch auto_start_switch;
    Switch auto_save_switch;
    Bundle extras;
    String auto_save;
    String auto_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definicoes);

        Toolbar toolbar = findViewById(R.id.toolbar_definicoes);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        auto_save_switch = findViewById(R.id.auto_save_switch);
        auto_start_switch = findViewById(R.id.auto_start_switch);

        extras = getIntent().getExtras();

        auto_save=extras.getString("auto_save");
        auto_start=extras.getString("auto_start");

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        auto_save_switch = findViewById(R.id.auto_save_switch);
        auto_start_switch = findViewById(R.id.auto_start_switch);

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(Definicoes.this, Principal.class);
        if (auto_start_switch.isChecked()) {
            System.out.println("DADi5");
            intent.putExtra("auto_start","true");
        } else {
            System.out.println("DADi6");
            intent.putExtra("auto_start","false");
        }
        if (auto_save_switch.isChecked()) {
            System.out.println("DADi3");
            intent.putExtra("auto_save","true");
        } else {
            System.out.println("DADi4");
            intent.putExtra("auto_save","false");
        }
        startActivity(intent);
        super.onDestroy();
    }
}