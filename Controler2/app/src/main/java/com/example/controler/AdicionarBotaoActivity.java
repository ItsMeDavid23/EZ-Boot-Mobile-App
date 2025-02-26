package com.example.controler;

import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AdicionarBotaoActivity extends AppCompatActivity {
    private EditText editTextTexto;
    private EditText editTextAcao;
    private Button btnAdicionar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_botao);

        Log.d(TAG, "onCreate: AdicionarBotaoActivity started");

        editTextTexto = findViewById(R.id.editTextTexto);
        editTextAcao = findViewById(R.id.editTextAcao);
        btnAdicionar = findViewById(R.id.btnAdicionar);

        btnAdicionar.setOnClickListener(v -> {
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
    }
}