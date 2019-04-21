package com.dadi590.ps3proxyserverforandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Instrucoes_iniciais extends AppCompatActivity {

    private Button botao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrucoes_iniciais);

        botao = findViewById(R.id.botao);
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Principal = new Intent(Instrucoes_iniciais.this, Principal.class);
                Principal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Principal.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                Principal.putExtra("extras_coisas_iniciais_vistas","true");
                startActivity(Principal);
                finish();
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
}
