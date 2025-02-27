package com.example.controler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private LinearLayout layoutBotoesDinamicos;
    private static final String TAG = "ControlerApp";
    private static final String PREFS_NAME = "BotaoPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutBotoesDinamicos = findViewById(R.id.layoutBotoes);

        // Adicionar botões pré-definidos ao iniciar o app pela primeira vez
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("first_run", true)) {
            addPredefinedButtons(preferences);
            preferences.edit().putBoolean("first_run", false).apply();
        }

        // Botão de configurações e adicionar botão
        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(this::openSettings);
        Button btnAdicionar = findViewById(R.id.btnAdd);
        Button btnSelection = findViewById(R.id.btnSelection);
        btnSelection.setOnClickListener(v -> {
            // Inicie a atividade SelecionarBotoesActivity
            Intent intent = new Intent(MainActivity.this, SelecionarBotoesActivity.class);
            startActivity(intent);
        });
        btnAdicionar.setOnClickListener(v -> abrirPaginaAdicaoBotao());
    }

    // Metodo para criar botões a ui de todos os botoes selecionados==True com tag de ação e tipo
    private Button adicionarBotaoDinamico(String texto, String acao, boolean tipo) {
        Button novoBotao = new Button(this);
        novoBotao.setText(texto);
        Log.e(TAG, "O botao tem tipo: " + tipo+"!!!!!!!!");
        Botao botaoTag = new Botao(texto, true, tipo, acao);
        novoBotao.setTag(botaoTag);
        novoBotao.setOnClickListener(v -> {
            Log.d(TAG, "Nome Botão clicado: " + botaoTag.getTexto());
            Log.d(TAG, "Ação Botão clicado: " + botaoTag.getAcao());
            Log.d(TAG, "Tipo Botão clicado: " + botaoTag.getTipo());
            executarAcao(botaoTag.getAcao(), botaoTag.getTipo());
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);
        layoutParams.setMargins(margin, margin, margin, margin);
        novoBotao.setLayoutParams(layoutParams);

        layoutBotoesDinamicos.addView(novoBotao);
        return novoBotao;
    }

    // Executa when the activity is about to become visible and interactable to the user
    @Override
    protected void onResume() {
        super.onResume();
        // Atualizar os valores das configurações ao retomar a main activity (mac, ip, porta)
        atualizarValores();
        layoutBotoesDinamicos.removeAllViews();
        // Mostrar os botões selecionados
        mostrarBotoesSelecionados();
    }

    // Função para recuperar os botões Selecionados
    private void mostrarBotoesSelecionados() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selectedButtons = preferences.getStringSet("selected_buttons", new HashSet<>());

        for (String buttonName : selectedButtons) {
            String acao = preferences.getString("botao_" + buttonName, "");
            boolean tipo = preferences.getBoolean("botao_" + buttonName + "_tipo", false);
            adicionarBotaoDinamico(buttonName, acao, tipo);
        }
    }

    // Função para iniciar a atividade ProgressActivity
    private void executarAcao(String ordem, Boolean tipo) {

        String perfilAtivo = obterPerfilAtivo();
        Log.d(TAG,"perfil ativo:"+ perfilAtivo);
        if (perfilAtivo != null) {
            String[] configValues = obterValoresConfiguracao(perfilAtivo);
            for (int i = 0; i < configValues.length; i++) {
                Log.d(TAG, "configValues[" + i + "]: " + configValues[i]);
            }

            String macAddress = configValues[0];
            String serverIp = configValues[1];
            String serverPort = configValues[2];

            Log.d(TAG, "Valores - MAC: " + macAddress + ", IP: " + serverIp + ", Porta: " + serverPort);


            if (macAddress.isEmpty() || serverIp.isEmpty() || serverPort.isEmpty()) {
                Toast.makeText(this, "Configure o MAC, IP e Porta nas configurações", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "executando ação...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ProgressActivity.class);
            intent.putExtra("macAddress", macAddress);
            intent.putExtra("serverIp", serverIp);
            intent.putExtra("serverPort", serverPort);
            intent.putExtra("ordem", ordem);
            intent.putExtra("tipo", tipo);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Nenhum perfil ativo encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    // Funções para receber os valores do perfil ativo (juntamente com obterPerfilAtivo() e obterValoresConfiguracao())
    private void atualizarValores() {
        String perfilAtivo = obterPerfilAtivo();
        if (perfilAtivo != null) {
            String[] configValues = obterValoresConfiguracao(perfilAtivo);
            Log.d(TAG, "Valores atualizados - MAC: " + configValues[0] + ", IP: " + configValues[1] + ", Porta: " + configValues[2]);
        } else {
            Log.d(TAG, "Nenhum perfil ativo encontrado.");
        }
    }

    private String obterPerfilAtivo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("perfil_ativo", null);
    }

    private String[] obterValoresConfiguracao(String perfil) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String profileData = preferences.getString("profile_" + perfil, "");
        preferences.getString(perfil + "_mac_address", "");
        preferences.getString(perfil + "_ip_address", "");
        preferences.getString(perfil + "_port", "");

        String macAddress = null;
        String serverIp = null;
        String serverPort = null;

        if (!profileData.isEmpty()) {
            String[] parts = profileData.split("\\|\\|\\|");
            if (parts.length == 3) {
                macAddress = parts[0];
                serverIp = parts[1];
                serverPort = parts[2];

                Toast.makeText(this, "Perfil " + perfil + " carregado", Toast.LENGTH_SHORT).show();
            }
        }
        return new String[]{macAddress, serverIp, serverPort};
    }
    ////////////////////////////////////////////////////////////////////////////////////////

    // Abre a página de configurações de perfil
    private void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // Abrir a pagina de criação de botão
    private void abrirPaginaAdicaoBotao() {
        Intent intent = new Intent(this, AdicionarBotaoActivity.class);
        startActivityForResult(intent, 1);
    }

    //Função para criar botões pré-definidos ao iniciar o app pela primeira vez
    private void addPredefinedButtons(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        String[] predefinedButtons = {"None", "LOL", "Valorant", "GTA5"};
        for (String buttonName : predefinedButtons) {
            Botao botao = new Botao(buttonName, false, true, "acao_" + buttonName.toLowerCase());
            editor.putString("botao_" + botao.getTexto(), botao.getAcao());
            editor.putBoolean("botao_" + botao.getTexto() + "_selecionado", botao.isSelected());
            editor.putBoolean("botao_" + botao.getTexto() + "_tipo", botao.getTipo());
        }
        editor.apply();
    }
}