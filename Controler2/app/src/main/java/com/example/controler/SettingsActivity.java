// SettingsActivity.java
package com.example.controler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controler.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    private EditText editTextMac, editTextIp, editTextPort, editTextProfileName;
    private ListView listViewProfiles;
    private ArrayAdapter<String> profilesAdapter;
    private Set<String> profilesSet;
    private String activeProfileName;

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
        editTextProfileName = findViewById(R.id.editTextProfileName);
        listViewProfiles = findViewById(R.id.listViewProfiles);

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
        saveButton.setOnClickListener(v -> saveProfile());

        // Carregar as configurações salvas
        loadSavedProfiles();
        loadActiveProfile();

        //------- Footer type shit , ignorar -------
        Button btnFooterHome = findViewById(R.id.btnFooterHome);
        Button btnFooterAdd = findViewById(R.id.btnFooterAdd);
        Button btnFooterSelectButtons = findViewById(R.id.btnFooterSelectButtons);
        btnFooterHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnFooterAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AdicionarBotaoActivity.class);
                startActivity(intent);
            }
        });
        btnFooterSelectButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SelecionarBotoesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveProfile() {
        String profileName = editTextProfileName.getText().toString();
        if (profileName.isEmpty()) {
            Toast.makeText(this, "Nome do perfil não pode estar vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        String macAddress = editTextMac.getText().toString();
        String ipAddress = editTextIp.getText().toString();
        String port = editTextPort.getText().toString();

        if (!isValidIpAddress(ipAddress)) {
            Toast.makeText(this, "Endereço IP inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPort(port)) {
            Toast.makeText(this, "Porta inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        String profileData = macAddress + "|||" + ipAddress + "|||" + port;
        editor.putString("profile_" + profileName, profileData);

        profilesSet.add(profileName);
        editor.putStringSet("profiles", profilesSet);
        editor.apply();

        // Atualizar a lista de perfis
        profilesAdapter.clear();
        profilesAdapter.addAll(profilesSet);
        profilesAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Perfil salvo com sucesso", Toast.LENGTH_SHORT).show();

        // Salvar o perfil ativo
        saveActiveProfile(profileName);
    }

    private void loadSavedProfiles() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        profilesSet = preferences.getStringSet("profiles", new HashSet<>());
        profilesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(profilesSet));
        listViewProfiles.setAdapter(profilesAdapter);

        listViewProfiles.setOnItemClickListener((parent, view, position, id) -> {
            String profileName = profilesAdapter.getItem(position);
            if (profileName != null) {
                loadProfile(profileName);
                saveActiveProfile(profileName);
            }
        });
    }

    private void loadProfile(String profileName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String profileData = preferences.getString("profile_" + profileName, "");

        Log.d("SettingsActivity", "NIFFAAA" );

        if (!profileData.isEmpty()) {
            String[] parts = profileData.split("\\|\\|\\|");
            if (parts.length == 3) {
                editTextMac.setText(parts[0]);
                editTextIp.setText(parts[1]);
                editTextPort.setText(parts[2]);
                editTextProfileName.setText(profileName);
                Toast.makeText(this, "Perfil " + profileName + " carregado", Toast.LENGTH_SHORT).show();

                // Adicionando logs para mostrar os dados carregados
                Log.d("SettingsActivity", "Perfil carregado: " + profileName);
                Log.d("SettingsActivity", "MAC: " + parts[0]);
                Log.d("SettingsActivity", "IP: " + parts[1]);
                Log.d("SettingsActivity", "Porta: " + parts[2]);
            }
        }
    }

    private void saveActiveProfile(String profileName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("perfil_ativo", profileName);
        editor.apply();
    }

    private void loadActiveProfile() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        activeProfileName = preferences.getString("perfil_ativo", null);
        if (activeProfileName != null) {
            loadProfile(activeProfileName);
        }
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