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
            // Crie um novo botão
            Button button = new Button(this);
            button.setText(buttonName);

            // Defina um OnClickListener para o botão
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Adicione a ação que deve ser executada quando o botão é clicado
                }
            });

            // Adicione o botão ao layout
            layoutBotoesDinamicos.addView(button);
        }
        // Recuperar os botões adicionados anteriormente
        recuperarBotoesSalvos();


        // Botão de configurações e adicionar botão
        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> openSettings());
        Button btnAdicionar = findViewById(R.id.btnAdd);
        Button btnSelection = findViewById(R.id.btnSelection);
        btnSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicie a atividade SelecionarBotoesActivity
                Intent intent = new Intent(MainActivity.this, SelecionarBotoesActivity.class);
                startActivity(intent);
            }
        });
        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirPaginaAdicaoBotao();
            }
        });
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
        novoBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Adicione um log ao clicar no botão
                Log.d(TAG, "Botão clicado: " + texto);

                // Logic for dynamic button action
                executarAcao(acao, "Mensagem do botão dinâmico");
            }
        });

        novoBotao.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Cria um AlertDialog perguntando se o usuário deseja excluir o botão
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Excluir botão")
                        .setMessage("Deseja eliminar este botão?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Se o usuário clicar em "Sim", remova o botão do layout
                                layoutBotoesDinamicos.removeView(novoBotao);
                                // Atualize as preferências compartilhadas
                                salvarBotoes();
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();

                // Retorna true para indicar que o evento de clique longo foi consumido
                return true;
            }
        });

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

        // Log the number of buttons in layoutBotoes
        Log.d(TAG, "Número de botões em layoutBotoes: " + layoutBotoesDinamicos.getChildCount());

        // Return the new button
        return novoBotao;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save the selected buttons when the activity is about to be paused
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        Set<String> selectedButtons = new HashSet<>();
        int totalButtons = layoutBotoesDinamicos.getChildCount();
        for (int i = 0; i < totalButtons; i++) {
            Button button = (Button) layoutBotoesDinamicos.getChildAt(i);
            String buttonText = button.getText().toString();
            selectedButtons.add(buttonText);
        }

        editor.putStringSet("selected_buttons", selectedButtons);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Atualizar os valores das configurações ao retomar a main activity (mac, ip, porta)
        atualizarValores();

        // Limpar todos os botões existentes
        layoutBotoesDinamicos.removeAllViews();

        // Recuperar os botões Selecionados no 'SelecionarBotoesActivity'
        mostrarBotoesSelecionados();
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void executarAcao(String ordem, String mensagemToast) {

        // Obter valores das configurações
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String macAddress = preferences.getString("mac_address", "");
        String serverIp = preferences.getString("ip_address", "");
        String serverPort = preferences.getString("port", "");

        // Validar se os valores foram preenchidos nas configurações
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
    }

    private void atualizarValores() {
        // Obter valores das configurações
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String macAddress = preferences.getString("mac_address", "");
        String serverIp = preferences.getString("ip_address", "");
        String serverPort = preferences.getString("port", "");

        // Use os valores atualizados conforme necessário
        Log.d(TAG, "Valores atualizados - MAC: " + macAddress + ", IP: " + serverIp + ", Porta: " + serverPort);
    }

    private void abrirPaginaAdicaoBotao() {
        Intent intent = new Intent(this, AdicionarBotaoActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Obtenha dados extras da intent
                String textoBotao = data.getStringExtra("texto");
                String acaoBotao = data.getStringExtra("acao");

                // Adicione o botão dinamicamente
                adicionarBotaoDinamico(textoBotao, acaoBotao);

                // Salvar os botões após adicionar um novo
                salvarBotoes();
            }
        }
    }

    private  void mostrarBotoesSelecionados() {
        // Recuperar e exibir os botões selecionados
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selectedButtons = preferences.getStringSet("selected_buttons", new HashSet<>());

        Log.d(TAG, "Botões selecionados: " + selectedButtons);

        // Verificar se selectedButtons está vazio
        if (!selectedButtons.isEmpty()) {
            for (String buttonName : selectedButtons) {
                // Crie um novo botão
                Button button = new Button(this);
                button.setText(buttonName);

                // Defina um OnClickListener para o botão
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Adicione um log ao clicar no botão
                        Log.d(TAG, "Botão clicado: " + buttonName);

                        // Obter valores das configurações
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        String macAddress = preferences.getString("mac_address", "");
                        String serverIp = preferences.getString("ip_address", "");
                        String serverPort = preferences.getString("port", "");

                        // Validar se os valores foram preenchidos nas configurações
                        if (macAddress.isEmpty() || serverIp.isEmpty() || serverPort.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Configure o MAC, IP e Porta nas configurações", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Iniciar a ProgressActivity
                        Intent intent = new Intent(MainActivity.this, ProgressActivity.class);
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("serverIp", serverIp);
                        intent.putExtra("serverPort", serverPort);
                        intent.putExtra("ordem", buttonName);
                        startActivity(intent);
                    }
                });

                // Adicione o botão ao layout
                layoutBotoesDinamicos.addView(button);
            }
        } else {
            Log.d(TAG, "Nenhum botão selecionado.");
        }
    }

    private void salvarBotoes() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); // Preferências compartilhadas privadas
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
            Button novoBotao = adicionarBotaoDinamico(textoBotao, acaoBotao);

            // Defina um OnClickListener para o botão
            novoBotao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Adicione um log ao clicar no botão
                    Log.d(TAG, "Botão clicado: " + textoBotao);

                    // Adicione a ação que deve ser executada quando o botão é clicado
                    executarAcao(acaoBotao, "Mensagem do botão dinâmico");
                }
            });
        }

        Log.d(TAG, "Número de botões recuperados: " + layoutBotoesDinamicos.getChildCount());
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Salvar os botões quando a atividade estiver prestes a ser parada
        salvarBotoes();
    }
}


