package com.example.controler;

import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdicionarBotaoListener {
    private LinearLayout layoutBotoesDinamicos;
    private static final String TAG = "ControlerApp";
    private static final String PREFS_NAME = "BotaoPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutBotoesDinamicos = findViewById(R.id.layoutBotoes);

        // Atualizar os valores das configurações ao retomar a main activity (mac, ip, porta)
        atualizarValores();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selectedButtons = preferences.getStringSet("selected_buttons", new HashSet<>());

        for (String buttonName : selectedButtons) {
            adicionarBotaoDinamico(buttonName, null);
        }
        // Recuperar os botões adicionados anteriormente
        recuperarBotoesSalvos();

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

    @Override
    public void adicionarBotao(String texto, String acao) {
        // Lógica para adicionar botão à sua atividade
        adicionarBotaoDinamico(texto, acao);
        Toast.makeText(this, "Botão adicionado: " + texto, Toast.LENGTH_SHORT).show();
    }

    private Button adicionarBotaoDinamico(String texto, String acao) {
        // Create a button programmatically and add it to layoutBotoes
        Button novoBotao = new Button(this);
        novoBotao.setText(texto);
        novoBotao.setTag(acao);
        novoBotao.setOnClickListener(v -> {
            // Adicione um log ao clicar no botão
            Log.d(TAG, "Nome Botão clicado: " + texto);
            Log.d(TAG, "Ação Botão clicado: " + acao);

            // Logic for dynamic button action
            executarAcao(acao, "Mensagem do botão dinâmico");
        });

        //novoBotao.setOnLongClickListener(v -> {
        //    // Cria um AlertDialog perguntando se o usuário deseja excluir o botão
        //    new AlertDialog.Builder(MainActivity.this)
        //            .setTitle("Excluir botão")
        //            .setMessage("Deseja eliminar este botão?")
        //            .setPositiveButton("Sim", (dialog, which) -> {
        //                // Se o usuário clicar em "Sim", remova o botão do layout
        //                layoutBotoesDinamicos.removeView(novoBotao);
        //                // Atualize as preferências compartilhadas
        //                salvarBotoes();
        //            })
        //            .setNegativeButton("Não", null)
        //            .show();
        //
        //    // Retorna true para indicar que o evento de clique longo foi consumido
        //    return true;
        //});
        //

        // Set layout properties for the new button
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,  // Change this line
        LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;  // Add this line
        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin); // replace 'default_margin' with the desired margin value
        layoutParams.setMargins(margin, margin, margin, margin);
        novoBotao.setLayoutParams(layoutParams);

        // Add the new button to layoutBotoes
        layoutBotoesDinamicos.addView(novoBotao);

        // Return the new button
        return novoBotao;
    }

    @Override
    protected void onPause() {
        super.onPause();
        salvarBotoes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarValores();
        layoutBotoesDinamicos.removeAllViews();
        mostrarBotoesSelecionados();
    }

    private void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void executarAcao(String ordem, String mensagemToast) {
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

            Toast.makeText(this, mensagemToast, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ProgressActivity.class);
            intent.putExtra("macAddress", macAddress);
            intent.putExtra("serverIp", serverIp);
            intent.putExtra("serverPort", serverPort);
            intent.putExtra("ordem", ordem);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Nenhum perfil ativo encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

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

    private void abrirPaginaAdicaoBotao() {
        Intent intent = new Intent(this, AdicionarBotaoActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String textoBotao = data.getStringExtra("texto");
            String acaoBotao = data.getStringExtra("acao");
            adicionarBotaoDinamico(textoBotao, acaoBotao);
            salvarBotoes();
        }
    }

    private void mostrarBotoesSelecionados() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selectedButtons = preferences.getStringSet("selected_buttons", new HashSet<>());

        Log.d(TAG, "Botões selecionados: " + selectedButtons);

        if (!selectedButtons.isEmpty()) {
            for (String buttonName : selectedButtons) {
                adicionarBotaoDinamico(buttonName, buttonName);
            }
        } else {
            Log.d(TAG, "Nenhum botão selecionado.");
        }
    }

    private void salvarBotoes() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();

        int totalBotoes = layoutBotoesDinamicos.getChildCount();
        editor.putInt("totalBotoes", totalBotoes);

        Set<String> botoes = new HashSet<>();
        for (int i = 0; i < totalBotoes; i++) {
            Button botao = (Button) layoutBotoesDinamicos.getChildAt(i);
            String textoBotao = botao.getText().toString();
            String acaoBotao = (String) botao.getTag();
            botoes.add(textoBotao + "|||" + acaoBotao);
        }
        editor.putStringSet("botoes", botoes);
        editor.apply();
    }

    private void recuperarBotoesSalvos() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> botoes = prefs.getStringSet("botoes", new HashSet<>());

        for (String botao : botoes) {
            String[] partes = botao.split("\\|\\|\\|");
            if (partes.length < 2) {
                Log.e(TAG, "Botão malformado: " + botao);
                continue;
            }
            String textoBotao = partes[0];
            String acaoBotao = partes[1];
            Log.d(TAG, "Recuperando botão: " + textoBotao + " com ação: " + acaoBotao + "!!!!!!!");
            adicionarBotaoDinamico(textoBotao, acaoBotao);
        }

        Log.d(TAG, "Número de botões recuperados: " + layoutBotoesDinamicos.getChildCount());
    }

    @Override
    protected void onStop() {
        super.onStop();
        salvarBotoes();
    }
}