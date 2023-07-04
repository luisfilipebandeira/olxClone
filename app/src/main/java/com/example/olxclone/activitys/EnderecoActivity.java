package com.example.olxclone.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.olxclone.R;
import com.example.olxclone.helper.FirebaseHelper;
import com.example.olxclone.model.Endereco;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class EnderecoActivity extends AppCompatActivity {

    private EditText edt_cep, edt_uf, edt_municipio, edt_bairro;
    private ProgressBar progressBar;

    private Endereco endereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);

        iniciaComponentes();
        recuperaEndereco();
    }

    private void iniciaComponentes() {
        TextView text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Endereço");

        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());

        edt_cep = findViewById(R.id.edt_cep);
        edt_uf = findViewById(R.id.edt_uf);
        edt_municipio = findViewById(R.id.edt_municipio);
        edt_bairro = findViewById(R.id.edt_bairro);
        progressBar = findViewById(R.id.progressBar);
    }

    private void recuperaEndereco() {

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference enderecoRef = FirebaseHelper.getDatabaseReference()
                .child("enderecos")
                .child(FirebaseHelper.getIdFirebase());
        enderecoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    endereco = snapshot.getValue(Endereco.class);
                    configEndereco(endereco);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configEndereco(Endereco endereco) {
        edt_cep.setText(endereco.getCep());
        edt_uf.setText(endereco.getUf());
        edt_municipio.setText(endereco.getMunicipio());
        edt_bairro.setText(endereco.getBairro());

        progressBar.setVisibility(View.GONE);
    }

    public void validaDadosEndereco(View view) {
        String cep = edt_cep.getText().toString();
        String uf = edt_uf.getText().toString();
        String municipio = edt_municipio.getText().toString();
        String bairro = edt_bairro.getText().toString();

        if (!cep.isEmpty()) {
            if (!uf.isEmpty()) {
                if (!municipio.isEmpty()) {
                    if (!bairro.isEmpty()) {
                        progressBar.setVisibility(View.VISIBLE);

                        if (endereco == null) endereco = new Endereco();
                        Endereco endereco = new Endereco();
                        endereco.setCep(cep);
                        endereco.setUf(uf);
                        endereco.setBairro(bairro);
                        endereco.setMunicipio(municipio);
                        endereco.salvar(FirebaseHelper.getIdFirebase(), getBaseContext(), progressBar);

                        finish();

                    } else {
                        edt_bairro.requestFocus();
                        edt_bairro.setError("Preencha seu bairro.");
                    }
                } else {
                    edt_municipio.requestFocus();
                    edt_municipio.setError("Preencha seu municipio.");
                }
            } else {
                edt_uf.requestFocus();
                edt_uf.setError("Preencha sua UF.");
            }
        } else {
            edt_cep.requestFocus();
            edt_cep.setError("Preencha seu CEP.");
        }
    }
}