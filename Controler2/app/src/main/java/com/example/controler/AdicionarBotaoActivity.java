package com.example.controler;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AdicionarBotaoActivity extends AppCompatActivity {
    private EditText editTextTexto;
    private EditText editTextAcao;
    private Button btnCriar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_botao);

        Log.d(TAG, "onCreate: AdicionarBotaoActivity started");

        editTextTexto = findViewById(R.id.editTextTexto);
        editTextAcao = findViewById(R.id.editTextAcao);
        btnCriar = findViewById(R.id.btnAdicionar);

        btnCriar.setOnClickListener(v -> {
            String textoBotao = editTextTexto.getText().toString();
            String acaoBotao = editTextAcao.getText().toString();

            Log.d(TAG, "onClick: btnAdicionar clicked - Texto: " + textoBotao + ", Ação: " + acaoBotao);

            if (!textoBotao.isEmpty() && !acaoBotao.isEmpty()) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("botao_" + textoBotao, acaoBotao);
                editor.apply();
                Log.d(TAG, "onClick: Botão salvo - Texto: " + textoBotao + ", Ação: " + acaoBotao);
                finish();
            } else {
                Log.d(TAG, "onClick: Texto ou ação do botão está vazio");
            }
        });

        Log.d(TAG, "onCreate: AdicionarBotaoActivity setup complete");


        //------- Footer type shit , ignorar -------
        Button btnFooterHome = findViewById(R.id.btnFooterHome);
        Button btnFooterSelectButtons = findViewById(R.id.btnFooterSelectButtons);
        Button btnFooterConfig = findViewById(R.id.btnFooterConfig);
        btnFooterHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdicionarBotaoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnFooterSelectButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdicionarBotaoActivity.this, SelecionarBotoesActivity.class);
                startActivity(intent);
            }
        });
        btnFooterConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdicionarBotaoActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}