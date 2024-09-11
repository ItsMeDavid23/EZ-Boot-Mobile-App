package com.example.controler;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

        recyclerView = findViewById(R.id.recyclerView);

        List<Botao> botoes = getBotoes();
        botaoAdapter = new BotaoAdapter(botoes, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(botaoAdapter);

        Button btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(v -> finish());

    }

    void onBotaoSelected(Botao botao) {
        // Adicionar o botão à lista de botões selecionados
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selectedButtons = new HashSet<>(preferences.getStringSet("selected_buttons", new HashSet<>()));

        if (botao.isSelected()) {
            selectedButtons.add(botao.getTexto());
        } else {
            selectedButtons.remove(botao.getTexto());
        }

        preferences.edit().putStringSet("selected_buttons", selectedButtons).apply();

        Log.d(TAG, "Botão selecionado: " + botao.getTexto());
        Log.d(TAG, "Botões selecionados: " + selectedButtons);
    }

    private List<Botao> getBotoes() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selectedButtons = preferences.getStringSet("selected_buttons", new HashSet<>());

        Log.d(TAG, "Botões recuperados: " + selectedButtons);

        List<Botao> botoes = new ArrayList<>();
        botoes.add(new Botao("None", selectedButtons.contains("None"),true));
        botoes.add(new Botao("LOL", selectedButtons.contains("LOL"),true));
        botoes.add(new Botao("Valorant", selectedButtons.contains("Valorant"),true));
        botoes.add(new Botao("GTA5", selectedButtons.contains("GTA5"),true));


        // Recupere os botões criados pelo usuário de SharedPreferences e adicione-os à lista
        Map<String, ?> allEntries = preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith("botao_") && entry.getValue() instanceof String) {
                String texto = entry.getKey().substring(6); // Remove the "botao_" prefix
                String acao = (String) entry.getValue();
                botoes.add(new Botao(texto, selectedButtons.contains(acao), false));
            }
        }

        return botoes;
    }
}