// AdicionarBotaoActivity.java
package com.example.controler;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AdicionarBotaoActivity extends AppCompatActivity {

    private EditText editTextTexto;
    private EditText editTextAcao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_botao);

        editTextTexto = findViewById(R.id.editTextTexto);
        editTextAcao = findViewById(R.id.editTextAcao);

        Button btnAdicionar = findViewById(R.id.btnAdicionar);
        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtenha os dados do novo botão (texto e ação) e envie de volta para MainActivity
                String texto = editTextTexto.getText().toString();
                String acao = editTextAcao.getText().toString();

                // Armazene o botão criado pelo usuário em SharedPreferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AdicionarBotaoActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("botao_" + texto, acao + false);
                editor.apply();

                finish();
            }
        });
    }
}
