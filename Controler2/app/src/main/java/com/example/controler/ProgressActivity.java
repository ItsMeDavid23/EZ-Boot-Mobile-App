package com.example.controler;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.ImageButton;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ProgressActivity extends AppCompatActivity {
    private TextView statusTextView;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private String ordem;
    private boolean tipo;
    private Handler handler = new Handler();
    private AdView adView;
    private ViewGroup adContainerView;
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"; // Substitui pelo teu ID real

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view before finding views
        setContentView(R.layout.activity_progress);

        // Initialize Mobile Ads SDK
        MobileAds.initialize(this, initializationStatus -> Log.d(TAG, "SDK de anúncios inicializado."));

        adContainerView = findViewById(R.id.adContainerView);
        adView = new AdView(this);
        adView.setAdUnitId(AD_UNIT_ID);
        adView.setAdSize(getAdSize());
        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        statusTextView = findViewById(R.id.statusTextView);
        progressBar = findViewById(R.id.progressBar);

        // Load the ad
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Recupere os extras do Intent
        String macAddress = getIntent().getStringExtra("macAddress");
        String serverIp = getIntent().getStringExtra("serverIp");
        String serverPort = getIntent().getStringExtra("serverPort");
        ordem = getIntent().getStringExtra("ordem");
        tipo = getIntent().getBooleanExtra("tipo", false);
        Log.e(TAG, "Ordem que chegou ao progressactivity: " + ordem);

        progressBar.setProgress(progressStatus);

        // Perguntar ao usuário se será necessário efetuar login apenas se tipo for verdadeiro
        if (tipo) {
            perguntarSeNecessarioLogin(macAddress, serverIp, serverPort);
        } else {
            ordem += ",0,0,0,0";
            Log.e(TAG, "Ordem: " + ordem);
            new WakeOnLanTask().execute(macAddress, ordem, serverIp, serverPort);
        }

        // Apply window insets to the root view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Get the ad size with screen width.
    public AdSize getAdSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int adWidthPixels = displayMetrics.widthPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = this.getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        }

        float density = displayMetrics.density;
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void perguntarSeNecessarioLogin(String macAddress, String serverIp, String serverPort) {
        new AlertDialog.Builder(this)
                .setTitle("Login Necessário")
                .setMessage("Vai ser necessário efetuar login?")
                .setPositiveButton("Sim", (dialog, which) -> exibirCamposLogin(macAddress, serverIp, serverPort))
                .setNegativeButton("Não", (dialog, which) -> {
                    ordem += ",0,0,0,0";
                    Log.e(TAG, "Ordem: " + ordem);
                    new WakeOnLanTask().execute(macAddress, ordem, serverIp, serverPort);
                })
                .setNeutralButton("Cancelar", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void exibirCamposLogin(String macAddress, String serverIp, String serverPort) {
        // Crie um layout para os campos de entrada
        View loginView = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);
        EditText editTextUsername = loginView.findViewById(R.id.editTextUsername);
        EditText editTextPassword = loginView.findViewById(R.id.editTextPassword);
        CheckBox checkBoxSignedIn = loginView.findViewById(R.id.checkBoxSignedIn);


        new AlertDialog.Builder(this)
                .setTitle("Efetuar Login")
                .setView(loginView)
                .setPositiveButton("OK", (dialog, which) -> {
                    String username = editTextUsername.getText().toString();
                    String password = editTextPassword.getText().toString();
                    boolean keepSignedIn = checkBoxSignedIn.isChecked();
                    ordem += ",1," + username + "," + password + "," + (keepSignedIn ? "1" : "0");
                    Log.e(TAG, "Ordem: " + ordem);
                    new WakeOnLanTask().execute(macAddress, ordem, serverIp, serverPort);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private class WakeOnLanTask extends AsyncTask<String, String, String> {
        private static final int INTERVALO_TENTATIVA = 2000; // Intervalo de tentativa em milissegundos (5 segundos)
        private static final int DURACAO_TOTAL = 90000; // Duração total em milissegundos (1 minuto)

        @Override
        protected String doInBackground(String... params) {
            try {
                String macAddress = params[0];
                String ordem = params[1];
                String serverIp = params[2];
                String serverPort = params[3];

                if (macAddress != null && serverIp != null && serverPort != null) {
                    long startTime = System.currentTimeMillis();
                    long currentTime;
                    int tentativas = 0;

                    progressStatus = 33;
                    publishProgress("Tentando se conectar com o PC...");

                    while ((currentTime = System.currentTimeMillis()) - startTime < DURACAO_TOTAL) {
                        tentativas++;
                        String resultado = enviarPacoteWakeOnLan(macAddress, ordem, serverIp, serverPort);
                        if ("SUCESSO".equals(resultado)) {
                            return "SUCESSO";
                        }

                        // Aguardar o intervalo antes da próxima tentativa
                        Thread.sleep(INTERVALO_TENTATIVA);
                    }

                    exibirToast("ERRO: Tempo limite atingido após " + tentativas + " tentativas.");

                    return "ERRO: Tempo limite atingido";
                } else {
                    exibirToast("Parâmetros nulos. macAddress: " + macAddress + ", serverIp: " + serverIp + ", serverPort: " + serverPort);

                    return "ERRO";
                }
            } catch (Exception e) {
                exibirToast("Erro doInBackground: " + e.getMessage());

                return "ERRO";
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            statusTextView.setText(values[0]);
            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progressStatus);
            progressAnimator.setDuration(500); // duração da animação em milissegundos
            progressAnimator.start();
        }

        private String enviarPacoteWakeOnLan(String macAddress, String ordem, String serverIp, String serverPort) {
            try {
                byte[] macBytes = getMacBytes(macAddress);
                byte[] magicPacket = new byte[6 + 16 * macBytes.length];
                for (int i = 0; i < 6; i++) {
                    magicPacket[i] = (byte) 0xFF;
                }
                for (int i = 6; i < magicPacket.length; i += macBytes.length) {
                    System.arraycopy(macBytes, 0, magicPacket, i, macBytes.length);
                }

                InetAddress address = InetAddress.getByName("255.255.255.255");
                DatagramPacket packet = new DatagramPacket(magicPacket, magicPacket.length, address, 9);

                DatagramSocket socket = new DatagramSocket();
                socket.send(packet);
                socket.close();

                Log.d(TAG, "Pacote Wake-on-LAN enviado para: " + macAddress);
                runOnUiThread(() -> Toast.makeText(ProgressActivity.this, "Pacote Wake-on-LAN enviado para: " + macAddress, Toast.LENGTH_SHORT).show());

                // Conectar ao servidor para enviar a ordem e receber a resposta do servidor (2 respostas)
                try (Socket serverSocket = new Socket(serverIp, Integer.parseInt(serverPort));
                     DataOutputStream dos = new DataOutputStream(serverSocket.getOutputStream());
                     BufferedReader br = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()))) {
                    //faz um log da ordem que vai ser enviada
                    Log.d(TAG, "Ordem a ser enviada: " + ordem);
                    dos.writeUTF(ordem);

                    // Lê a primeira resposta do servidor
                    String primeiraResposta = br.readLine();
                    System.out.println("Primeira resposta do servidor :"+primeiraResposta);

                    if ("Information received successfully".equals(primeiraResposta)) {
                        progressStatus = 66;
                        publishProgress("A abrir " + ordem + "...");

                        // Lê a segunda resposta do servidor
                        String segundaResposta = br.readLine();
                        System.out.println("Segunda resposta do servidor :"+segundaResposta);

                        if ("Order completed successfully".equals(segundaResposta)) {
                            progressStatus = 100;
                            publishProgress("Ordem completada com sucesso!");
                            return "SUCESSO";
                        } else {
                            Log.e(TAG, "Resposta inesperada do servidor: " + segundaResposta);
                            return "ERRO";
                        }
                    } else {
                        Log.e(TAG, "Resposta inesperada do servidor: " + primeiraResposta);
                        return "ERRO";
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Erro de E/S ao tentar conectar ao servidor: " + e.getMessage());
                    return "ERRO";
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro enviarPacoteWakeOnLan: " + e.getMessage());
                return "ERRO";
            }
        }

        private byte[] getMacBytes(String macStr) {
            byte[] macBytes = new byte[6];
            String[] hex = macStr.split("([:\\-])");
            for (int i = 0; i < 6; i++) {
                macBytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
            return macBytes;
        }

        private void exibirToast(String mensagem) {
            runOnUiThread(() -> Toast.makeText(ProgressActivity.this, mensagem, Toast.LENGTH_SHORT).show());
        }
    }
}