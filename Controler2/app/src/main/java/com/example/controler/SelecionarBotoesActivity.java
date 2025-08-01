package com.example.controler;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class SelecionarBotoesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BotaoAdapter botaoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectionarbotoesactivity);

        Log.d(TAG, "onCreate: SelecionarBotoesActivity started");

        recyclerView = findViewById(R.id.recyclerView);

        List<Botao> botoes = getBotoes();
        botaoAdapter = new BotaoAdapter(botoes, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(botaoAdapter);

        Log.d(TAG, "onCreate: SelecionarBotoesActivity setup complete");



        //------- Footer type shit , ignorar -------
        Button btnFooterHome = findViewById(R.id.btnFooterHome);
        Button btnFooterAdd = findViewById(R.id.btnFooterAdd);
        Button btnFooterConfig = findViewById(R.id.btnFooterConfig);
        btnFooterHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelecionarBotoesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnFooterAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelecionarBotoesActivity.this, AdicionarBotaoActivity.class);
                startActivity(intent);
            }
        });
        btnFooterConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelecionarBotoesActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    void onBotaoSelected(Botao botao) {
        Log.d(TAG, "onBotaoSelected: Botão selecionado - " + botao.getTexto() + ", estado - " + botao.isSelected());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> selectedButtons = new HashSet<>(preferences.getStringSet("selected_buttons", new HashSet<>()));

        if (botao.isSelected()) {
            selectedButtons.add(botao.getTexto());
        } else {
            selectedButtons.remove(botao.getTexto());
        }

        editor.putStringSet("selected_buttons", selectedButtons);
        editor.putBoolean("botao_" + botao.getTexto() + "_selecionado", botao.isSelected());
        editor.apply();

        Log.d(TAG, "onBotaoSelected: Botões selecionados atualizados - " + selectedButtons);
    }

    void onBotaoDeleted(Botao botao) {
        Log.d(TAG, "onBotaoDeleted: Botão deletado - " + botao.getTexto());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        // Remove the button from SharedPreferences
        editor.remove("botao_" + botao.getTexto());
        editor.remove("botao_" + botao.getTexto() + "_selecionado");
        editor.apply();

        // Remove the button from the saved buttons set
        Set<String> botoes = preferences.getStringSet("botoes", new HashSet<>());
        botoes.remove(botao.getTexto() + "|||" + botao.getAcao());
        editor.putStringSet("botoes", botoes);
        editor.apply();

        // Remove the button from the selected buttons set if it is selected
        Set<String> selectedButtons = new HashSet<>(preferences.getStringSet("selected_buttons", new HashSet<>()));
        selectedButtons.remove(botao.getTexto());
        editor.putStringSet("selected_buttons", selectedButtons);
        editor.apply();

        Log.d(TAG, "onBotaoDeleted: Botão removido de SharedPreferences e botões selecionados atualizados - " + selectedButtons);

        // Update the RecyclerView
        List<Botao> botoesAtualizados = getBotoes();
        botaoAdapter.updateBotoes(botoesAtualizados);
    }

    private List<Botao> getBotoes() {
        Log.d(TAG, "getBotoes: Recuperando botões");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selectedButtons = preferences.getStringSet("selected_buttons", new HashSet<>());

        Log.d(TAG, "getBotoes: Botões selecionados recuperados - " + selectedButtons);

        List<Botao> botoes = new ArrayList<>();
        Map<String, ?> allEntries = preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith("botao_") && entry.getValue() instanceof String) {
                String texto = entry.getKey().substring(6);
                String acao = (String) entry.getValue();
                boolean selecionado = preferences.getBoolean("botao_" + texto + "_selecionado", false);
                boolean tipo = preferences.getBoolean("botao_" + texto + "_tipo", false);
                botoes.add(new Botao(texto, selecionado, tipo, acao));
                Log.d(TAG, "getBotoes: Botão adicionado - " + texto + ", selecionado - " + selecionado);
            }
        }

        Log.d(TAG, "getBotoes: Total de botões recuperados - " + botoes.size());
        return botoes;
    }
}