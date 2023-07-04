package com.example.olxclone.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.olxclone.activitys.MainActivity;
import com.example.olxclone.R;
import com.example.olxclone.helper.FirebaseHelper;
import com.example.olxclone.model.Usuario;

public class CriarContaActivity extends AppCompatActivity {

    private EditText edt_nome, edt_email, edt_telefone, edt_senha;
    private ProgressBar progressBar;
    private TextView text_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);

        iniciaComponentes();
    }

    public void validaDados(View view) {
        String nome = edt_nome.getText().toString();
        String email = edt_email.getText().toString();
        String telefone = edt_telefone.getText().toString();
        String senha = edt_senha.getText().toString();

        if (!nome.isEmpty()) {
            if (!email.isEmpty()) {
                if (!telefone.isEmpty()) {
                    if (!senha.isEmpty()) {

                        progressBar.setVisibility(View.VISIBLE);

                        Usuario usuario = new Usuario();
                        usuario.setNome(nome);
                        usuario.setEmail(email);
                        usuario.setTelefone(telefone);
                        usuario.setSenha(senha);

                        cadastrarUsuario(usuario);

                    } else {
                        edt_senha.requestFocus();
                        edt_senha.setError("Preencha sua senha");
                    }
                } else {
                    edt_telefone.requestFocus();
                    edt_telefone.setError("Preencha seu telefone");
                }
            } else {
                edt_email.requestFocus();
                edt_email.setError("Preencha seu email");
            }
        } else {
            edt_nome.requestFocus();
            edt_nome.setError("Preencha seu nome");
        }
    }

    private void cadastrarUsuario(Usuario usuario) {
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                String id = task.getResult().getUser().getUid();
                usuario.setId(id);
                usuario.salvar(progressBar, getBaseContext());

                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                FirebaseHelper.validaErros(task.getException().getMessage());
            }
            progressBar.setVisibility(View.GONE);
        });

        progressBar.setVisibility(View.GONE);
    }

    private void iniciaComponentes() {
        findViewById(R.id.ib_voltar).setOnClickListener(View -> finish());
        text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Criar conta");

        edt_nome = findViewById(R.id.edt_nome);
        edt_email = findViewById(R.id.edt_email);
        edt_telefone = findViewById(R.id.edt_telefone);
        edt_senha = findViewById(R.id.edt_senha);
        progressBar = findViewById(R.id.progressBar);
    }
}