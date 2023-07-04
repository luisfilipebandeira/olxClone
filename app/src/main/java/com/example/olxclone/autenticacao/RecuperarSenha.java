package com.example.olxclone.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olxclone.R;
import com.example.olxclone.helper.FirebaseHelper;

public class RecuperarSenha extends AppCompatActivity {

    private EditText edt_email;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        iniciaComponentes();
    }

    public void validaDados(View view) {
        String email = edt_email.getText().toString();

        if (!email.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);

            enviarEmail(email);
        } else {
            edt_email.requestFocus();
            edt_email.setError("Preencha seu email.");
        }
    }

    private void enviarEmail(String email) {
        FirebaseHelper.getAuth().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Verifique seu email", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseHelper.validaErros(task.getException().getMessage());
            }

            progressBar.setVisibility(View.GONE);
        });
    }

    private void iniciaComponentes() {
        findViewById(R.id.ib_voltar).setOnClickListener(View -> finish());
        TextView text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Recuperar senha");

        edt_email = findViewById(R.id.edt_email);
        progressBar = findViewById(R.id.progressBar);
    }
}