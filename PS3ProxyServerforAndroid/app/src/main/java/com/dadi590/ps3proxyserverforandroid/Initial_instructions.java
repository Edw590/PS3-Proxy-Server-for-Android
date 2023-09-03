package com.dadi590.ps3proxyserverforandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Initial_instructions extends AppCompatActivity {

    private Button button;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_instructions);

        settings = getSharedPreferences("App_settings", Context.MODE_PRIVATE);
        editor = settings.edit();

        button = findViewById(R.id.button_real_time_logging);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(Initial_instructions.this, Main.class);
                Main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                editor.putString("extras_initial_stuff_seen", "true");
                editor.apply();
                startActivity(Main);
                finish();
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
}
