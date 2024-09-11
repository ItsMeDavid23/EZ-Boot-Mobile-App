package com.example.controler;// SettingsActivity.java

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controler.R;

public class SettingsActivity extends AppCompatActivity {
    private EditText editTextMac, editTextIp, editTextPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Adicionando título
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText("Configurar App");

        editTextMac = findViewById(R.id.editTextMac);
        editTextIp = findViewById(R.id.editTextIp);
        editTextPort = findViewById(R.id.editTextPort);

        // Adicionando TextWatcher para converter para letras maiúsculas
        editTextMac.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nada a fazer antes da mudança de texto
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nada a fazer durante a mudança de texto
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Converter para letras maiúsculas
                String text = editable.toString().toUpperCase();
                if (!text.equals(editable.toString())) {
                    editTextMac.setText(text);
                    editTextMac.setSelection(editTextMac.length());
                }
            }
        });

        // Adicionando TextWatcher para permitir apenas números no campo do IP
        editTextIp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nada a fazer antes da mudança de texto
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nada a fazer durante a mudança de texto
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Remover caracteres não numéricos
                String text = editable.toString().replaceAll("[^0-9.]", "");
                if (!text.equals(editable.toString())) {
                    editTextIp.setText(text);
                    editTextIp.setSelection(editTextIp.length());
                }
            }
        });

        // Adicionando TextWatcher para permitir apenas números no campo da porta
        editTextPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nada a fazer antes da mudança de texto
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nada a fazer durante a mudança de texto
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Remover caracteres não numéricos
                String text = editable.toString().replaceAll("[^0-9]", "");
                if (!text.equals(editable.toString())) {
                    editTextPort.setText(text);
                    editTextPort.setSelection(editTextPort.length());
                }
            }
        });

        // Botão para salvar as configurações
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveSettings());

        // Botão para voltar à atividade principal
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Carregar as configurações salvas
        loadSavedSettings();
    }

    private void saveSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("mac_address", editTextMac.getText().toString());

        // Validar o endereço IP
        String ipAddress = editTextIp.getText().toString();
        if (isValidIpAddress(ipAddress)) {
            editor.putString("ip_address", ipAddress);
        } else {
            // Exibir mensagem de erro se o endereço IP for inválido
            Toast.makeText(this, "Endereço IP inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar a porta
        String port = editTextPort.getText().toString();
        if (isValidPort(port)) {
            editor.putString("port", port);
        } else {
            // Exibir mensagem de erro se a porta for inválida
            Toast.makeText(this, "Porta inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        editor.apply();

        // Exibir Toast informando que as configurações foram salvas com sucesso
        Toast.makeText(this, "Configurações salvas com sucesso", Toast.LENGTH_SHORT).show();
    }

    private void loadSavedSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Carregar os dados salvos e atribuir aos campos correspondentes
        editTextMac.setText(preferences.getString("mac_address", ""));
        editTextIp.setText(preferences.getString("ip_address", ""));
        editTextPort.setText(preferences.getString("port", ""));
    }

    // Função para validar o endereço IP
    private boolean isValidIpAddress(String ipAddress) {
        return !ipAddress.isEmpty(); // Adicione uma lógica de validação mais avançada, se necessário
    }

    // Função para validar a porta
    private boolean isValidPort(String port) {
        try {
            int portNumber = Integer.parseInt(port);
            return portNumber >= 1 && portNumber <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
