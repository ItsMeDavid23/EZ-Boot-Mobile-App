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

        Log.d(TAG, "onCreate: SelecionarBotoesActivity started");

        recyclerView = findViewById(R.id.recyclerView);

        List<Botao> botoes = getBotoes();
        botaoAdapter = new BotaoAdapter(botoes, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(botaoAdapter);

        Button btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(v -> {
            Log.d(TAG, "onClick: btnVoltar clicked");
            finish();
        });

        Log.d(TAG, "onCreate: SelecionarBotoesActivity setup complete");
    }

    void onBotaoSelected(Botao botao) {
        Log.d(TAG, "onBotaoSelected: Botão selecionado - " + botao.getTexto());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selectedButtons = new HashSet<>(preferences.getStringSet("selected_buttons", new HashSet<>()));

        if (botao.isSelected()) {
            selectedButtons.add(botao.getTexto());
        } else {
            selectedButtons.remove(botao.getTexto());
        }

        preferences.edit().putStringSet("selected_buttons", selectedButtons).apply();

        Log.d(TAG, "onBotaoSelected: Botões selecionados atualizados - " + selectedButtons);
    }

    void onBotaoDeleted(Botao botao) {
        Log.d(TAG, "onBotaoDeleted: Botão deletado - " + botao.getTexto());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        // Remove the button from SharedPreferences
        editor.remove("botao_" + botao.getTexto());
        editor.apply();

        // Remove the button from the selected buttons set if it is selected
        Set<String> selectedButtons = new HashSet<>(preferences.getStringSet("selected_buttons", new HashSet<>()));
        selectedButtons.remove(botao.getTexto());
        editor.putStringSet("selected_buttons", selectedButtons);
        editor.apply();

        Log.d(TAG, "onBotaoDeleted: Botão removido de SharedPreferences e botões selecionados atualizados - " + selectedButtons);

        // Update the RecyclerView
        List<Botao> botoes = getBotoes();
        botaoAdapter.updateBotoes(botoes);
    }

    private List<Botao> getBotoes() {
        Log.d(TAG, "getBotoes: Recuperando botões");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selectedButtons = preferences.getStringSet("selected_buttons", new HashSet<>());

        Log.d(TAG, "getBotoes: Botões selecionados recuperados - " + selectedButtons);

        List<Botao> botoes = new ArrayList<>();
        botoes.add(new Botao("None", selectedButtons.contains("None"), true));
        botoes.add(new Botao("LOL", selectedButtons.contains("LOL"), true));
        botoes.add(new Botao("Valorant", selectedButtons.contains("Valorant"), true));
        botoes.add(new Botao("GTA5", selectedButtons.contains("GTA5"), true));

        Map<String, ?> allEntries = preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith("botao_") && entry.getValue() instanceof String) {
                String texto = entry.getKey().substring(6);
                String acao = (String) entry.getValue();
                botoes.add(new Botao(texto, selectedButtons.contains(texto), false));
                Log.d(TAG, "getBotoes: Botão criado pelo usuário adicionado - " + texto);
            }
        }

        Log.d(TAG, "getBotoes: Total de botões recuperados - " + botoes.size());
        return botoes;
    }
}