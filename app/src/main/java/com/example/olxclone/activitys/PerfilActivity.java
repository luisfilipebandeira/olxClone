package com.example.olxclone.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olxclone.R;
import com.example.olxclone.helper.FirebaseHelper;
import com.example.olxclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.santalu.maskara.widget.MaskEditText;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class PerfilActivity extends AppCompatActivity {

    private EditText edt_nome, edt_email;
    private MaskEditText edt_telefone;
    private ProgressBar progressBar;

    private Usuario usuario;

    private final int SELECAO_GALERIA = 100;

    ImageView image_perfil;

    private String caminhoImagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        iniciaComponentes();
        configCliques();
        recuperaPerfil();
    }

    private void iniciaComponentes() {
        TextView txt_toolbar = findViewById(R.id.text_toolbar);
        txt_toolbar.setText("Perfil");

        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());

        image_perfil = findViewById(R.id.imagem_perfil);
        edt_nome = findViewById(R.id.edt_nome);
        edt_email = findViewById(R.id.edt_email);
        edt_telefone = findViewById(R.id.edt_telefone);
        progressBar = findViewById(R.id.progressBar);
    }

    private void recuperaPerfil() {
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference perfilRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());

        perfilRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    usuario = snapshot.getValue(Usuario.class);
                    configDados();
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados() {
        edt_nome.setText(usuario.getNome());
        edt_telefone.setText(usuario.getTelefone());
        edt_email.setText(usuario.getEmail());

        progressBar.setVisibility(View.GONE);

        if (usuario.getImagemPerfil() != null ){
            Picasso.get().load(usuario.getImagemPerfil()).into(image_perfil);
        }
    }

    private void salvarImagemPerfil() {
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("perfil")
                .child(FirebaseHelper.getIdFirebase() + ".jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {

            String urlImagem = task.getResult().toString();
            usuario.setImagemPerfil(urlImagem);
            usuario.salvar(progressBar, getBaseContext());

        })).addOnFailureListener(e-> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void validaDados(View view) {
        String nome = edt_nome.getText().toString();
        String telefone = edt_telefone.getUnMasked();

        if (!nome.isEmpty()) {
            if (!telefone.isEmpty() && telefone.length() == 11) {

                progressBar.setVisibility(View.VISIBLE);

                if (caminhoImagem != null) {
                    salvarImagemPerfil();
                } else {
                    usuario.salvar(progressBar, getBaseContext());
                }

            } else {
                edt_telefone.requestFocus();
                edt_telefone.setError("Preencha seu telefone corretamente.");
            }
        } else {
            edt_nome.requestFocus();
            edt_nome.setError("Preencha seu nome");
        }
    }

    private void configCliques() {
        image_perfil.setOnClickListener(view -> verificaPermissaoGaleria());
    }

    private void verificaPermissaoGaleria() {
        abrirGaleria();

//        PermissionListener permissionlistener = new PermissionListener() {
//            @Override
//            public void onPermissionGranted() {
//                abrirGaleria();
//            }
//
//            @Override
//            public void onPermissionDenied(List<String> deniedPermissions) {
//                Toast.makeText(PerfilActivity.this, "Permissão negada", Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        TedPermission.create()
//                .setPermissionListener(permissionlistener)
//                .setDeniedTitle("Permissões negadas")
//                .setDeniedMessage("Se você não aceitar a permissão, não poderá acessar a galeria do dispositivo")
//                .setDeniedCloseButtonText("Não")
//                .setGotoSettingButtonText("Sim")
//                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .check();
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECAO_GALERIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri imageSelecionada = data.getData();
            Bitmap imagemRecuperada;

            try {
                imagemRecuperada = MediaStore.Images.Media.getBitmap(getContentResolver(), imageSelecionada);
                image_perfil.setImageBitmap(imagemRecuperada);

                caminhoImagem = imageSelecionada.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}